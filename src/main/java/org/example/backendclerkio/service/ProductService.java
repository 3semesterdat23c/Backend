package org.example.backendclerkio.service;

import jakarta.persistence.Column;
import org.example.backendclerkio.dto.ProductDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final WebClient webClient;

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository, WebClient.Builder webClient) {
        this.webClient = webClient.build();
        this.productRepository = productRepository;
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
                    List<ProductDTO> combinedProducts = new ArrayList<>();
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

    public Product createProduct(String name, float price, String decription, int stockCount, String imageURL){
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setDescription(decription);
        product.setStockCount(stockCount);
        product.setImageURL(imageURL);
        return productRepository.save(product);
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
        return productRepository.save(existingProduct);
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }


}



