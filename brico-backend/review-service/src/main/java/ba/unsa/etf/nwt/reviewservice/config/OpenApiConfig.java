package ba.unsa.etf.nwt.reviewservice.config;

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
                .title("Brico – Review Service API")
                .version("1.0.0")
                .description("Upravljanje recenzijama i omiljenim salonima")
                .contact(new Contact().name("Brico Team").email("dev@brico.ba")));
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
