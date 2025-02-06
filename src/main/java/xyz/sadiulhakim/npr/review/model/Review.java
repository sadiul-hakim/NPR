package xyz.sadiulhakim.npr.review.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 250, nullable = false)
    private String review;

    @Column(nullable = false)
    private double rating;

    @Column(length = 100, nullable = false)
    private String visitorName;

    @Column(length = 100, nullable = false)
    private String visitorEmail;

    @Column(length = 200)
    private String visitorPicture;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time = LocalDateTime.now();

    public Review() {
    }

    public Review(long id, String review, double rating, String visitorName, String visitorEmail,
                  String visitorPicture, LocalDateTime time) {
        this.id = id;
        this.review = review;
        this.rating = rating;
        this.visitorName = visitorName;
        this.visitorEmail = visitorEmail;
        this.visitorPicture = visitorPicture;
        this.time = time;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review1 = (Review) o;
        return id == review1.id && Double.compare(rating, review1.rating) == 0 && Objects.equals(review, review1.review) && Objects.equals(visitorName, review1.visitorName) && Objects.equals(visitorEmail, review1.visitorEmail) && Objects.equals(visitorPicture, review1.visitorPicture) && Objects.equals(time, review1.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, review, rating, visitorName, visitorEmail, visitorPicture, time);
    }
}
