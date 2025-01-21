package xyz.sadiulhakim.npr.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String name;
    private final String password;
    private final String role;
    private final String picture;

    public CustomUserDetails(String username, String name, String password, String role, String picture) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
        this.picture = picture;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> role);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }
}
