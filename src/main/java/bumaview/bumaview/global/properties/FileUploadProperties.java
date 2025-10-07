package bumaview.bumaview.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    private String portfolioDir;
    private String sampleDir;
}