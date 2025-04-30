package xyz.sadiulhakim.npr.review.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import xyz.sadiulhakim.npr.product.converter.SetOfLongConverter;
import xyz.sadiulhakim.npr.product.model.Product;

import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(max = 120)
    @Column(length = 250, nullable = false)
    private String review;

    @PositiveOrZero
    @Column(nullable = false)
    private double rating;

    @JsonIgnore
    @Convert(converter = SetOfLongConverter.class)
    @Column(columnDefinition = "json")
    private Set<Long> helpful = new HashSet<>();

    @JsonIgnore
    @Convert(converter = SetOfLongConverter.class)
    @Column(columnDefinition = "json")
    private Set<Long> notHelpful = new HashSet<>();

    @NotBlank
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String visitorName;

    @NotBlank
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String visitorEmail;

    @NotBlank
    @Size(max = 200)
    @Column(length = 200)
    private String visitorPicture;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time = LocalDateTime.now();

    public Review() {
    }

    public Review(long id, String review, double rating, Set<Long> helpful, Set<Long> notHelpful, String visitorName,
                  String visitorEmail, String visitorPicture, Product product, LocalDateTime time) {
        this.id = id;
        this.review = review;
        this.rating = rating;
        this.helpful = helpful;
        this.notHelpful = notHelpful;
        this.visitorName = visitorName;
        this.visitorEmail = visitorEmail;
        this.visitorPicture = visitorPicture;
        this.product = product;
        this.time = time;
    }

    public Review(long id, String review, double rating, String visitorName, String visitorEmail,
                  String visitorPicture, Product product) {
        this.id = id;
        this.review = review;
        this.rating = rating;
        this.visitorName = visitorName;
        this.visitorEmail = visitorEmail;
        this.visitorPicture = visitorPicture;
        this.product = product;
    }

    public String getVisitorEmail() {
        return visitorEmail;
    }

    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorPicture() {
        return visitorPicture;
    }

    public void setVisitorPicture(String visitorPicture) {
        this.visitorPicture = visitorPicture;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Set<Long> getHelpful() {
        return helpful;
    }

    public void setHelpful(Set<Long> helpful) {
        this.helpful = helpful;
    }

    public Set<Long> getNotHelpful() {
        return notHelpful;
    }

    public void setNotHelpful(Set<Long> notHelpful) {
        this.notHelpful = notHelpful;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Review review1 = (Review) o;
        return id == review1.id && Double.compare(rating, review1.rating) == 0 && Objects.equals(review, review1.review) &&
                Objects.equals(helpful, review1.helpful) && Objects.equals(notHelpful, review1.notHelpful) &&
                Objects.equals(visitorName, review1.visitorName) && Objects.equals(visitorEmail, review1.visitorEmail) &&
                Objects.equals(visitorPicture, review1.visitorPicture) && Objects.equals(product, review1.product) &&
                Objects.equals(time, review1.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, review, rating, helpful, notHelpful, visitorName, visitorEmail, visitorPicture, product,
                time);
    }
}
