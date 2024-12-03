package org.example.backendclerkio.service;

import org.example.backendclerkio.dto.ProductResponseDTO;
import org.example.backendclerkio.dto.ProductRequestDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.Tag;
import org.example.backendclerkio.repository.CategoryRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.repository.TagRepository;
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
    private TagRepository tagRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, TagRepository tagRepository, WebClient.Builder webClient) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
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

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product createProduct(ProductRequestDTO productRequestDTO) {
        // Get the category name from the DTO
        String categoryName = productRequestDTO.category();

        // Check if the category exists, or create it if it doesn't
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        // Create a set of tags
        Set<Tag> tags = new HashSet<>();
        for (String tagName : productRequestDTO.tags()) {
            // Check if the tag already exists, if not create and save it
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            // Add the tag to the set
            tags.add(tag);
        }

        // Create the product with the category and tags
        Product product = new Product(
                productRequestDTO.title(),
                productRequestDTO.description(),
                productRequestDTO.price(),
                productRequestDTO.stock(),
                category, // Associate the found/created category
                productRequestDTO.images(),
                productRequestDTO.discountPercentage(),
                tags // Include tags as a Set of Tag entities
        );

        // Save the product to the repository
        productRepository.save(product);

        return product;
    }

    public void deleteProduct(int id){
    if (!productRepository.existsById(id)) {
        throw new IllegalArgumentException("Product not found");
    }
        productRepository.deleteById(id);

    }

    public Product updateProduct(int id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        String categoryName = productRequestDTO.category();

        // Check if the category exists, or create it if it doesn't
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        // Handle tags
        Set<Tag> tags = new HashSet<>();
        for (String tagName : productRequestDTO.tags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));
            tags.add(tag);
        }

        // Set the properties for the product
        existingProduct.setName(productRequestDTO.title());
        existingProduct.setPrice(productRequestDTO.price());
        existingProduct.setDescription(productRequestDTO.description());
        existingProduct.setStockCount(productRequestDTO.stock());
        existingProduct.setCategory(category);  // Single category set here
        existingProduct.setImages(productRequestDTO.images());
        existingProduct.setDiscount(productRequestDTO.discountPercentage());
        existingProduct.setTags(tags);  // Set the tags for the product

        return productRepository.save(existingProduct);
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }


}



