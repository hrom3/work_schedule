package by.bsuir.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "confirmation.email")
@Data
public class ConfirmationDataProperties {

    private Long lifetimeConfirmationUuidMillis;
}