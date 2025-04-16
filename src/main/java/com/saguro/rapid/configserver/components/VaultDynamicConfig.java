package com.saguro.rapid.configserver.components;

import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.*;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.enums.VaultAuthMethod;

import java.util.Collections;
import java.util.Map;
import java.net.URI;

@Component
public class VaultDynamicConfig {

    public Map<String, Object> configureAndFetchVaultProperties(Application app, String microservice) {
        if (!app.isVaultEnabled()) {
            // Si Vault está deshabilitado, retornar un Map vacío
            return Collections.emptyMap();
        }

        VaultEndpoint vaultEndpoint = VaultEndpoint.create(app.getVaultUrl(), 8200);
        vaultEndpoint.setScheme("http"); // Ajusta el esquema si es necesario

        ClientAuthentication clientAuthentication = getClientAuthentication(app);

        // Leer las propiedades desde Vault
        String secretPath = String.format("%s/%s/%s", app.getSecretEngine(), "data", microservice);
        VaultResponse response = new VaultTemplate(vaultEndpoint, clientAuthentication).read(secretPath);

        if (response != null && response.getData() != null) {
            Map<String, Object> responseData = response.getData();
            Object data = (responseData != null) ? responseData.get("data") : null;

            if (data instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) data;
                return result;
            }
        }
        return Collections.emptyMap();
    }

    private ClientAuthentication getClientAuthentication(Application app) {
        VaultAuthMethod authMethod = app.getVaultAuthMethod();

        if (authMethod == null) {
            throw new IllegalArgumentException("No valid Vault authentication method configured.");
        }

        // Asegurarse de que la URL de Vault sea absoluta
        String vaultUrl = ensureAbsoluteUrl(app.getVaultUrl());

        // Configurar el RestTemplate con el VaultEndpoint
        RestTemplate restTemplate = createRestTemplate(vaultUrl + ":8200");

        switch (authMethod) {
            case APPROLE:
                String approleToken = fetchAppRoleToken(app, restTemplate, vaultUrl + ":8200");
                return new TokenAuthentication(approleToken);
            case USERPASS:
                String userPassToken = fetchUserPassToken(app, restTemplate, vaultUrl + ":8200");
                return new TokenAuthentication(userPassToken);
            case TOKEN:
            default:
                return new TokenAuthentication(app.getVaultToken());
        }
    }

    private String fetchAppRoleToken(Application app, RestTemplate restTemplate, String vaultUrl) {
        String loginEndpoint = vaultUrl + "/v1/auth/approle/login";
        Map<String, String> requestBody = Map.of(
                "role_id", app.getAppRoleId(),
                "secret_id", app.getAppRoleSecret()
        );

        Map<String, Object> response = restTemplate.postForObject(loginEndpoint, requestBody, Map.class);
        if (response == null || !response.containsKey("auth")) {
            throw new IllegalStateException("Failed to authenticate with AppRole. No auth field in response.");
        }

        Map<String, Object> auth = (Map<String, Object>) response.get("auth");
        return (String) auth.get("client_token");
    }

    private String fetchUserPassToken(Application app, RestTemplate restTemplate, String vaultUrl) {
        String loginEndpoint = vaultUrl + "/v1/auth/userpass/login/" + app.getVaultUsername();
        Map<String, String> requestBody = Map.of("password", app.getVaultPassword());

        Map<String, Object> response = restTemplate.postForObject(loginEndpoint, requestBody, Map.class);
        if (response == null || !response.containsKey("auth")) {
            throw new IllegalStateException("Failed to authenticate with UserPass. No auth field in response.");
        }

        Map<String, Object> auth = (Map<String, Object>) response.get("auth");
        return (String) auth.get("client_token");
    }

    private RestTemplate createRestTemplate(String vaultEndpoint) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000); // Configurar tiempo de espera de conexión
        requestFactory.setReadTimeout(5000); // Configurar tiempo de espera de lectura
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(vaultEndpoint));
        return restTemplate;
    }

    private String ensureAbsoluteUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url; // Agregar esquema predeterminado si falta
        }
        return url;
    }
}