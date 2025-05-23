package xyz.sadiulhakim.npr.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    SecurityConfig(CustomUserDetailsService userDetailsService,
                   CustomAuthenticationSuccessHandler authenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    SecurityFilterChain config(HttpSecurity http) throws Exception {

        String[] publicApi = {
                "/",
                "/css/**",
                "/fonts/**",
                "/js/**",
                "/images/**",
                "/picture/**",
                "/products/by-brand/**",
                "/products/by-category/**",
                "/products/view",
                "/categories/list",
                "/brands/list",
                "/products/search_api/**",
                "/products/list"
        };

        String[] OIDC_USER = {
                "/reviews/vote"
        };

        String[] adminAccess = {
                "/dashboard/**",
                "/users/**",
                "/roles/**",
                "/brands/**",
                "/categories/**",
                "/products/**",
                "/visitors/**",
                "/actuator/**",
                "/data_importer",
                "/status"
        };
        return http
                .authorizeHttpRequests(auth -> auth.requestMatchers(publicApi).permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers(OIDC_USER).hasAuthority("OIDC_USER"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(adminAccess).hasAnyRole("ADMIN", "ASSISTANT"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .oauth2Login(login -> login.loginPage("/oauth2/authorization/google").successHandler(authenticationSuccessHandler))
                .formLogin(form -> form
                        .loginPage("/admin_login")
                        .defaultSuccessUrl("/dashboard/page", true)
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true").permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout")
                        .permitAll()
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)  // Invalidate session
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "SESSION")
                )
                .build();
    }
}
