package xyz.sadiulhakim.npr.product.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.sadiulhakim.npr.brand.model.Brand;
import xyz.sadiulhakim.npr.category.model.Category;

import java.util.List;
import java.util.Optional;

interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    Page<Product> findAllByNameContainingOrDescriptionContaining(String name, String category, Pageable pageable);

    Page<Product> findAllByCategory(Category category, Pageable page);

    Page<Product> findAllByBrand(Brand brand, Pageable page);

    @Query(value = "select count(*) from Product")
    long numberOfProducts();

    List<Product> findAllByOrderByRatingDesc(Pageable pageable);
}
