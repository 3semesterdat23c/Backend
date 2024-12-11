//package org.example.backendclerkio.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.example.backendclerkio.entity.Category;
//import org.example.backendclerkio.entity.Product;
//import org.example.backendclerkio.entity.Tag;
//import org.example.backendclerkio.service.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class ProductControllerTest {
//
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper; // For serializing/deserializing JSON
//
//
//    @MockBean
//    private ProductService productService;
//
//    private Page<Product> mockProductPage;
//
//    @BeforeEach
//    void setUp() {
//        // Create a sample Product for testing
//        Product product = new Product();
//        product.setProductId(1);
//        product.setTitle("Product 1");
//        product.setDescription("Sample description");
//        product.setPrice(100.0);
//        product.setStockCount(10);
//
//        Category category = new Category();
//        category.setCategoryId(1);
//        category.setCategoryName("Category 1");
//        product.setCategory(category);
//
//        product.setImages(Arrays.asList("imageUrl1", "imageUrl2"));
//        Set<Tag> tags = new HashSet<>();
//        tags.add(new Tag("Tag1"));
//        product.setTags(tags);
//
//        // Create a Page<Product> mock response
//        mockProductPage = new PageImpl<>(Arrays.asList(product), PageRequest.of(0, 10), 1);
//    }
//
//    @Test
//    void findAll_NoParameters() throws Exception {
//        // Mock the service to return a page of products
//        Product product1 = new Product();
//        product1.setProductId(1);
//        product1.setTitle("Product 1");
//        product1.setDescription("Sample description");
//        product1.setPrice(100.0);
//        product1.setStockCount(10);
//
//        Product product2 = new Product();
//        product2.setProductId(2);
//        product2.setTitle("Product 2");
//        product2.setDescription("Another product description");
//        product2.setPrice(150.0);
//        product2.setStockCount(5);
//
//        List<Product> products = Arrays.asList(product1, product2);
//        Page<Product> mockProductPage = new PageImpl<>(products, PageRequest.of(0, 10), products.size());
//
//        // Mocking the productService method
//        when(productService.findAll(PageRequest.of(0, 10))).thenReturn(mockProductPage);
//
//        // Perform the GET request to /api/v1/products
//        mockMvc.perform(get("api/v1/products"))
//                .andExpect(status().isOk()) // Assert status 200 OK
//                .andExpect(jsonPath("$").isArray())  // Assert response is an array
//                .andExpect(jsonPath("$[0].productId").value(1)) // Assert productId is 1
//                .andExpect(jsonPath("$[0].title").value("Product 1")) // Assert title
//                .andExpect(jsonPath("$[0].description").value("Sample description")) // Assert description
//                .andExpect(jsonPath("$[0].price").value(100.0)) // Assert price
//                .andExpect(jsonPath("$[0].stockCount").value(10)) // Assert stock count
//                .andExpect(jsonPath("$[1].productId").value(2)) // Assert productId is 2
//                .andExpect(jsonPath("$[1].title").value("Product 2")); // Assert title of second product
//    }
//
//
//    @Test
//    void getProductList() {
//    }
//
//    @Test
//    void findByCategory() {
//    }
//
//    @Test
//    void createProduct() {
//    }
//
//    @Test
//    void deleteProduct() {
//    }
//
//    @Test
//    void updateProduct() {
//    }
//
//    @Test
//    void updateStock() {
//    }
//
//    @Test
//    void getProductById() {
//    }
//}