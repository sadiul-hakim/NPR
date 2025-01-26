package xyz.sadiulhakim.npr.product.model;

import jakarta.persistence.*;
import xyz.sadiulhakim.npr.product.converter.ListOfLongConverter;
import xyz.sadiulhakim.npr.product.converter.MapOfStringAndObjectConverter;

import java.util.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 150)
    private String name;

    @Column(length = 150, nullable = false)
    private String brand;

    @Column(length = 65, nullable = false)
    private String category;

    @Column(length = 150, nullable = false)
    private String picture;

    @Column(length = 250)
    private String description;

    @Column(columnDefinition = "JSON")
    @Convert(converter = MapOfStringAndObjectConverter.class)
    private Map<String, Object> details = new HashMap<>();

    private double rating;

    @Column(columnDefinition = "JSON")
    @Convert(converter = ListOfLongConverter.class)
    private List<Long> reviews = new ArrayList<>();

    public Product() {
    }

    public Product(long id, String name, String brand, String category, String picture, String description,
                   Map<String, Object> details, double rating, List<Long> reviews) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.picture = picture;
        this.description = description;
        this.details = details;
        this.rating = rating;
        this.reviews = reviews;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public List<Long> getReviews() {
        return reviews;
    }

    public void setReviews(List<Long> reviews) {
        this.reviews = reviews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(rating, product.rating) == 0 && Objects.equals(name, product.name) &&
                Objects.equals(brand, product.brand) && Objects.equals(category, product.category) &&
                Objects.equals(picture, product.picture) && Objects.equals(description, product.description) &&
                Objects.equals(details, product.details) && Objects.equals(reviews, product.reviews);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, brand, category, picture, description, details, rating, reviews);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", picture='" + picture + '\'' +
                ", description='" + description + '\'' +
                ", details=" + details +
                ", rating=" + rating +
                ", reviews=" + reviews +
                '}';
    }
}