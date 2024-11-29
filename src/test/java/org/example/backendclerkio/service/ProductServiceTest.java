//package org.example.backendclerkio.service;
//
//import jakarta.transaction.Transactional;
//import org.example.backendclerkio.dto.ProductRequestDTO;
//import org.example.backendclerkio.entity.Product;
//import org.example.backendclerkio.repository.ProductRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentMatchers;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.data.domain.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.doThrow;
//
//@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Uses an in-memory database
//@Transactional
//class ProductServiceTest {
//
//    @Mock
//    private ProductRepository mockedProductRepository;
//
//    @Mock
//    private WebClient.Builder mockedWebClientBuilder; // Mocking WebClient.Builder
//
//    @Mock
//    private WebClient mockedWebClient; // Mocking WebClient if used in ProductService
//
//    private ProductService productService; // ProductService with mocked dependencies
//
//    @BeforeEach
//    void setUp() {
//        // Instantiate ProductService with mocked dependencies
//        productService = new ProductService(mockedProductRepository, mockedWebClientBuilder);
//
//        // Arrange Mock behaviors
//
//        // Mocking the WebClient.Builder to return a mocked WebClient
//        Mockito.when(mockedWebClientBuilder.build()).thenReturn(mockedWebClient);
//
//        // Mocking the save method to return a Product constructed via the new constructor
//        Product mockSavedProduct = new Product(
//                1,
//                "Test Product",
//                "Test Description",
//                50.0f,
//                10,
//                "Category A",
//                List.of("test-image-url.jpg"),
//                10.0f
//        );
//        Mockito.when(mockedProductRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(mockSavedProduct);
//
//        // Mocking findAll with Pageable using the new constructor
//        List<Product> productList = new ArrayList<>();
//        productList.add(new Product(
//                1,
//                "Product A",
//                "Description A",
//                10.0f,
//                100,
//                "Category A",
//                List.of("imageA.jpg"),
//                0.0f
//        ));
//        productList.add(new Product(
//                2,
//                "Product B",
//                "Description B",
//                20.0f,
//                200,
//                "Category B",
//                List.of("imageB.jpg"),
//                5.0f
//        ));
//        productList.add(new Product(
//                3,
//                "Product C",
//                "Description C",
//                30.0f,
//                300,
//                "Category C",
//                List.of("imageC.jpg"),
//                15.0f
//        ));
//
//        Pageable pageableFirstPage = PageRequest.of(0, 2, Sort.by("name").ascending());
//        Page<Product> firstPage = new PageImpl<>(productList.subList(0, 2), pageableFirstPage, productList.size());
//
//        Pageable pageableSecondPage = PageRequest.of(1, 2, Sort.by("name").ascending());
//        Page<Product> secondPage = new PageImpl<>(productList.subList(2, 3), pageableSecondPage, productList.size());
//
//        // Define behavior for findAll with first page
//        Mockito.when(mockedProductRepository.findAll(pageableFirstPage)).thenReturn(firstPage);
//        // Define behavior for findAll with second page
//        Mockito.when(mockedProductRepository.findAll(pageableSecondPage)).thenReturn(secondPage);
//        // Define behavior for findAll with no products
//        Pageable pageableEmpty = PageRequest.of(0, 10, Sort.by("name").ascending());
//        Page<Product> emptyPage = new PageImpl<>(new ArrayList<>(), pageableEmpty, 0);
//        Mockito.when(mockedProductRepository.findAll(pageableEmpty)).thenReturn(emptyPage);
//
//        // Mocking deleteById to throw exception for non-existing product
//        doThrow(new RuntimeException("Product not found with id: 42"))
//                .when(mockedProductRepository).deleteById(42);
//
//        // Mocking findById for existing product using the new constructor
//        int existingProductId = 1;
//        Product existingProduct = new Product(
//                existingProductId,
//                "Product A",
//                "Description A",
//                10.0f,
//                100,
//                "Category A",
//                List.of("imageA.jpg"),
//                0.0f
//        );
//        Mockito.when(mockedProductRepository.findById(existingProductId)).thenReturn(Optional.of(existingProduct));
//
//        Mockito.when(mockedProductRepository.existsById(existingProductId)).thenReturn(true);
//
//        Mockito.when(mockedProductRepository.existsById(42)).thenReturn(false);
//    }
//
//    @Test
//    void testCreateProduct() {
//        // Arrange: Create a ProductRequestDTO instance
//        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
//                "Test Product",
//                "Test Description",
//                50.0f,
//                10,
//                "Category A",
//                10.0f,
//                List.of("test-image-url.jpg")
//        );
//
//        // Act: Call the service method to create a product
//        Product createdProduct = productService.createProduct(productRequestDTO);
//
//        // Assert: Verify that the product was created as expected
//        assertNotNull(createdProduct, "Created product should not be null");
//        assertEquals("Test Product", createdProduct.getName(), "Product name should match");
//        assertEquals(50.0f, createdProduct.getPrice(), "Product price should match");
//        assertEquals("Test Description", createdProduct.getDescription(), "Product description should match");
//        assertEquals(10, createdProduct.getStockCount(), "Product stock count should match");
//        assertEquals("test-image-url.jpg", createdProduct.getImageURL(), "Product image URL should match");
//        assertEquals(10.0f, createdProduct.getDiscount(), "Product discount should match");
//
//        // Verify interactions with WebClient if applicable
//        Mockito.verify(mockedWebClientBuilder, Mockito.times(1)).build();
//        Mockito.verify(mockedProductRepository, Mockito.times(1)).save(ArgumentMatchers.any(Product.class));
//    }
//
//    @Test
//    void testFindAllWithPaginationFirstPageSuccess() {
//        // Arrange
//        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());
//
//        // Act
//        Page<Product> productPage = productService.findAll(pageable);
//
//        // Assert
//        assertNotNull(productPage, "The returned Page<Product> should not be null");
//        assertEquals(2, productPage.getContent().size(), "The page should contain 2 products");
//        assertEquals(3, productPage.getTotalElements(), "Total elements should be 3");
//        assertEquals(2, productPage.getTotalPages(), "Total pages should be 2");
//        assertEquals("Product A", productPage.getContent().get(0).getName(), "First product should be 'Product A'");
//        assertEquals("Product B", productPage.getContent().get(1).getName(), "Second product should be 'Product B'");
//
//        // Verify repository interaction
//        Mockito.verify(mockedProductRepository, Mockito.times(1)).findAll(pageable);
//    }
//
//    @Test
//    void testFindAllWithPaginationSecondPageSuccess() {
//        // Arrange
//        Pageable pageable = PageRequest.of(1, 2, Sort.by("name").ascending());
//
//        // Act
//        Page<Product> productPage = productService.findAll(pageable);
//
//        // Assert
//        assertNotNull(productPage, "The returned Page<Product> should not be null");
//        assertEquals(1, productPage.getContent().size(), "The page should contain 1 product");
//        assertEquals(3, productPage.getTotalElements(), "Total elements should be 3");
//        assertEquals(2, productPage.getTotalPages(), "Total pages should be 2");
//        assertEquals("Product C", productPage.getContent().get(0).getName(), "First product on second page should be 'Product C'");
//
//        // Verify repository interaction
//        Mockito.verify(mockedProductRepository, Mockito.times(1)).findAll(pageable);
//    }
//
//    @Test
//    void testFindAllWithNoProductsReturnsEmptyPage() {
//        // Arrange
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
//
//        // Act
//        Page<Product> productPage = productService.findAll(pageable);
//
//        // Assert
//        assertNotNull(productPage, "The returned Page<Product> should not be null");
//        assertTrue(productPage.getContent().isEmpty(), "The page should be empty");
//        assertEquals(0, productPage.getTotalElements(), "Total elements should be 0");
//        assertEquals(0, productPage.getTotalPages(), "Total pages should be 0");
//
//        // Verify repository interaction
//        Mockito.verify(mockedProductRepository, Mockito.times(1)).findAll(pageable);
//    }
//
//    @Test
//    void testDeleteProductByNonExistingId(){
//        // Act & Assert: Attempt to delete a non-existing product and expect a RuntimeException
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(42), "Deleting non-existing product should throw RuntimeException");
//        assertEquals("Product not found", exception.getMessage(), "Exception message should match");
//
//        // Verify that deleteById was called with id=42
//       // Mockito.verify(mockedProductRepository, Mockito.times(1)).deleteById(42);
//    }
//
//    @Test
//    void testDeleteProductByIdExistingProduct(){
//        // Arrange
//        int existingProductId = 1;
//
//        // Act & Assert: Attempt to delete an existing product and expect no exception
//        assertDoesNotThrow(() -> productService.deleteProduct(existingProductId), "Deleting existing product should not throw any exception");
//
//        // Verify that deleteById was called with id=1
//        Mockito.verify(mockedProductRepository, Mockito.times(1)).deleteById(existingProductId);
//    }
//}
