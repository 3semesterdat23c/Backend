package org.example.backendclerkio.config;

import jakarta.annotation.PostConstruct;
import org.example.backendclerkio.ProductDTO;
import org.example.backendclerkio.ProductsResponseDTO;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.service.ProductService;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class InitData {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public InitData(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
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

    private Product mapToEntity(ProductDTO dto) {
        System.out.println("Mapping:" + dto);
        return new Product (
                dto.id(),
                dto.title(),
                dto.description(),
                dto.price(),
                dto.stock(),
                dto.category(),
                dto.images()
        );
    }
}
