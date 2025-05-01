package xyz.sadiulhakim.npr.review.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import xyz.sadiulhakim.npr.product.converter.SetOfLongConverter;
import xyz.sadiulhakim.npr.product.model.Product;
import xyz.sadiulhakim.npr.visitor.model.Visitor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(max = 250)
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

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time = LocalDateTime.now();

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Review() {
    }

    public Review(long id, String review, double rating, Set<Long> helpful, Set<Long> notHelpful, Visitor visitor,
                  Product product, LocalDateTime time) {
        this.id = id;
        this.review = review;
        this.rating = rating;
        this.helpful = helpful;
        this.notHelpful = notHelpful;
        this.visitor = visitor;
        this.product = product;
        this.time = time;
    }

    public Review(long id, String review, double rating, Visitor visitor, Product product) {
        this.id = id;
        this.review = review;
        this.rating = rating;
        this.visitor = visitor;
        this.product = product;
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
                Objects.equals(visitor, review1.visitor) && Objects.equals(product, review1.product) &&
                Objects.equals(time, review1.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, review, rating, helpful, notHelpful, visitor, product, time);
    }
}
