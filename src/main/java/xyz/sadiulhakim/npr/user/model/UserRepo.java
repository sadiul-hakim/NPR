package xyz.sadiulhakim.npr.user.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    long countByRole(String role);

    Page<User> findByNameContainingOrEmailContaining(String name, String email, Pageable page);

    @Query(value = "select count(*) from User")
    long numberOfUsers();
}
