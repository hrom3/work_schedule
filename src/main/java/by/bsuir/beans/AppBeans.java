package by.bsuir.beans;

import by.bsuir.util.RandomFromFile;
import by.bsuir.util.UserGenerator;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;


@Configuration
public class AppBeans {

    //select * from users where user id = ?
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // select * from users where user id = :userId
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource){
        return  new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public DataSource hikariDataSource(DatabaseProperties databaseProperties) {
        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setDriverClassName(databaseProperties.getDriverName());
        hikariDataSource.setJdbcUrl(databaseProperties.getUrl());
        hikariDataSource.setUsername(databaseProperties.getLogin());
        hikariDataSource.setPassword(databaseProperties.getPassword());
        hikariDataSource.setMaximumPoolSize(10);

        return  hikariDataSource;
    }

    @Bean
    public RandomFromFile getRandomMy() {
        return new RandomFromFile();
    }

//    @Bean
//    public UserGenerator getUserGenerator() {
//        return new UserGenerator();
//    }
}
