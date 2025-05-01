package xyz.sadiulhakim.npr.review.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.product.model.Product;
import xyz.sadiulhakim.npr.product.model.ProductService;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.PageUtil;
import xyz.sadiulhakim.npr.visitor.model.Visitor;
import xyz.sadiulhakim.npr.visitor.model.VisitorService;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class ReviewService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final String STARS_5 = "5_Stars";
    private static final String STARS_4 = "4_Stars";
    private static final String STARS_3 = "3_Stars";
    private static final String STARS_2 = "2_Stars";
    private static final String STARS_1 = "1_Stars";

    private final ReviewRepository repository;
    private final ProductService productService;
    private final AppProperties appProperties;
    private final VisitorService visitorService;

    public ReviewService(ReviewRepository repository, ProductService productService, AppProperties appProperties,
                         VisitorService visitorService) {
        this.repository = repository;
        this.productService = productService;
        this.appProperties = appProperties;
        this.visitorService = visitorService;
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

    public void save(Review review, long productId, String visitor) {

        // If there is no such product do not save review
        Optional<Product> product = productService.getById(productId);
        if (product.isEmpty()) {
            return;
        }

        // If there is no such visitor do not save review
        Optional<Visitor> vById = visitorService.getByMail(visitor);
        if (vById.isEmpty()) {
            return;
        }

        // If the visitor had already given a review do not add another one
        long reviews = repository.countAllByVisitorAndProduct(vById.get(), product.get());
        if (reviews > 0) {
            return;
        }

        Product p = product.get();
        review.setProduct(p);
        review.setVisitor(vById.get());

        // Update the final rating of the product.
        p.setSumOfRating(p.getSumOfRating() + review.getRating());
        p.setNumOfRating(p.getNumOfRating() + 1);

        double finalRating = calculateBayesianAverage(p.getSumOfRating(), p.getNumOfRating());
        p.setRating(finalRating);

        repository.save(review);
        productService.save(p, null);
    }

    public Map<String, Integer> vote(String visitorMail, long reviewId, int voteType) {
        Optional<Review> byId = repository.findById(reviewId);
        if (byId.isEmpty()) {
            Map<String, Integer> map = new HashMap<>();
            map.put("helpful", 0);
            map.put("notHelpful", 0);
            return map;
        }

        Optional<Visitor> vByMail = visitorService.getByMail(visitorMail);
        if (vByMail.isEmpty()) {
            Map<String, Integer> map = new HashMap<>();
            map.put("helpful", 0);
            map.put("notHelpful", 0);
            return map;
        }

        Review review = byId.get();
        Visitor visitor = vByMail.get();

        // voteType 0 means helpful and 1 means not helpful
        if (voteType == 0) {
            review.getNotHelpful().remove(visitor.getId());
            review.getHelpful().add(visitor.getId());
        } else if (voteType == 1) {
            review.getHelpful().remove(visitor.getId());
            review.getNotHelpful().add(visitor.getId());
        }

        repository.save(review);

        Map<String, Integer> map = new HashMap<>();
        map.put("helpful", review.getHelpful().size());
        map.put("notHelpful", review.getNotHelpful().size());
        return map;
    }

    public Map<String, Integer> numberOfStars(long productId) {
        Map<String, Integer> map = new HashMap<>();
        map.put(STARS_5, 0);
        map.put(STARS_4, 0);
        map.put(STARS_3, 0);
        map.put(STARS_2, 0);
        map.put(STARS_1, 0);

        // If there is no such product do not save review
        Optional<Product> product = productService.getById(productId);
        if (product.isEmpty()) {
            return map;
        }

        List<Review> reviews = findAllByProduct(productId);
        int stars5 = 0;
        int stars4 = 0;
        int stars3 = 0;
        int stars2 = 0;
        int stars1 = 0;

        for (Review review : reviews) {
            double rating = review.getRating();
            if (rating == 5) {
                stars5++;
            } else if (rating == 4) {
                stars4++;
            } else if (rating == 3) {
                stars3++;
            } else if (rating == 2) {
                stars2++;
            } else if (rating == 1) {
                stars1++;
            }
        }

        map.put(STARS_5, stars5);
        map.put(STARS_4, stars4);
        map.put(STARS_3, stars3);
        map.put(STARS_2, stars2);
        map.put(STARS_1, stars1);
        return map;
    }

    private List<Review> findAllByProduct(long productId) {
        Optional<Product> product = productService.getById(productId);
        if (product.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.findAllByProduct(product.get());
    }

    private double calculateBayesianAverage(double totalRatingSum, int totalRatingCount) {
        final double C = 10.0; // MINIMUM_REVIEWS
        final double m = 3.5;  // GLOBAL_MEAN_RATING

        double rating = ((C * m) + (totalRatingSum)) / (C + totalRatingCount);
        String format = DECIMAL_FORMAT.format(rating);
        return Double.parseDouble(format);
    }
}
