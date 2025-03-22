package com.saguro.rapid.configserver.components;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import com.saguro.rapid.configserver.entity.GitRepository;
import com.saguro.rapid.configserver.service.GitRepositoryService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;



@Component
public class DynamicConfigComponent implements EnvironmentRepository{

    private GitRepositoryService gitRepositoryService;

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(".properties", ".yml", ".yaml");

    public DynamicConfigComponent(GitRepositoryService gitRepositoryService) {
        // Puedes configurar la URI base de tu servidor de repositorios si es necesario
        this.gitRepositoryService = gitRepositoryService;
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        // Asumiendo que application es la ruta completa con la organización, aplicación y microservicio
        String[] pathParts = application.split("/");

        if (pathParts.length != 3) {
            throw new IllegalArgumentException("La ruta debe seguir el formato {organization}/{application}/{microservice}");
        }

        String organization = pathParts[0];
        String app = pathParts[1];
        String microservice = pathParts[2];

        // Obtener los repositorios según la organización, aplicación y microservicio
        Optional<GitRepository> repositoryOpt = gitRepositoryService.getRepositoriesByOrganizationApp(organization, app).stream()
            .filter(repo -> shouldUseRepositoryForConfig(repo, profile, label))
            .findFirst();

        return repositoryOpt.map(repo -> loadConfigFromRepository(repo, microservice, profile, label))
                            .orElse(null);
    }

    private boolean shouldUseRepositoryForConfig(GitRepository repo, String profile, String label) {
        // Comprobar si el perfil y la etiqueta coinciden con los del repositorio
        return repo.getProfile().equalsIgnoreCase(profile) && repo.getLabel().equalsIgnoreCase(label);
    }


    private Environment loadConfigFromRepository(GitRepository repo, String application, String profile, String label) {
        try {
            // Clonar el repositorio en un directorio temporal
            File tempDir = prepareTemporaryDirectory(label);
            Git git = cloneRepository(repo, tempDir, label);

            // Buscar el archivo de configuración
            File configFile = findConfigFile(tempDir, application, profile);

            // Validar que el archivo exista
            validateConfigFile(configFile);

            // Cargar el entorno desde el archivo de configuración
            return getPropertySource(application, profile, git, configFile);

        } catch (IOException e) {
            throw new RuntimeException("Error al manejar el archivo de configuración", e);
        } catch (GitAPIException e) {
            throw new RuntimeException("Error al clonar el repositorio Git", e);
        }
    }

    private File prepareTemporaryDirectory(String label) throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "config-repo-" + label);
        if (tempDir.exists()) {
            deleteDirectory(tempDir);
        }
        return tempDir;
    }

    private Git cloneRepository(GitRepository repo, File tempDir, String branch) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(repo.getUri())
                .setDirectory(tempDir)
                .setBranch(branch) // Asegura que estamos utilizando la rama correcta
                .call();
    }

    private File findConfigFile(File tempDir, String application, String profile) {
        String configFileName = application + "-" + profile;
        for (String ext : SUPPORTED_EXTENSIONS) {
            File configFile = new File(tempDir, configFileName + ext);
            if (configFile.exists()) {
                return configFile;
            }
        }
        return null;
    }

    private void validateConfigFile(File configFile) throws IOException {
        if (configFile == null || !configFile.exists()) {
            throw new IOException("No se encontró el archivo de configuración en la ruta esperada.");
        }
    }

    private Environment getPropertySource(String application, String profile, Git git, File configFile) throws IOException {
        // Obtener la extensión del archivo
        String fileExtension = getFileExtension(configFile);

        // Validar si la extensión es soportada
        if (!SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
        }

        // Cargar las propiedades según el tipo de archivo
        Map<String, Object> properties = loadConfigFile(configFile, fileExtension);

        // Crear el entorno y agregar las propiedades
        Environment environment = new Environment(application, profile);
        PropertySource propertySource = new PropertySource("dynamicConfig", properties);
        environment.add(propertySource);

        // Usar try-with-resources para cerrar el repositorio Git
        try (git) {
            return environment;
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("File does not have an extension: " + fileName);
        }
        return fileName.substring(lastDotIndex);
    }

    private Map<String, Object> loadConfigFile(File configFile, String fileExtension) throws IOException {
        if (fileExtension.equalsIgnoreCase(".properties")) {
            return loadPropertiesFile(configFile);
        } else if (fileExtension.equalsIgnoreCase(".yml") || fileExtension.equalsIgnoreCase(".yaml")) {
            return loadYamlFile(configFile);
        } else {
            throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
        }
    }

    // Método para cargar el archivo de propiedades
    private Map<String, Object> loadPropertiesFile(File configFile) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }
    
        Map<String, Object> result = new java.util.HashMap<>();
        properties.forEach((key, value) -> result.put(key.toString(), value));
        return result;  // Convertir el objeto Properties a Map para usarlo en Spring Cloud Config
    }

    // Método para cargar el archivo YAML
    private Map<String, Object> loadYamlFile(File configFile) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            // Cargar el YAML como un mapa genérico
            Map<String, Object> yamlData = yaml.load(fis);

            // Convertir el mapa jerárquico en un formato plano (propiedades)
            Map<String, Object> properties = new java.util.HashMap<>();
            flattenYaml("", yamlData, properties);

            return properties;
        }
    }

    // Método recursivo para aplanar el YAML
    private void flattenYaml(String parentKey, Map<String, Object> yamlData, Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
            String key = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // Si el valor es un mapa, llamar recursivamente
                if (value instanceof Map<?, ?>) {
                    if (value instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> nestedMap = (Map<String, Object>) value;
                        flattenYaml(key, nestedMap, properties);
                    }
                }
            } else {
                // Si el valor es un dato simple, agregarlo al mapa de propiedades
                properties.put(key, value);
            }
        }
    }

    // Método para eliminar un directorio y su contenido
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
    
}