package by.bsuir.beans;

import by.bsuir.repository.RepositoryUtils;
import by.bsuir.util.ConfirmationDataGenerator;
import by.bsuir.util.ConverterDateTimeStamp;
import by.bsuir.util.RandomFromFile;
import by.bsuir.util.UserGenerator;
import by.bsuir.util.WorkTimeHelper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring3.SpringTemplateEngine;


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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        return new SpringTemplateEngine();
    }

    @Bean
    public ConfirmationDataGenerator getConfirmationData() {
        return new ConfirmationDataGenerator();
    }

    @Bean
    public RepositoryUtils getRepositoryUtils() {
        return new RepositoryUtils();
    }

    @Bean
    public ConverterDateTimeStamp getConverterDateTimeStamp() {
        return new ConverterDateTimeStamp();
    }

    @Bean
    public WorkTimeHelper getWorkTimeHelper() {
        return new WorkTimeHelper();
    }

}

