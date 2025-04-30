package xyz.sadiulhakim.npr.review.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.product.model.Product;
import xyz.sadiulhakim.npr.product.model.ProductService;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository repository;
    private final ProductService productService;
    private final AppProperties appProperties;

    public ReviewService(ReviewRepository repository, ProductService productService, AppProperties appProperties) {
        this.repository = repository;
        this.productService = productService;
        this.appProperties = appProperties;
    }

    public long count() {
        return repository.numberOfReviews();
    }

    public long countByProduct(long productId) {
        Optional<Product> product = productService.getById(productId);
        return product.map(repository::countAllByProduct).orElse(0L);
    }

    public Optional<Review> getById(long review) {
        return repository.findById(review);
    }

    public PaginationResult findAllByProduct(long productId, int pageNumber) {
        Optional<Product> product = productService.getById(productId);
        if (product.isEmpty()) {
            return new PaginationResult();
        }

        Page<Review> page = repository.findAllByProductOrderByTimeDesc(
                product.get(),
                PageRequest.of(pageNumber, appProperties.getPaginationSize())
        );
        return PageUtil.prepareResult(page);
    }

    public void save(Review review, long productId) {

        Optional<Product> product = productService.getById(productId);
        if (product.isEmpty()) {
            return;
        }

        Product p = product.get();
        review.setProduct(p);

        // Update the final rating of the product.
        p.setSumOfRating(p.getSumOfRating() + review.getRating());
        p.setNumOfRating(p.getNumOfRating() + 1);
        p.setRating(p.getSumOfRating() / p.getNumOfRating());

        repository.save(review);
        productService.save(p, null);
    }

    public long vote(long visitor, long reviewId, int voteType) {
        Optional<Review> byId = repository.findById(reviewId);
        if (byId.isEmpty()) {
            return 0;
        }

        Review review = byId.get();

        // voteType 0 means helpful and 1 means not helpful
        if (voteType == 0) {
            review.getNotHelpful().remove(visitor);
            review.getHelpful().add(visitor);
        } else if (voteType == 1) {
            review.getHelpful().remove(visitor);
            review.getNotHelpful().add(visitor);
        }

        repository.save(review);

        return voteType == 0 ? review.getHelpful().size() : review.getNotHelpful().size();
    }
}
