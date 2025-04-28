package xyz.sadiulhakim.npr.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.sadiulhakim.npr.brand.model.BrandService;
import xyz.sadiulhakim.npr.category.model.CategoryService;
import xyz.sadiulhakim.npr.product.model.ProductService;
import xyz.sadiulhakim.npr.review.model.ReviewService;
import xyz.sadiulhakim.npr.user.model.UserService;
import xyz.sadiulhakim.npr.visitor.model.VisitorService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardService.class);
    private final UserService userService;
    private final VisitorService visitorService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ReviewService reviewService;

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public DashboardService(UserService userService, VisitorService visitorService, BrandService brandService,
                            CategoryService categoryService, ProductService productService, ReviewService reviewService) {
        this.userService = userService;
        this.visitorService = visitorService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.reviewService = reviewService;
    }

    public SseEmitter subscribe(String username) {
        // Retrieve the emitter from the map (or create a new one if it doesn't exist)
        SseEmitter emitter = emitters.computeIfAbsent(username, key -> new SseEmitter((long) (1000 * 60 * 20)));

        // Handle timeout
        emitter.onTimeout(() -> {
            emitter.complete(); // Complete the emitter on timeout
            emitters.remove(username); // Remove the emitter from the map on timeout
        });

        // Handle errors
        emitter.onError(ex -> {
            emitter.completeWithError(ex); // Complete with error if needed
            emitters.remove(username); // Remove the emitter from the map on error
        });

        // Handle completion
        emitter.onCompletion(() -> {
            emitters.remove(username); // Remove the emitter from the map on completion
        });

        // After the checks, ensure the emitter is still valid
        // If the emitter was removed (expired or completed), create a new one and return it
        return emitters.computeIfAbsent(username, key -> new SseEmitter((long) (1000 * 60 * 20)));
    }

    @Scheduled(fixedRate = 5000, scheduler = "defaultTaskScheduler")
    public void streamCount() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("Users", userService.count());
        counts.put("Visitors", visitorService.count());
        counts.put("Brands", brandService.count());
        counts.put("Categories", categoryService.count());
        counts.put("Products", productService.count());
        counts.put("Reviews", reviewService.count());

        for (SseEmitter emitter : emitters.values()) {
            try {
                emitter.send(SseEmitter.event().name("dashboard_counts").data(counts));
            } catch (IOException e) {
                LOGGER.warn("DashboardService.streamCount :: {}", e.getMessage());
            }
        }
    }
}
