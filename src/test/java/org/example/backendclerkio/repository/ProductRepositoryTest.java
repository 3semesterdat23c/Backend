package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryName("Tis");
        categoryRepository.save(category);

        // Product 1 - Laptop
        Product product1 = new Product();
        product1.setTitle("Laptop");
        product1.setDescription("High performance laptop");
        product1.setPrice(1000);
        product1.setDiscountPrice(950);
        product1.setStockCount(100);
        product1.setCategory(category);
        product1.setImages(List.of("image1_url", "image2_url"));
        product1.setTags(Set.of(new Tag("electronics"), new Tag("computers")));



        // Product 2 - Smartphone
        Product product2 = new Product();
        product2.setTitle("Smartphone");
        product2.setDescription("Latest smartphone with advanced features");
        product2.setPrice(700);
        product2.setDiscountPrice(650);
        product2.setStockCount(102);
        product2.setCategory(category);
        product2.setImages(List.of("image3_url", "image4_url"));
        product2.setTags(Set.of(new Tag("electronics"), new Tag("mobile")));

        // Product 3 - Table
        Product product3 = new Product();
        product3.setTitle("Table");
        product3.setDescription("Wooden dining table");
        product3.setPrice(300);
        product3.setDiscountPrice(275);
        product3.setStockCount(99);
        product3.setCategory(category);
        product3.setImages(List.of("image5_url", "image6_url"));
        product3.setTags(Set.of(new Tag("furniture"), new Tag("wooden")));

        // Product 4 - Laptop2
        Product product4 = new Product();
        product4.setTitle("Laptop2");
        product4.setDescription("Budget laptop");
        product4.setPrice(55);
        product4.setDiscountPrice(50);
        product4.setStockCount(1);
        product4.setCategory(category);
        product4.setImages(List.of("image7_url", "image8_url"));
        product4.setTags(Set.of(new Tag("electronics"), new Tag("budget")));

        // Save all products
        productRepository.saveAll(List.of(product1, product2, product3, product4));
    }

    @Test
    void shouldFindAllByCategory() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> result = productRepository.findAllByCategory(pageable, category);

        int expected = 4;
        assertEquals(expected, result.getTotalElements());
    }



    @Test
    void findProductsByCategory_CategoryName() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> result = productRepository.findProductsByCategory_CategoryName("Tis", pageable);
        int expected = 4;
        assertEquals(expected, result.getTotalElements());
    }

    @Test
    void findByStockCountBetween() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> result = productRepository.findByStockCountBetween(100,200,pageable);
        int expected = 2;
        assertEquals(expected, result.getTotalElements());
    }

    @Test
    void findByStockCount() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findByStockCount(99,pageable);
        int expected = 1;
        assertEquals(expected, result.getTotalElements());
    }

    @Test
    void findByTitleContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findByTitleContainingIgnoreCase("Laptop", pageable);
        int expected = 2;
        assertEquals(expected, result.getTotalElements());
    }

    @Test
    void findProductsWithAllQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findProducts("Tis","Laptop",false,false,50,2000, pageable);
        int expected = 2;
        assertEquals(expected, result.getTotalElements());
    }
}