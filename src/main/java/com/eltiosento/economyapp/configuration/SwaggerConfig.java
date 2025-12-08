package com.eltiosento.economyapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

        @Bean
        OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new Components()
                                                .addSecuritySchemes("Bearer Authentication",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .bearerFormat("JWT")
                                                                                .scheme("bearer")))
                                .info(new Info()
                                                .title("API EconomyApp")
                                                .description(
                                                                "API para gestionar la economía doméstica de la familia")
                                                .contact(new Contact().name("Vicent Roselló")
                                                                .email("vicentedev37@gmail.com").url(null))
                                                .version("1.0"));
        }

}
