package xyz.sadiulhakim.npr.role.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import xyz.sadiulhakim.npr.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Role {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank
    @Size(min = 2,max = 30)
    @Column(length = 30, nullable = false, unique = true)
    private String name;

    @Column(length = 150)
    private String description;

    @OneToMany(mappedBy = "role")
    private List<User> users = new ArrayList<>();

    public Role() {
    }

    public Role(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Role(long id, String name, String description, List<User> users) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.users = users;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
