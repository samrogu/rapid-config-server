package com.saguro.rapid.configserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc // Habilita la configuración de MVC para asegurar que Swagger funcione correctamente
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rapid Config Server API")
                        .version("1.0")
                        .description("API documentation for Rapid Config Server"));
    }
}
