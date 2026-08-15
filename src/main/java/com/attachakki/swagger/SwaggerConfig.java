package com.attachakki.swagger;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(value = "dev")
@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI api() {

                Server server = new Server();
                server.setUrl("http://localhost:8080");
                server.setDescription("Development");

                Contact myContact = new Contact();
                myContact.setName("Karan Buradkar");
                myContact.setEmail("karanburadkar30@gmail.com");

                Info info = new Info()
                        .title("Atta Chakki APIs")
                        .version("v1.0")
                        .description("This API exposes endpoints to manage atta chakki")
                        .contact(myContact);

                Components components = new Components()
                        .addSecuritySchemes(
                                "JavaInUseSecurityScheme",
                                new SecurityScheme()
                                        .name("JavaInUseSecurityScheme")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer").bearerFormat("JWT")
                        );

                return new OpenAPI()
                        .info(info)
                        .servers(List.of(server))
                        .addSecurityItem(new SecurityRequirement().addList("JavaInUseSecurityScheme"))
                        .components(components);
        }

        @Bean
        public GroupedOpenApi allApi() {
                return GroupedOpenApi.builder()
                        .group("All")
                        .pathsToMatch("/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi authApi() {
                return GroupedOpenApi.builder()
                        .group("Auth")
                        .pathsToMatch("/auth/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi shopApi() {
                return GroupedOpenApi.builder()
                        .group("Shop")
                        .pathsToMatch("/v1/shops/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi v1Api() {
                return GroupedOpenApi.builder()
                        .group("v1")
                        .pathsToMatch("/v1/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi v2Api() {
                return GroupedOpenApi.builder()
                        .group("v2")
                        .pathsToMatch("/v2/**")
                        .build();
        }
}
