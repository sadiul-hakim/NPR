package xyz.sadiulhakim.npr.category.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    Page<Category> findAllByNameContaining(String name, Pageable page);

    @Query(value = "select count(*) from Category")
    long numberOfCategories();
}
