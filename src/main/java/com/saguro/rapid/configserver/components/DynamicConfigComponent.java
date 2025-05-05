package com.saguro.rapid.configserver.components;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DynamicConfigComponent.class);

    private ApplicationService applicationService;
    private VaultDynamicConfig vaultDynamicConfig;

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(".properties", ".yml", ".yaml");

    public DynamicConfigComponent(ApplicationService applicationService, VaultDynamicConfig vaultDynamicConfig) {
        this.applicationService = applicationService;
        this.vaultDynamicConfig = vaultDynamicConfig;
    }

    public Environment findOne(String application, String profile, String label) {
        logger.info("Fetching configuration for application: {}, profile: {}, label: {}", application, profile, label);

        String[] pathParts = application.split("/");

        if (pathParts.length != 3) {
            logger.error("Invalid application path format: {}. Expected format: {organization}/{application}/{microservice}", application);
            throw new IllegalArgumentException("The path must follow the format {organization}/{application}/{microservice}");
        }

        String uidOrg = pathParts[0];
        String uidApp = pathParts[1];
        String microservice = pathParts[2];

        Optional<Application> applicationOpt = applicationService.findByOrganizationAndUidAndMicroservice(uidOrg, uidApp, label);

        if (applicationOpt.isEmpty()) {
            logger.warn("No application found for organization: {}, application: {}, label: {}", uidOrg, uidApp, label);
        }

        return applicationOpt.map(appEntity -> buildEnvironment(appEntity, profile, microservice, label)).orElse(null);
    }

    private Environment buildEnvironment(Application appEntity, String profile, String microservice, String label) {
        logger.info("Building environment for application: {}, profile: {}, microservice: {}", appEntity.getName(), profile, microservice);

        Environment environment = new Environment(microservice, profile);

        // Load default properties
        loadDefaultProperties(environment, appEntity, microservice, label);

        // Load profile-specific properties if not "default"
        if (!profile.equals("default")) {
            loadProfileProperties(environment, appEntity, microservice, profile, label);
        }

        // Load Vault properties if enabled
        loadVaultProperties(environment, appEntity, microservice);

        return environment;
    }

    private void loadDefaultProperties(Environment environment, Application appEntity, String microservice, String label) {
        logger.info("Loading default properties for application: {}, microservice: {}", appEntity.getName(), microservice);
        Environment defaultEnvironment = loadConfigFromApplication(appEntity, microservice, "default", label);
        if (defaultEnvironment != null) {
            environment.getPropertySources().addAll(defaultEnvironment.getPropertySources());
        } else {
            logger.warn("No default properties found for application: {}, microservice: {}", appEntity.getName(), microservice);
        }
    }

    private void loadProfileProperties(Environment environment, Application appEntity, String microservice, String profile, String label) {
        logger.info("Loading profile-specific properties for application: {}, profile: {}, microservice: {}", appEntity.getName(), profile, microservice);
        Environment profileEnvironment = loadConfigFromApplication(appEntity, microservice, profile, label);
        if (profileEnvironment != null) {
            environment.getPropertySources().addAll(profileEnvironment.getPropertySources());
        } else {
            logger.warn("No properties found for profile: {} in application: {}, microservice: {}", profile, appEntity.getName(), microservice);
        }
    }

    private void loadVaultProperties(Environment environment, Application appEntity, String microservice) {
        if (appEntity.isVaultEnabled()) {
            logger.info("Loading Vault properties for application: {}, microservice: {}", appEntity.getName(), microservice);
            Map<String, Object> vaultProperties = vaultDynamicConfig.configureAndFetchVaultProperties(appEntity, microservice);
            if (!vaultProperties.isEmpty()) {
                PropertySource propertyVault = new PropertySource("vault", vaultProperties);
                environment.add(propertyVault);
                logger.info("Vault properties successfully loaded for application: {}, microservice: {}", appEntity.getName(), microservice);
            } else {
                logger.warn("No Vault properties found for application: {}, microservice: {}", appEntity.getName(), microservice);
            }
        } else {
            logger.info("Vault is disabled for application: {}", appEntity.getName());
        }
    }

    private Environment loadConfigFromApplication(Application appEntity, String application, String profile, String label) {
        try {
            logger.info("Loading configuration from Git for application: {}, profile: {}, label: {}", application, profile, label);

            if (label.contains("..") || label.contains("/") || label.contains("\\")) {
                logger.error("Invalid label: {}", label);
                throw new IllegalArgumentException("Invalid filename");
            }
            if (application.contains("..") || application.contains("/") || application.contains("\\")) {
                logger.error("Invalid application name: {}", application);
                throw new IllegalArgumentException("Invalid filename");
            }

            File tempDir = prepareTemporaryDirectory(label);
            Git git = cloneRepository(appEntity, tempDir, label);

            File configFile = findConfigFile(tempDir, application, profile);

            return getPropertySource(application, profile, git, configFile, appEntity.getUri());
        } catch (IOException e) {
            logger.error("Error handling configuration file for application: {}, profile: {}, label: {}", application, profile, label, e);
            throw new RuntimeException("Error handling configuration file", e);
        } catch (GitAPIException e) {
            logger.error("Error cloning Git repository for application: {}, profile: {}, label: {}", application, profile, label, e);
            throw new RuntimeException("Error cloning Git repository", e);
        }
    }

    private File prepareTemporaryDirectory(String label) throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "config-repo-" + label);
        if (tempDir.exists()) {
            deleteDirectory(tempDir);
        }
        logger.debug("Temporary directory prepared: {}", tempDir.getAbsolutePath());
        return tempDir;
    }

    private Git cloneRepository(Application appEntity, File tempDir, String branch) throws GitAPIException {
        logger.info("Cloning Git repository for application: {}, branch: {}", appEntity.getName(), branch);
        return Git.cloneRepository()
                .setURI(appEntity.getUri())
                .setDirectory(tempDir)
                .setBranch(branch)
                .call();
    }

    private File findConfigFile(File tempDir, String application, String profile) {
        logger.debug("Searching for configuration file in temporary directory: {}", tempDir.getAbsolutePath());
        if (profile.equals("default")) {
            for (String ext : SUPPORTED_EXTENSIONS) {
                File configFile = new File(tempDir, application + ext);
                if (configFile.exists()) {
                    logger.info("Configuration file found: {}", configFile.getAbsolutePath());
                    return configFile;
                }
            }
        } else {
            for (String ext : SUPPORTED_EXTENSIONS) {
                File configFile = new File(tempDir, application + "-" + profile + ext);
                if (configFile.exists()) {
                    logger.info("Configuration file found: {}", configFile.getAbsolutePath());
                    return configFile;
                }
            }
        }
        logger.warn("No configuration file found for application: {}, profile: {}", application, profile);
        return null;
    }

    private Environment getPropertySource(String application, String profile, Git git, File configFile, String urlGit) throws IOException {
        Environment environment = new Environment(application, profile);
        if (validateConfigFile(configFile)) {
            String fileExtension = getFileExtension(configFile);
            if (!SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                logger.error("Unsupported file extension: {}", fileExtension);
                throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
            }
            Map<String, Object> properties = loadConfigFile(configFile, fileExtension);
            PropertySource propertySource = new PropertySource(urlGit, properties);
            environment.add(propertySource);
            logger.info("Properties successfully loaded from file: {}", configFile.getAbsolutePath());
        } else {
            logger.warn("Invalid or missing configuration file: {}", configFile);
        }
        try (git) {
            return environment;
        }
    }

    private boolean validateConfigFile(File configFile) {
        boolean isValid = configFile != null && configFile.exists();
        if (!isValid) {
            logger.warn("Configuration file is invalid or does not exist: {}", configFile);
        }
        return isValid;
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            logger.error("File does not have an extension: {}", fileName);
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
            logger.error("Unsupported file extension: {}", fileExtension);
            throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
        }
    }

    private Map<String, Object> loadPropertiesFile(File configFile) throws IOException {
        logger.debug("Loading properties file: {}", configFile.getAbsolutePath());
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }

        Map<String, Object> result = new java.util.HashMap<>();
        properties.forEach((key, value) -> result.put(key.toString(), value));
        return result;
    }

    private Map<String, Object> loadYamlFile(File configFile) throws IOException {
        logger.debug("Loading YAML file: {}", configFile.getAbsolutePath());
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
                        logger.debug("Deleting file: {}", file.getAbsolutePath());
                        file.delete();
                    }
                }
            }
            logger.debug("Deleting directory: {}", directory.getAbsolutePath());
            directory.delete();
        }
    }
}