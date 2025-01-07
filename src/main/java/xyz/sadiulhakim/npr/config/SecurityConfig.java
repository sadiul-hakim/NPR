package xyz.sadiulhakim.npr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import xyz.sadiulhakim.npr.user.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {

        String[] publicApi = {
                "/",
                "/css/**",
                "/fonts/**",
                "/js/**",
                "/images/**",
                "/admin_login"
        };

        String[] adminAccess = {
                "/dashboard/**",
                "/users/**",
                "/brands/**",
                "/categories/**",
                "/products/**"
        };
        return http
                .authorizeHttpRequests(auth -> auth.requestMatchers(publicApi).permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers(adminAccess).hasAnyRole("ADMIN"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .oauth2Login(login -> login.loginPage("/oauth2/authorization/google").defaultSuccessUrl("/", true))
                .formLogin(form -> form
                        .loginPage("/admin_login")
                        .defaultSuccessUrl("/dashboard/user_page", true)
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").permitAll().logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
