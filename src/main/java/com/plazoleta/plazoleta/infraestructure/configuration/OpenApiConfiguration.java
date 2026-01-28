package com.plazoleta.plazoleta.infraestructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Microservicio Plazoleta")
                        .version("1.0.0")
                        .description("API REST para la gestión de restaurantes y platos en el sistema de plazoleta de comidas")
                        .contact(new Contact()
                                .name("Equipo Plazoleta")
                                .email("soporte@plazoleta.com")));
    }
}