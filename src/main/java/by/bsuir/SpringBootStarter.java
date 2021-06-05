package by.bsuir;

import by.bsuir.beans.AppBeans;
import by.bsuir.beans.SecurityConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = "by.bsuir")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableWebMvc
@Import({
        AppBeans.class,
        SecurityConfig.class})
public class SpringBootStarter {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarter.class, args);
    }
}
