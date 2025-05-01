package xyz.sadiulhakim.npr.review.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
import java.util.stream.Collectors;

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

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof OAuth2User user) {
            String username = user.getAttribute("email");
            Visitor visitor = visitorService.getByMail(username).get();
            List<Review> reviews = repository.findAllByVisitorAndProduct(visitor, product.get());

            // Remove duplicates (in case visitorReviews are already in the page)
            List<Review> filteredPageContent = page.getContent().stream()
                    .filter(review -> !review.getVisitor().getEmail().equals(visitor.getEmail()))
                    .toList();

            // Combine: visitor reviews first, then rest of the page
            List<Review> combined = new ArrayList<>();
            combined.addAll(reviews);
            combined.addAll(filteredPageContent);

            // Trim to fit page size
            int pageSize = appProperties.getPaginationSize();
            List<Review> finalPageContent = combined.stream()
                    .limit(pageSize)
                    .toList();

            // Wrap into a PageImpl to retain pagination metadata
            page = new PageImpl<>(finalPageContent, PageRequest.of(pageNumber, pageSize),
                    page.getTotalElements());
        }

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

    public long delete(long reviewId, String visitor) {
        Optional<Visitor> vByMail = visitorService.getByMail(visitor);
        if (vByMail.isEmpty()) {
            return 0;
        }

        Optional<Review> rById = getById(reviewId);
        if (rById.isEmpty()) {
            return 0;
        }

        Review review = rById.get();
        Visitor visitorObj = vByMail.get();
        if (!review.getVisitor().getEmail().equals(visitorObj.getEmail())) {
            return 0;
        }

        Product product = review.getProduct();
        product.setNumOfRating(product.getNumOfRating() - 1);
        product.setSumOfRating(product.getSumOfRating() - review.getRating());

        double finalRating = calculateBayesianAverage(product.getSumOfRating(), product.getNumOfRating());
        product.setRating(finalRating);

        productService.save(product, null);
        repository.delete(review);

        return product.getId();
    }
}
