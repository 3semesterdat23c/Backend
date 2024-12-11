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
    private String title;

    @Column(name = "product_description")
    private String description;


    @Column(name = "product_price", nullable = false)
    private double price;

    @Column(name = "product_discount_price", nullable = true)
    private double discountPrice;


    @Column(name = "stock_count", nullable = false)
    private int stockCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    private Category category;


    // Change from single String to List<String>
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images;


    @ManyToMany
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrderProduct> orderProducts;

    // Constructor with tags and category
    public Product(String title, String description, double price, double discountPrice, int stockCount, Category category, List<String> images, Set<Tag> tags) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stockCount = stockCount;
        this.category = category;
        this.images = images;
        this.tags = tags;
    }
}
