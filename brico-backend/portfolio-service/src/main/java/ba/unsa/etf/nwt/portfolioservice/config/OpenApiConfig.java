package ba.unsa.etf.nwt.portfolioservice.config;

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
                .title("Brico – Portfolio Service API")
                .version("1.0.0")
                .description("Upravljanje pretplatnim planovima i salon pretplatama")
                .contact(new Contact().name("Brico Team").email("dev@brico.ba")));
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
