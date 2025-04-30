package xyz.sadiulhakim.npr.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import xyz.sadiulhakim.npr.brand.model.Brand;
import xyz.sadiulhakim.npr.category.model.Category;
import xyz.sadiulhakim.npr.product.converter.MapOfStringAndObjectConverter;
import xyz.sadiulhakim.npr.review.model.Review;

import java.util.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(min = 2, max = 150)
    @Column(unique = true, nullable = false, length = 150)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Brand brand;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @Column(length = 150, nullable = false)
    private String picture;

    @Column(length = 150)
    private String qrCode;

    @Size(max = 250)
    @Column(length = 250)
    private String description;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = MapOfStringAndObjectConverter.class)
    private Map<String, Object> details = new HashMap<>();

    private double rating;

    @JsonIgnore
    private double sumOfRating;

    @JsonIgnore
    private int numOfRating;

    private boolean active = true;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
    private List<Review> reviews = new ArrayList<>();

    public Product() {
    }

    public Product(long id, String name, Brand brand, Category category, String picture, String qrCode,
                   String description, Map<String, Object> details, double rating, boolean active,
                   List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.picture = picture;
        this.qrCode = qrCode;
        this.description = description;
        this.details = details;
        this.rating = rating;
        this.active = active;
        this.reviews = reviews;
    }

    public Product(long id, String name, Brand brand, Category category, String description) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getSumOfRating() {
        return sumOfRating;
    }

    public void setSumOfRating(double sumOfRating) {
        this.sumOfRating = sumOfRating;
    }

    public int getNumOfRating() {
        return numOfRating;
    }

    public void setNumOfRating(int numOfRating) {
        this.numOfRating = numOfRating;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(rating, product.rating) == 0 &&
                Double.compare(sumOfRating, product.sumOfRating) == 0 && numOfRating == product.numOfRating &&
                active == product.active && Objects.equals(name, product.name) && Objects.equals(brand, product.brand) &&
                Objects.equals(category, product.category) && Objects.equals(picture, product.picture) &&
                Objects.equals(qrCode, product.qrCode) && Objects.equals(description, product.description) &&
                Objects.equals(details, product.details) && Objects.equals(reviews, product.reviews);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, brand, category, picture, qrCode, description, details, rating, sumOfRating,
                numOfRating, active, reviews);
    }
}