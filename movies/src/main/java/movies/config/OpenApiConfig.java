package movies.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setName("Movies API Support");
        contact.setEmail("support@moviesapi.com");

        License license = new License();
        license.setName("Apache 2.0");

        Info info = new Info()
                .title("Movies API")
                .version("1.0.0")
                .description("REST API for managing movies, actors, genres, and reviews")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .servers(List.of(devServer))
                .info(info);
    }
}

