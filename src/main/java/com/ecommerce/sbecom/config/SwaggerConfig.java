package com.ecommerce.sbecom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
//the scheme is for swagger authentication flow
        SecurityScheme bearerScheme= new SecurityScheme().
                type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer token");

        SecurityRequirement bearerRequirement= new SecurityRequirement()
        .addList("Bearer Authentication");

        return new OpenAPI()
                .info(new Info().title("Spring Boot eCommerce API")
                        .version("1.0")
                        .description("This is a spring Boot project for eCommerce")
                        .contact(new Contact()
                                .name("Keerthi Reddy")
                                .email("kxb220063@utdallas.edu")))
                .components(new Components()
                .addSecuritySchemes("Bearer Authentication", bearerScheme))
                .addSecurityItem(bearerRequirement);


    }
}
