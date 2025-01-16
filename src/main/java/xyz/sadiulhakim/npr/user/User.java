package xyz.sadiulhakim.npr.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xyz.sadiulhakim.npr.role.Role;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "application_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private long id;

    @Column(length = 60, nullable = false)
    private String name;

    @Column(length = 60, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 100)
    private String picture;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    public User() {
    }

    public User(long id, String fullName, String email, String password, String photo, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.name = fullName;
        this.email = email;
        this.password = password;
        this.picture = photo;
        this.role = role;
        this.createdAt = createdAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> getRole().getName());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String fullName) {
        this.name = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String photo) {
        this.picture = photo;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
