package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"config", "service"})
@PropertySource("application.properties")
public class SpringConfig {

    @Bean(name = "jacksonMapper")
    public ObjectMapper getJacksonMapper() {
        return new ObjectMapper();
    }

}
