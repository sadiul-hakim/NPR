package xyz.sadiulhakim.npr.visitor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitorRepo extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findByEmail(String email);
}
