
package org.example.backendclerkio.config;

import jakarta.annotation.PostConstruct;
import org.example.backendclerkio.dto.ProductRequestDTO;
import org.example.backendclerkio.dto.ProductsRequestDTO;
import org.example.backendclerkio.dto.UserRequestDTO;
import org.example.backendclerkio.entity.*;
import org.example.backendclerkio.repository.*;
import org.example.backendclerkio.service.OrderService;
import org.example.backendclerkio.service.ProductService;
import org.example.backendclerkio.service.UserService;
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
    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;


    public InitData(ProductRepository productRepository, CategoryRepository categoryRepository, TagRepository tagRepository, UserService userService, UserRepository userRepository, ProductService productService, OrderService orderService,OrderRepository orderRepository, OrderProductRepository orderProductRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.productService = productService;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @PostConstruct
    public void init() {

        if (userRepository.count() == 0) {
            userService.registerUser(new UserRequestDTO("admin", "admin", "admin@admin.dk", "admin"));
            User admin = userService.findByUserEmail("admin@admin.dk").get();
            admin.setAdmin(true);
            userService.registerUser(new UserRequestDTO("kunde", "kunde", "kunde@kunde.dk", "kunde"));
            User kunde = userService.findByUserEmail("kunde@kunde.dk").get();
            kunde.setAdmin(false);
            userRepository.saveAll(List.of(admin, kunde));
        }

        if (productRepository.count() == 0) {
            try {
           ProductsRequestDTO response = productService.getAllProducts().block();

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

        Product greenChiliPepper = productRepository.findById(26).get();
        greenChiliPepper.setDiscountPrice(0.50);
        productRepository.save(greenChiliPepper);

        Product car = productRepository.findById(170).get();
        car.setDiscountPrice(2);
        productRepository.save(car);

        Product onions = productRepository.findById(37).get();
        onions.setDiscountPrice(1);
        productRepository.save(onions);

        Product water = productRepository.findById(42).get();
        water.setStockCount(3);
        productRepository.save(water);

        Product cola = productRepository.findById(39).get();
        cola.setStockCount(0);
        productRepository.save(cola);
    }

    private Product mapToEntity(ProductRequestDTO productRequestDTO) {
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
                productRequestDTO.price(),
                productRequestDTO.stockCount(),
                category,
                productRequestDTO.images(),
                tags
        );

        return product;
    }
}