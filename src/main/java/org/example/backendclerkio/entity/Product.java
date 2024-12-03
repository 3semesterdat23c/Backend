package org.example.backendclerkio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // Change from single String to List<String>
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images;

    @Column(name = "discount")
    private float discount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrderProduct> orderProducts;


    // Convenience constructor
    public Product(String title, String description, float price, int stock, Set<Category> categories, List<String> images, float discount) {
        this.name = title;
        this.description = description;
        this.price = price;
        this.stockCount = stock;
        this.discount = discount;
        this.categories = categories;
        this.images = images;
    }

    public Product(String title, String description, float price, int stock, String category, List<String> images, float discount) {
        this.name = title;
    // Constructor with tags and category
    public Product(String name, String description, float price, int stockCount, Category category, List<String> images, float discount, Set<Tag> tags) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockCount = stockCount;
        this.category = category;
        this.images = images;
        this.discount = discount;
        this.tags = tags;
    }
}
