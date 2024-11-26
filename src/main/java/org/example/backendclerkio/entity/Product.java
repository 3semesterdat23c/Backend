package org.example.backendclerkio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String productName;
    @Column (name = "product_price")
    private float productPrice;
    @Column (name = "product_description")
    private String productDescription;
    @Column (name = ("stock_count"))
    private int stockCount;
    @Column (name = "image_string")
    private String imageURL;


}
