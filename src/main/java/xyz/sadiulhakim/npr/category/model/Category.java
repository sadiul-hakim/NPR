package xyz.sadiulhakim.npr.category.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import xyz.sadiulhakim.npr.product.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(min = 2,max = 65)
    @Column(length = 65, unique = true, nullable = false)
    private String name;

    @Column(length = 150, nullable = false)
    private String picture;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    public Category() {
    }

    public Category(long id, String name, String picture, List<Product> products) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id && Objects.equals(name, category.name) && Objects.equals(picture, category.picture) &&
                Objects.equals(products, category.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, picture, products);
    }
}
