package com.hackathon.reservation.reservation_mvp.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Swagger / OpenAPI documentation, including JWT bearer auth scheme.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "JWT TOKEN";

    /**
     * Builds the root OpenAPI definition, setting title, version, and security requirements.
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI moongOpenApi() {
        Info info = new Info()
                .title("MOONG 술강신청 API")
                .description("술강신청 API 명세서")
                .version("1.0.0");

        SecurityRequirement requirement = new SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME);

        Components components = new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(requirement)
                .components(components);
    }
}