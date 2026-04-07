package ba.unsa.etf.nwt.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Brico – User Service API")
                .version("1.0.0")
                .description("Upravljanje korisnicima (registracija, pregled, ažuriranje, brisanje)")
                .contact(new Contact().name("Brico Team").email("dev@brico.ba")));
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
