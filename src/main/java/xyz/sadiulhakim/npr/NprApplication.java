package xyz.sadiulhakim.npr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class NprApplication {

    public static void main(String[] args) {
        SpringApplication.run(NprApplication.class, args);
    }

}
