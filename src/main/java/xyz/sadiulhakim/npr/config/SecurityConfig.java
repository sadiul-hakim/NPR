package xyz.sadiulhakim.npr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {

        String[] publicApi = {
                "/",
                "/css/**",
                "/fonts/**",
                "/js/**",
                "/images/**",
                "/register_page",
                "/login",
                "/login_page"
        };

        String[] adminAccess = {
                "/dashboard",
                "/users/**",
                "/brands/**",
                "/categories/**",
                "/products/**"
        };
        return http
                .authorizeHttpRequests(auth -> auth.requestMatchers(publicApi).permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers(adminAccess).hasAnyRole("ADMIN"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2Login(login -> login.defaultSuccessUrl("/", true))
                .formLogin(form -> form.defaultSuccessUrl("/", true))
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
