package org.example.backendclerkio.config;

import jakarta.annotation.PostConstruct;
import org.example.backendclerkio.dto.ProductResponseDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.CategoryRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.service.ProductService;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class InitData {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductService productService;

    public InitData(ProductRepository productRepository, CategoryRepository categoryRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productService = productService;
    }

    @PostConstruct
    public void init() {
        if (productRepository.count() == 0) {
            ProductsResponseDTO response = productService.getAllProducts().block();

            if (response != null && response.products() != null) {
                List<Product> products = response.products().stream()
                        .map(this::mapToEntity)
                        .collect(Collectors.toList());

                productRepository.saveAll(products);
            } else {
                System.err.println("No products received from the API.");
            }
        } else {
            System.out.println("Database already populated with products.");
        }
    }

    private Product mapToEntity(ProductResponseDTO dto) {
        // Find or create the category
        Category category = categoryRepository.findByCategoryName(dto.category())
                .orElseGet(() -> categoryRepository.save(new Category(0, dto.category(), 0, null)));

        // Create the product with the category
        return new Product(
                dto.title(),
                dto.description(),
                dto.price(),
                dto.stock(),
                Set.of(category),  // Associate the found/created category
                dto.images(),
                dto.discountPercentage()
        );
    }
}
