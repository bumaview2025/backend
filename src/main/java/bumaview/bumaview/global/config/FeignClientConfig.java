package bumaview.bumaview.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "bumaview.bumaview")
public class FeignClientConfig {
}
