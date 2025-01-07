package xyz.sadiulhakim.npr.user;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.sadiulhakim.npr.role.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findAllByRole(Role role);
}