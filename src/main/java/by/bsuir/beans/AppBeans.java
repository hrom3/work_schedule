package by.bsuir.beans;

import by.bsuir.util.RandomFromFile;
import by.bsuir.util.UserGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;


@Configuration
public class AppBeans {

    @Bean
    public RandomFromFile getRandomMy() {
        return new RandomFromFile();
    }

    @Bean
    public UserGenerator getUserGenerator() {
        return new UserGenerator();
    }

    @Bean
    public NoOpPasswordEncoder noOpPasswordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
