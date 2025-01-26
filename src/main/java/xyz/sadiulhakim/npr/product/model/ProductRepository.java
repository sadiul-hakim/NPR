package xyz.sadiulhakim.npr.product.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    Page<Product> findAllByNameContainingOrCategoryContainingOrBrandContainingOrDescriptionContaining(String name, String category, String brand, String description, Pageable pageable);

    List<Product> findAllByCategory(String category);

    List<Product> findAllByBrand(String brand);

    @Query(value = "select count(*) from Product")
    long numberOfProducts();
}
