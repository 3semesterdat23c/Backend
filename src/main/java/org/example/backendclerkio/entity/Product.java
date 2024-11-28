package org.example.backendclerkio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;
    @Column (name = "product_name")
    private String name;
    @Column (name = "product_price")
    private float price;
    @Column (name = "product_description")
    private String description;
    @Column (name = ("stock_count"))
    private int stockCount;
    @Column (name = "image_string")
    private String imageURL;
    @Column (name = "discount")
    private float discount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_product", // The join table name
            joinColumns = @JoinColumn(name = "product_id"), // Product column in join table
            inverseJoinColumns = @JoinColumn(name = "category_id") // Category column in join table
    )
    private Set<Category> categories;

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
