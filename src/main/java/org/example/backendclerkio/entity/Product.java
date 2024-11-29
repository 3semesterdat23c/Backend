package org.example.backendclerkio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "product_price", nullable = false)
    private float price;

    @Column(name = "product_description")
    private String description;

    @Column(name = "stock_count", nullable = false)
    private int stockCount;

    @Column(name = "image_string")
    private String imageURL;

    @Column(name = "discount", nullable = false)
    private float discount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_product",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonManagedReference // Indicates this is the "parent" side of the relationship
    private Set<Category> categories;

    // Convenience constructor
    public Product(String title, String description, float price, int stock, Set<Category> categories, List<String> images, float discount) {
        this.name = title;
        this.description = description;
        this.price = price;
        this.stockCount = stock;
        this.discount = discount;
        this.categories = categories;
        this.imageURL = images != null && !images.isEmpty() ? images.get(0) : null;
    }
}