package org.example.backendclerkio.service;

import org.example.backendclerkio.dto.ProductRequestDTO;
import org.example.backendclerkio.dto.ProductsRequestDTO;
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

import java.util.*;

@Service
public class ProductService {
    private final WebClient webClient;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, TagRepository tagRepository, WebClient.Builder webClient) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.webClient = webClient.build();
    }

    public Mono<ProductsRequestDTO> getProductsFromDummy() {
        return webClient.get()
                .uri("https://dummyjson.com/products?limit=100")
                .retrieve()
                .bodyToMono(ProductsRequestDTO.class);
    }

    public Mono<ProductsRequestDTO> getProductsFromAnotherDummy() {
        return webClient.get()
                .uri("https://dummyjson.com/products?skip=100&limit=200")
                .retrieve()
                .bodyToMono(ProductsRequestDTO.class);

    }

    public Mono<ProductsRequestDTO> getAllProducts() {
        Mono<ProductsRequestDTO> firstBatchMono = getProductsFromDummy();
        Mono<ProductsRequestDTO> secondBatchMono = getProductsFromAnotherDummy();

        return Mono.zip(firstBatchMono, secondBatchMono)
                .map(tuple -> {
                    ProductsRequestDTO firstBatch = tuple.getT1();
                    ProductsRequestDTO secondBatch = tuple.getT2();

                    // Combine the product lists
                    List<ProductRequestDTO> combinedProducts = new ArrayList<>();
                    combinedProducts.addAll(firstBatch.products());
                    combinedProducts.addAll(secondBatch.products());

                    // Create a new ProductsResponseDTO with combined products
                    ProductsRequestDTO combinedResponse = new ProductsRequestDTO(
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
        String categoryName = productRequestDTO.category();

        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        Set<Tag> tags = new HashSet<>();
        for (String tagName : productRequestDTO.tags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            tags.add(tag);
        }

        Product product = new Product(
                productRequestDTO.title(),
                productRequestDTO.description(),
                productRequestDTO.price(),
                productRequestDTO.discountPrice(),
                productRequestDTO.stockCount(),
                category,
                productRequestDTO.images(),
                tags
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

    public Product updateStock(int id, int quantityToAdd) {
        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        if (quantityToAdd < 0) {
            throw new IllegalArgumentException("Quantity to add must be non-negative");
        }

        int currentStock = productToUpdate.getStockCount();

        int updatedStock = currentStock + quantityToAdd;


        productToUpdate.setStockCount(updatedStock);

        return productRepository.save(productToUpdate);
    }

    public Product updateProduct(int id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        String categoryName = productRequestDTO.category();

        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        Set<Tag> tags = new HashSet<>();
        for (String tagName : productRequestDTO.tags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));
            tags.add(tag);
        }

        existingProduct.setTitle(productRequestDTO.title());
        existingProduct.setDescription(productRequestDTO.description());
        existingProduct.setPrice(productRequestDTO.price());
        existingProduct.setDiscountPrice(productRequestDTO.discountPrice());
        existingProduct.setStockCount(productRequestDTO.stockCount());
        existingProduct.setCategory(category);
        existingProduct.setImages(productRequestDTO.images());
        existingProduct.setTags(tags);

        return productRepository.save(existingProduct);
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public Page<Product> findProducts(String category, String search, boolean lowStock, boolean outOfStock, Integer minPrice, Integer maxPrice, Pageable pageable) {
        return productRepository.findProducts(category, search, lowStock, outOfStock, minPrice, maxPrice, pageable);
    }
}