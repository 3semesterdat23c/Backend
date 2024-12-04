package org.example.backendclerkio.config;

import jakarta.annotation.PostConstruct;
import org.example.backendclerkio.dto.ProductResponseDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.dto.UserRequestDTO;
import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.Tag;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.repository.CategoryRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.repository.TagRepository;
import org.example.backendclerkio.repository.UserRepository;
import org.example.backendclerkio.service.ProductService;
import org.example.backendclerkio.service.UserService;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    //TEST
    private final UserService userService;
    private final UserRepository userRepository;

    public InitData(ProductRepository productRepository, CategoryRepository categoryRepository, TagRepository tagRepository, UserService userService, UserRepository userRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    @PostConstruct
    public void init() {

        //TEST
        if (userRepository.count() == 0) {
            userService.registerUser(new UserRequestDTO("lol", "lol", "lol@lol.dk", "lol"));
            User user = userService.findByUserEmail("lol@lol.dk").get();
            user.setAdmin(true);
            userRepository.save(user);
        }


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
        String categoryName = dto.category();
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        Set<Tag> tags = new HashSet<>();
        for (String tagName : dto.tags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            tags.add(tag);
        }

        float price = BigDecimal.valueOf(dto.price() / 100.0 * (100 - dto.discountPercentage()))
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();

        Product product = new Product(
                dto.title(),
                dto.description(),
                price,
                dto.stock(),
                category,
                dto.images(),
                dto.discountPercentage(),
                tags
        );

        return product;
    }
}