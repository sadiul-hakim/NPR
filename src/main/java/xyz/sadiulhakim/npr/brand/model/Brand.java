package xyz.sadiulhakim.npr.brand.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import xyz.sadiulhakim.npr.product.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(min = 2, max = 150)
    @Column(length = 150, unique = true, nullable = false)
    private String name;

    @Column(length = 150)
    private String picture;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "brand", fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();

    public Brand() {
    }

    public Brand(long id, String name, String picture, List<Product> products) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.products = products;
    }

    public Brand(long id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
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
        Brand brand = (Brand) o;
        return id == brand.id && Objects.equals(name, brand.name) && Objects.equals(picture, brand.picture) &&
                Objects.equals(products, brand.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, picture, products);
    }
}
