package org.example.backendclerkio.service;

import org.example.backendclerkio.ProductDTO;
import org.example.backendclerkio.ProductsResponseDTO;
import org.example.backendclerkio.repository.ProductRepository;
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

    /*public Mono<ProductDTO> getProductsFromIkea() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://ikeaapi.p.rapidapi.com/keywordSearch?keyword=chair&countryCode=us&languageCode=en")
                .defaultHeader("x-rapidapi-key", "a5b84b7c39mshdae1688aabf7a42p196219jsnd2a0bf583d6a")
                .defaultHeader("x-rapidapi-host", "ikeaapi.p.rapidapi.com")
                .build();

        return webClient.get()
                .retrieve()
                .bodyToMono(ProductDTO.class);
    }
*/

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

}

