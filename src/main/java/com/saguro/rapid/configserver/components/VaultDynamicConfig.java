package com.saguro.rapid.configserver.components;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.config.VaultConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import com.saguro.rapid.configserver.entity.GitRepository;

import java.util.Collections;
import java.util.Map;

@Component
public class VaultDynamicConfig {

    public Map<String, Object> configureAndFetchVaultProperties(GitRepository repo, String microservice) {
        // Obtener la configuración de Vault desde la base de datos

            VaultEndpoint vaultEndpoint = VaultEndpoint.create(repo.getVaultUrl(), 8200);
            vaultEndpoint.setScheme("http");  // Ajusta el puerto si es necesario
            TokenAuthentication tokenAuthentication = new TokenAuthentication(repo.getVaultToken());


            // Leer las propiedades desde Vault
            String secretPath = String.format("%s/%s/%s", repo.getSecretEngine(), "data", microservice);
            VaultResponse response = new VaultTemplate(vaultEndpoint, tokenAuthentication).read(secretPath);

            // Verificar que 'response' no sea nulo y que 'getData()' sea un objeto no nulo
            if (response != null && response.getData() != null) {
                // Obtener el valor de "data" dentro de la respuesta
                Map<String, Object> responseData = response.getData();
                Object data = (responseData != null) ? responseData.get("data") : null;

                // Verificar que 'data' sea un mapa, para evitar posibles errores de tipo
                if (data instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked") // Suprimir la advertencia de tipo
                    Map<String, Object> result = (Map<String, Object>) data;
                    return result;  // Retorna las propiedades desde Vault
                }
            }
            // Si no se encuentran datos válidos o el tipo es incorrecto, retornar un Map vacío
            return Collections.emptyMap();

    }
}