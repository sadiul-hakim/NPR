package xyz.sadiulhakim.npr.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query(value = "select count(*) from Role")
    long numberOfRoles();

    Page<Role> findByNameContainingOrDescriptionContaining(String name, String desc, Pageable page);
}
