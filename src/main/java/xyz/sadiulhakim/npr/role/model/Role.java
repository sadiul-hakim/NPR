package xyz.sadiulhakim.npr.role.model;

import jakarta.persistence.*;
import org.springframework.modulith.NamedInterface;


@Entity
@NamedInterface("user-role")
public class Role {

    @Id
    @GeneratedValue
    private long id;

    @Column(length = 30, nullable = false, unique = true)
    private String name;

    @Column(length = 150)
    private String description;

    public Role() {
    }

    public Role(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
