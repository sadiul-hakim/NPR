package xyz.sadiulhakim.npr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import xyz.sadiulhakim.npr.user.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomAuthenticationSuccessHandler authenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {

        String[] publicApi = {
                "/",
                "/css/**",
                "/fonts/**",
                "/js/**",
                "/images/**",
                "/picture/**",
                "/admin_login"
        };

        String[] authenticatedUserAccess = {
                "/categories/get-all",
                "/brands/get-all",
                "/products/get-all"
        };

        String[] adminAccess = {
                "/dashboard/**",
                "/users/**",
                "/roles/**",
                "/brands/**",
                "/categories/**",
                "/products/**"
        };
        return http
                .authorizeHttpRequests(auth -> auth.requestMatchers(publicApi).permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers(authenticatedUserAccess).authenticated())
                .authorizeHttpRequests(auth -> auth.requestMatchers(adminAccess).hasAnyRole("ADMIN", "ASSISTANT"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .oauth2Login(login -> login.loginPage("/oauth2/authorization/google").successHandler(authenticationSuccessHandler))
                .formLogin(form -> form
                        .loginPage("/admin_login")
                        .defaultSuccessUrl("/dashboard/page", true)
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").permitAll().logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
