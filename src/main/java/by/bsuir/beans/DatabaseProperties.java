package by.bsuir.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("database")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseProperties {

    private String driver;

    private String url;

    private String login;

    private String password;

}
