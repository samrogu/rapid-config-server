package com.saguro.rapid.configserver.components;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.service.ApplicationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

@Component
public class DynamicConfigComponent {

    private static final Logger logger = LoggerFactory.getLogger(DynamicConfigComponent.class);
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(".properties", ".yml", ".yaml");

    private final ApplicationService applicationService;
    private final VaultDynamicConfig vaultDynamicConfig;

    public DynamicConfigComponent(ApplicationService applicationService, VaultDynamicConfig vaultDynamicConfig) {
        this.applicationService = applicationService;
        this.vaultDynamicConfig = vaultDynamicConfig;
    }

    public Environment findOne(String application, String profile, String label) {
        logger.info("Fetching configuration for application: {}, profile: {}, label: {}", application, profile, label);

        String[] pathParts = application.split("/");
        if (pathParts.length != 3) {
            logger.error(
                    "Invalid application path format: {}. Expected format: {organization}/{application}/{microservice}",
                    application);
            throw new IllegalArgumentException(
                    "The path must follow the format {organization}/{application}/{microservice}");
        }

        String uidOrg = pathParts[0];
        String uidApp = pathParts[1];
        String microservice = pathParts[2];

        Optional<Application> applicationOpt = applicationService.findByOrganizationAndUidAndMicroservice(uidOrg,
                uidApp, label);

        if (applicationOpt.isEmpty()) {
            logger.warn("No application found for organization: {}, application: {}, label: {}", uidOrg, uidApp, label);
            return null;
        }

        return buildEnvironment(applicationOpt.get(), profile, microservice, label);
    }

    private Environment buildEnvironment(Application appEntity, String profile, String microservice, String label) {
        logger.info("Building environment for application: {}, profile: {}, microservice: {}", appEntity.getName(),
                profile, microservice);

        Environment environment = new Environment(microservice, profile);
        File tempDir = null;

        try {
            tempDir = prepareTemporaryDirectory(label);
            Git git = cloneRepository(appEntity, tempDir, label);
            // We don't need to keep the Git object open, just the files.
            // But Git implements AutoCloseable, so we should close it.
            try (git) {
                // Load default properties
                loadDefaultProperties(environment, appEntity, microservice, tempDir);

                // Load profile-specific properties if not "default"
                if (!"default".equals(profile)) {
                    loadProfileProperties(environment, appEntity, microservice, profile, tempDir);
                }
            }

            // Load Vault properties if enabled
            loadVaultProperties(environment, appEntity, microservice);

        } catch (Exception e) {
            logger.error("Error building environment", e);
            throw new RuntimeException("Error building environment", e);
        } finally {
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }

        return environment;
    }

    private void loadDefaultProperties(Environment environment, Application appEntity, String microservice,
            File tempDir) {
        logger.info("Loading default properties for application: {}, microservice: {}", appEntity.getName(),
                microservice);
        Environment defaultEnvironment = loadConfigFromDir(appEntity, microservice, "default", tempDir);
        if (defaultEnvironment != null) {
            environment.getPropertySources().addAll(defaultEnvironment.getPropertySources());
        } else {
            logger.warn("No default properties found for application: {}, microservice: {}", appEntity.getName(),
                    microservice);
        }
    }

    private void loadProfileProperties(Environment environment, Application appEntity, String microservice,
            String profile, File tempDir) {
        logger.info("Loading profile-specific properties for application: {}, profile: {}, microservice: {}",
                appEntity.getName(), profile, microservice);
        Environment profileEnvironment = loadConfigFromDir(appEntity, microservice, profile, tempDir);
        if (profileEnvironment != null) {
            environment.getPropertySources().addAll(profileEnvironment.getPropertySources());
        } else {
            logger.warn("No properties found for profile: {} in application: {}, microservice: {}", profile,
                    appEntity.getName(), microservice);
        }
    }

    private void loadVaultProperties(Environment environment, Application appEntity, String microservice) {
        if (appEntity.isVaultEnabled()) {
            logger.info("Loading Vault properties for application: {}, microservice: {}", appEntity.getName(),
                    microservice);
            Map<String, Object> vaultProperties = vaultDynamicConfig.configureAndFetchVaultProperties(appEntity,
                    microservice);
            if (!vaultProperties.isEmpty()) {
                PropertySource propertyVault = new PropertySource("vault", vaultProperties);
                environment.add(propertyVault);
                logger.info("Vault properties successfully loaded for application: {}, microservice: {}",
                        appEntity.getName(), microservice);
            } else {
                logger.warn("No Vault properties found for application: {}, microservice: {}", appEntity.getName(),
                        microservice);
            }
        } else {
            logger.info("Vault is disabled for application: {}", appEntity.getName());
        }
    }

    private Environment loadConfigFromDir(Application appEntity, String application, String profile, File tempDir) {
        File configFile = findConfigFile(tempDir, application, profile);
        if (configFile == null) {
            return null;
        }

        Environment environment = new Environment(application, profile);
        try {
            if (validateConfigFile(configFile)) {
                String fileExtension = getFileExtension(configFile);
                Map<String, Object> properties = loadConfigFile(configFile, fileExtension);
                PropertySource propertySource = new PropertySource(appEntity.getUri(), properties);
                environment.add(propertySource);
                logger.info("Properties successfully loaded from file: {}", configFile.getAbsolutePath());
                return environment;
            }
        } catch (IOException e) {
            logger.error("Error loading config file", e);
        }
        return null;
    }

    private File prepareTemporaryDirectory(String label) throws IOException {
        // Create a unique temporary directory to avoid race conditions
        java.nio.file.attribute.FileAttribute<Set<java.nio.file.attribute.PosixFilePermission>> attrs = java.nio.file.attribute.PosixFilePermissions
                .asFileAttribute(
                        java.nio.file.attribute.PosixFilePermissions.fromString("rwx------"));

        File tempDir = Files.createTempDirectory("config-repo-" + label, attrs).toFile();
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
        File controlledDir = tempDir.getAbsoluteFile();
        String suffix = "default".equals(profile) ? "" : "-" + profile;

        for (String ext : SUPPORTED_EXTENSIONS) {
            File configFile = new File(controlledDir, application + suffix + ext);
            if (configFile.exists()) {
                logger.info("Configuration file found: {}", configFile.getAbsolutePath());
                return configFile;
            }
        }

        logger.warn("No configuration file found for application: {}, profile: {}", application, profile);
        return null;
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
        logger.debug("Loading properties file: {}", configFile.getAbsolutePath());
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }
        // Use HashMap instead of Properties (which is Hashtable)
        Map<String, Object> result = new java.util.HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            result.put(key, properties.getProperty(key));
        }
        return result;
    }

    private Map<String, Object> loadYamlFile(File configFile) {
        logger.debug("Loading YAML file: {}", configFile.getAbsolutePath());
        YamlMapFactoryBean factory = new YamlMapFactoryBean();
        factory.setResources(new FileSystemResource(configFile));
        Map<String, Object> map = factory.getObject();
        return map != null ? map : Collections.emptyMap();
    }

    private void deleteDirectory(File directory) {
        try {
            FileSystemUtils.deleteRecursively(directory);
            logger.debug("Deleted directory: {}", directory.getAbsolutePath());
        } catch (Exception e) {
            logger.warn("Failed to delete directory: {}", directory.getAbsolutePath(), e);
        }
    }
}