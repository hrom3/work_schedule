package by.bsuir;

import by.bsuir.beans.AppBeans;
import by.bsuir.beans.PersistenceBeansConfiguration;
import by.bsuir.beans.SecurityConfig;

import by.bsuir.beans.SwaggerConfig;
import by.bsuir.security.configuration.WebSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "by.bsuir")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableWebMvc
@EnableSwagger2
@Import({
        AppBeans.class,
        WebSecurityConfiguration.class,
        PersistenceBeansConfiguration.class,
        SwaggerConfig.class,
        SecurityConfig.class})
public class SpringBootStarter {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarter.class, args);
    }
}
