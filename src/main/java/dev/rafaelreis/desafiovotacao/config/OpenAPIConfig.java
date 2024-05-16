package dev.rafaelreis.desafiovotacao.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openApiDocumentation() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("VOTACAO REST API")
                    .description("API para votação de pautas em assembleias de associados")
                    .version("1.0")
                    .version("2.0")
                    .license(new License()
                        .name("Apache License Version 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public GroupedOpenApi apiV1() {
        String[] paths = {"/api/v1/**"};
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch(paths)
                .pathsToExclude("/api/v2/**")
                .build();
    }

    @Bean
    public GroupedOpenApi apiV2() {
        String[] paths = {"/api/v2/**"};
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch(paths)
                .pathsToExclude("/api/v1/**")
                .build();
    }

}
