package xyz.sadiulhakim.npr.review.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.sadiulhakim.npr.product.model.Product;
import xyz.sadiulhakim.npr.visitor.model.Visitor;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select count(*) from Review")
    long numberOfReviews();

    long countAllByProduct(Product product);

    Page<Review> findAllByProductOrderByTimeDesc(Product product, Pageable page);

    List<Review> findAllByProduct(Product product);

    List<Review> findAllByVisitorAndProduct(Visitor visitor, Product product);

    long countAllByVisitorAndProduct(Visitor visitor, Product product);
}
