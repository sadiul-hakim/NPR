package xyz.sadiulhakim.npr.brand.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query(value = "select count(*) from Brand")
    long numberOfBrands();

    Page<Brand> findAllByNameContaining(String name, Pageable page);

    Optional<Brand> findByName(String name);
}
