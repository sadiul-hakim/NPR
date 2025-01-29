package xyz.sadiulhakim.npr.visitor.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface VisitorRepo extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findByEmail(String email);
}
