package dev.marcosoliveira.discography.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

@Configuration
public class SwaggerConfig {

    @Value("${api.info.version}")
    private String version;

    @Value("${api.info.title}")
    private String title;

    @Value("${api.info.description}")
    private String description;

    @Value("${api.info.contact-name}")
    private String contactName;

    @Value("${api.info.contact-email}")
    private String contactEmail;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi publicApiV1() {
        return GroupedOpenApi.builder()
                .group("v1-public")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OperationCustomizer pageableCustomizer() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() != null) {
                operation.getParameters().forEach(param -> {
                    if (param.getName() != null && (
                            param.getName().equals("page") ||
                            param.getName().equals("size") ||
                            param.getName().equals("sort"))) {
                        param.setRequired(false);
                        if (param.getSchema() != null) {
                            param.getSchema().setNullable(true);
                        }
                    }
                });
            }
            return operation;
        };
    }
}
