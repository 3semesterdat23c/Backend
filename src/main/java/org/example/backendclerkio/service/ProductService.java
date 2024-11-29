package org.example.backendclerkio.service;

import org.example.backendclerkio.dto.ProductResponseDTO;
import org.example.backendclerkio.dto.ProductRequestDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.CategoryRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    private final WebClient webClient;

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, WebClient.Builder webClient) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.webClient = webClient.build();
    }


    public Mono<ProductsResponseDTO> getProductsFromDummy() {
        return webClient.get()
                .uri("https://dummyjson.com/products?limit=100")
                .retrieve()
                .bodyToMono(ProductsResponseDTO.class);
    }

    public Mono<ProductsResponseDTO> getProductsFromAnotherDummy() {
        return webClient.get()
                .uri("https://dummyjson.com/products?skip=100&limit=200")
                .retrieve()
                .bodyToMono(ProductsResponseDTO.class);

    }

    public Mono<ProductsResponseDTO> getAllProducts() {
        Mono<ProductsResponseDTO> firstBatchMono = getProductsFromDummy();
        Mono<ProductsResponseDTO> secondBatchMono = getProductsFromAnotherDummy();

        return Mono.zip(firstBatchMono, secondBatchMono)
                .map(tuple -> {
                    ProductsResponseDTO firstBatch = tuple.getT1();
                    ProductsResponseDTO secondBatch = tuple.getT2();

                    // Combine the product lists
                    List<ProductResponseDTO> combinedProducts = new ArrayList<>();
                    combinedProducts.addAll(firstBatch.products());
                    combinedProducts.addAll(secondBatch.products());

                    // Create a new ProductsResponseDTO with combined products
                    ProductsResponseDTO combinedResponse = new ProductsResponseDTO(
                            combinedProducts,
                            firstBatch.total() + secondBatch.total(), // Adjust total if necessary
                            firstBatch.skip(), // Adjust skip and limit as appropriate
                            firstBatch.limit() + secondBatch.limit()
                    );

                    return combinedResponse;
                });
    }

    public Page<Product> findAll(Pageable pageable){
        return productRepository.findAll(pageable);
    }

    public Product createProduct(ProductRequestDTO productRequestDTO) {
        // Get the category name from the DTO
        String categoryName = productRequestDTO.category();

        // Check if the category exists, or create it if it doesn't
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(0, categoryName, 0, null)));

        // Create a set of categories for the product (you can handle multiple categories if needed)
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        // Create the product with the associated category
        Product product = new Product(
                productRequestDTO.title(),
                productRequestDTO.description(),
                productRequestDTO.price(),
                productRequestDTO.stock(),
                categories,  // Associate the found/created category
                productRequestDTO.images(),
                productRequestDTO.discountPercentage()
        );

        productRepository.save(product);
        return product;
    }

    public void deleteProduct(int id){
    if (!productRepository.existsById(id)) {
        throw new IllegalArgumentException("Product not found");
    }
        productRepository.deleteById(id);

    }

    public Product updateProduct(int id, Product product) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setStockCount(product.getStockCount());
        existingProduct.setImageURL(product.getImageURL());
        existingProduct.setDiscount(product.getDiscount());
        return productRepository.save(existingProduct);
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }


}



