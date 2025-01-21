package xyz.sadiulhakim.npr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    Executor customAsyncTaskExecutor(){
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
