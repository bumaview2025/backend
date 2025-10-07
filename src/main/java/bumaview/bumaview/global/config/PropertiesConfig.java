package bumaview.bumaview.global.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "bumaview.bumaview.global.properties")
public class PropertiesConfig {
}