package by.bsuir.beans;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("security")
@Data
public class SecurityConfig {

    private String secretKey;
}
