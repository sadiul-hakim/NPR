package xyz.sadiulhakim.npr.review.web;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.sadiulhakim.npr.review.model.Review;
import xyz.sadiulhakim.npr.review.model.ReviewService;

import java.util.Map;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @ResponseBody
    @GetMapping("/vote")
    public Map<String, Integer> voteReview(@RequestParam(defaultValue = "0") String visitor,
                                           @RequestParam(defaultValue = "0") long reviewId,
                                           @RequestParam(defaultValue = "0") int type) {
        return reviewService.vote(visitor, reviewId, type);
    }

    @PostMapping("/write")
    String writeReview(@RequestParam int rating, @RequestParam String review, @RequestParam long productId,
                       @RequestParam String visitor) {

        if (rating < 0 || rating > 5 || !StringUtils.hasText(review) || review.length() > 250) {
            return "redirect:/products/view?page=0&&productId=" + productId;
        }

        Review reviewObj = new Review();
        reviewObj.setReview(review);
        reviewObj.setRating(rating);

        reviewService.save(reviewObj, productId, visitor);
        return "redirect:/products/view?page=0&&productId=" + productId;
    }

    @GetMapping("/delete")
    String deleteReview(@RequestParam long reviewId, @RequestParam String visitor) {
        long productId = reviewService.delete(reviewId, visitor);
        return "redirect:/products/view?page=0&&productId=" + productId;
    }
}