package xyz.sadiulhakim.npr.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "application_user")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private long id;

    @Column(length = 60, nullable = false)
    private String fullName;

    @Column(length = 60, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 100)
    private String photo;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;
}
