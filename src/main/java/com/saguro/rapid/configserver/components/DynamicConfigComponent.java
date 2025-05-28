package com.saguro.rapid.configserver.components;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.service.ApplicationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

@Component
public class DynamicConfigComponent {

    private ApplicationService applicationService;
    private VaultDynamicConfig vaultDynamicConfig;

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(".properties", ".yml", ".yaml");

    public DynamicConfigComponent(ApplicationService applicationService, VaultDynamicConfig vaultDynamicConfig) {
        this.applicationService = applicationService;
        this.vaultDynamicConfig = vaultDynamicConfig;
    }

    public Environment findOne(String application, String profile, String label) {
        String[] pathParts = application.split("/");

        if (pathParts.length != 3) {
            throw new IllegalArgumentException("La ruta debe seguir el formato {organization}/{application}/{microservice}");
        }

        String uidOrg = pathParts[0];
        String uidApp = pathParts[1];
        String microservice = pathParts[2];

        Optional<Application> applicationOpt = applicationService.findByOrganizationAndUidAndMicroservice(uidOrg, uidApp, label);

        return applicationOpt.map(appEntity -> buildEnvironment(appEntity, profile, microservice, label)).orElse(null);
    }

    private Environment buildEnvironment(Application appEntity, String profile, String microservice, String label) {
        Environment environment = new Environment(microservice, profile);

        // Cargar propiedades del perfil "default"
        loadDefaultProperties(environment, appEntity, microservice, label);

        // Cargar propiedades del perfil específico si no es "default"
        if (!profile.equals("default")) {
            loadProfileProperties(environment, appEntity, microservice, profile, label);
        }

        // Cargar propiedades desde Vault si está habilitado
        loadVaultProperties(environment, appEntity, microservice);

        return environment;
    }

    private void loadDefaultProperties(Environment environment, Application appEntity, String microservice, String label) {
        Environment defaultEnvironment = loadConfigFromApplication(appEntity, microservice, "default", label);
        if (defaultEnvironment != null) {
            environment.getPropertySources().addAll(defaultEnvironment.getPropertySources());
        }
    }

    private void loadProfileProperties(Environment environment, Application appEntity, String microservice, String profile, String label) {
        Environment profileEnvironment = loadConfigFromApplication(appEntity, microservice, profile, label);
        if (profileEnvironment != null) {
            environment.getPropertySources().addAll(profileEnvironment.getPropertySources());
        }
    }

    private void loadVaultProperties(Environment environment, Application appEntity, String microservice) {
        if (appEntity.isVaultEnabled()) {
            Map<String, Object> vaultProperties = vaultDynamicConfig.configureAndFetchVaultProperties(appEntity, microservice);
            if (!vaultProperties.isEmpty()) {
                PropertySource propertyVault = new PropertySource("vault", vaultProperties);
                environment.add(propertyVault);
            }
        }
    }

    private Environment loadConfigFromApplication(Application appEntity, String application, String profile, String label) {
        try {
            if (label.contains("..") || label.contains("/") || label.contains("\\")) {
                throw new IllegalArgumentException("Invalid filename");
            }
            if (application.contains("..") || application.contains("/") || application.contains("\\")) {
                throw new IllegalArgumentException("Invalid filename");
            }
            if (profile.contains("..") || profile.contains("/") || profile.contains("\\")) {
                throw new IllegalArgumentException("Invalid profile name");
            }
            File tempDir = prepareTemporaryDirectory(label);
            Git git = cloneRepository(appEntity, tempDir, label);

            File configFile = findConfigFile(tempDir, application, profile);

            return getPropertySource(application, profile, git, configFile, appEntity.getUri());
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

    private Git cloneRepository(Application appEntity, File tempDir, String branch) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(appEntity.getUri())
                .setDirectory(tempDir)
                .setBranch(branch)
                .call();
    }

    private File findConfigFile(File tempDir, String application, String profile) {
        File controlledDir = tempDir.getAbsoluteFile();
        if (profile.equals("default")) {
            for (String ext : SUPPORTED_EXTENSIONS) {
                File configFile = new File(controlledDir, application + ext);
                if (configFile.exists()) {
                    return configFile;
                }
            }
        } else {
            for (String ext : SUPPORTED_EXTENSIONS) {
                File configFile = new File(controlledDir, application + "-" + profile + ext);
                if (configFile.exists()) {
                    return configFile;
                }
            }
        }
        return null;
    }

    private Environment getPropertySource(String application, String profile, Git git, File configFile, String urlGit) throws IOException {
        Environment environment = new Environment(application, profile);
        if (validateConfigFile(configFile)) {
            String fileExtension = getFileExtension(configFile);
            if (!SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
            }
            Map<String, Object> properties = loadConfigFile(configFile, fileExtension);
            PropertySource propertySource = new PropertySource(urlGit, properties);
            environment.add(propertySource);
        }
        try (git) {
            return environment;
        }
    }

    private boolean validateConfigFile(File configFile) {
        return configFile != null && configFile.exists();
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

    private Map<String, Object> loadPropertiesFile(File configFile) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }

        Map<String, Object> result = new java.util.HashMap<>();
        properties.forEach((key, value) -> result.put(key.toString(), value));
        return result;
    }

    private Map<String, Object> loadYamlFile(File configFile) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            Map<String, Object> yamlData = yaml.load(fis);

            Map<String, Object> properties = new java.util.HashMap<>();
            flattenYaml("", yamlData, properties);

            return properties;
        }
    }

    private void flattenYaml(String parentKey, Map<String, Object> yamlData, Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
            String key = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                flattenYaml(key, nestedMap, properties);
            } else {
                properties.put(key, value);
            }
        }
    }

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