package xyz.sadiulhakim.npr.review.model;

import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public long count(){
        return repository.numberOfReviews();
    }
}
