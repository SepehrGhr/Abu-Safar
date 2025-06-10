package ir.ac.kntu.abusafar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI abuSafarOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("AbuSafar API")
                        .description("A comprehensive transportation booking platform API for booking trains, buses, and flights.")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}