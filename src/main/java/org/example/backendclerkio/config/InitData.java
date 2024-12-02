package org.example.backendclerkio.config;

import jakarta.annotation.PostConstruct;
import org.example.backendclerkio.dto.ProductResponseDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.Tag;
import org.example.backendclerkio.repository.CategoryRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.repository.TagRepository;
import org.example.backendclerkio.service.ProductService;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class InitData {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductService productService;

    public InitData(ProductRepository productRepository, CategoryRepository categoryRepository, TagRepository tagRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.productService = productService;
    }

    @PostConstruct
    public void init() {
        if (productRepository.count() == 0) {
            try {
                ProductsResponseDTO response = productService.getAllProducts().block();

                if (response != null && response.products() != null) {
                    List<Product> products = response.products().stream()
                            .map(this::mapToEntity)
                            .collect(Collectors.toList());

                    productRepository.saveAll(products);
                } else {
                    System.err.println("No products received from the API.");
                }
            } catch (Exception e) {
                System.err.println("Error fetching products from API: " + e.getMessage());
            }
        } else {
            System.out.println("Database already populated with products.");
        }
    }

    private Product mapToEntity(ProductResponseDTO dto) {
        // Handle category manually
        String categoryName = dto.category();
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        // Map the tags from the DTO to a Set of Tag entities
        Set<Tag> tags = new HashSet<>();
        for (String tagName : dto.tags()) {
            // Check if the tag already exists, or create it if it doesn't
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            // Add the tag to the set
            tags.add(tag);
        }

        // Create the product with the category and tags
        Product product = new Product(
                dto.title(),
                dto.description(),
                dto.price(),
                dto.stock(),
                category, // Associate the found/created category
                dto.images(),
                dto.discountPercentage(),
                tags // Include tags as a Set of Tag entities
        );

        // Return the product
        return product;
    }
}