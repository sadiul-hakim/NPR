package xyz.sadiulhakim.npr.review.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select count(*) from Review")
    long numberOfReviews();
}
