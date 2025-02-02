package xyz.sadiulhakim.npr.visitor.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface VisitorRepo extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findByEmail(String email);

    Optional<Visitor> findBySub(String sub);

    Page<Visitor> findAllByEmailContainingOrSubContainingOrNameContaining(String email, String sub, String name,
                                                                          Pageable pageable);

    @Query(value = "select count(*) from Visitor")
    long numberOfBrands();
}
