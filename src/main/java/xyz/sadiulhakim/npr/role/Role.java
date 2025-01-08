package xyz.sadiulhakim.npr.role;

import jakarta.persistence.*;


@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 30, nullable = false, unique = true)
    private String role;

    @Column(length = 150)
    private String description;

    public Role() {
    }

    public Role(long id, String role, String description) {
        this.id = id;
        this.role = role;
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
