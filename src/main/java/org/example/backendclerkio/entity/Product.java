package org.example.backendclerkio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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



    public Product(int id, String title, String description, float price, int stock, String category, List<String> images) {
        this.productId = id;
        this.name = title;
        this.description = description;
        this.price = price;
        this.stockCount = stock;
        this.imageURL = images != null && !images.isEmpty() ? images.get(0) : null; // Assuming you want to store the first image in the list
    }

}
