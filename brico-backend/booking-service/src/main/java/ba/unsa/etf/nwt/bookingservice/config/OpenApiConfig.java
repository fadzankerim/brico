package ba.unsa.etf.nwt.bookingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Brico – Booking Service API")
                .version("1.0.0")
                .description("Upravljanje terminima i rezervacijama")
                .contact(new Contact().name("Brico Team").email("dev@brico.ba")));
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
