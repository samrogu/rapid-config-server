package com.saguro.rapid.configserver.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.*;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.enums.VaultAuthMethod;

import java.util.Collections;
import java.util.Map;

@Component
public class VaultDynamicConfig {

    private static final Logger logger = LoggerFactory.getLogger(VaultDynamicConfig.class);

    public Map<String, Object> configureAndFetchVaultProperties(Application app, String microservice) {
        logger.info("Starting Vault configuration for application: {} and microservice: {}", app.getName(), microservice);

        if (!app.isVaultEnabled()) {
            logger.warn("Vault is disabled for application: {}. Returning empty properties.", app.getName());
            return Collections.emptyMap();
        }

        VaultEndpoint vaultEndpoint = VaultEndpoint.create(app.getVaultUrl(), 8200);
        vaultEndpoint.setScheme("http"); // Adjust the scheme if necessary
        logger.debug("Vault endpoint created: {}", vaultEndpoint);

        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = getClientAuthentication(app);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to configure Vault authentication for application: {}. Error: {}", app.getName(), e.getMessage());
            throw e;
        }

        String secretPath = String.format("%s/%s/%s", app.getSecretEngine(), "data", microservice);
        logger.info("Fetching secrets from Vault at path: {}", secretPath);

        VaultResponse response = new VaultTemplate(vaultEndpoint, clientAuthentication).read(secretPath);

        if (response != null && response.getData() != null) {
            Map<String, Object> responseData = response.getData();
            Object data = (responseData != null) ? responseData.get("data") : null;

            if (data instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) data;
                logger.info("Successfully fetched secrets from Vault for application: {} and microservice: {}", app.getName(), microservice);
                return result;
            }
        }

        logger.warn("No secrets found in Vault for application: {} and microservice: {}", app.getName(), microservice);
        return Collections.emptyMap();
    }

    private ClientAuthentication getClientAuthentication(Application app) {
        VaultAuthMethod authMethod = app.getVaultAuthMethod();

        if (authMethod == null) {
            logger.error("No valid Vault authentication method configured for application: {}", app.getName());
            throw new IllegalArgumentException("No valid Vault authentication method configured.");
        }

        String vaultUrl = ensureAbsoluteUrl(app.getVaultUrl());
        RestTemplate restTemplate = createRestTemplate(vaultUrl + ":8200");

        switch (authMethod) {
            case APPROLE:
                logger.info("Using AppRole authentication for application: {}", app.getName());
                return new TokenAuthentication(fetchAppRoleToken(app, restTemplate, vaultUrl + ":8200"));
            case USERPASS:
                logger.info("Using UserPass authentication for application: {}", app.getName());
                return new TokenAuthentication(fetchUserPassToken(app, restTemplate, vaultUrl + ":8200"));
            case TOKEN:
            default:
                logger.info("Using Token authentication for application: {}", app.getName());
                return new TokenAuthentication(app.getVaultToken());
        }
    }

    private String fetchAppRoleToken(Application app, RestTemplate restTemplate, String vaultUrl) {
        String loginEndpoint = vaultUrl + "/v1/auth/approle/login";
        logger.debug("Fetching AppRole token from Vault at endpoint: {}", loginEndpoint);

        Map<String, String> requestBody = Map.of(
                "role_id", app.getAppRoleId(),
                "secret_id", app.getAppRoleSecret()
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(loginEndpoint, requestBody, Map.class);
        if (response == null || !response.containsKey("auth")) {
            logger.error("Failed to authenticate with AppRole for application: {}. No auth field in response.", app.getName());
            throw new IllegalStateException("Failed to authenticate with AppRole. No auth field in response.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> auth = (Map<String, Object>) response.get("auth");
        logger.info("Successfully fetched AppRole token for application: {}", app.getName());
        return (String) auth.get("client_token");
    }

    private String fetchUserPassToken(Application app, RestTemplate restTemplate, String vaultUrl) {
        String loginEndpoint = vaultUrl + "/v1/auth/userpass/login/" + app.getVaultUsername();
        logger.debug("Fetching UserPass token from Vault at endpoint: {}", loginEndpoint);

        Map<String, String> requestBody = Map.of("password", app.getVaultPassword());

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(loginEndpoint, requestBody, Map.class);
        if (response == null || !response.containsKey("auth")) {
            logger.error("Failed to authenticate with UserPass for application: {}. No auth field in response.", app.getName());
            throw new IllegalStateException("Failed to authenticate with UserPass. No auth field in response.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> auth = (Map<String, Object>) response.get("auth");
        logger.info("Successfully fetched UserPass token for application: {}", app.getName());
        return (String) auth.get("client_token");
    }

    private RestTemplate createRestTemplate(String vaultEndpoint) {
        logger.debug("Creating RestTemplate for Vault endpoint: {}", vaultEndpoint);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(vaultEndpoint));
        return restTemplate;
    }

    private String ensureAbsoluteUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            logger.debug("Adding default scheme to Vault URL: {}", url);
            return "http://" + url;
        }
        return url;
    }
}