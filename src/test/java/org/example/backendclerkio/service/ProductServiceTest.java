package org.example.backendclerkio.service;

import jakarta.transaction.Transactional;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Mock
    private ProductRepository mockedProductRepository;

    @Mock
    private WebClient.Builder mockedWebClientBuilder; // Mocking WebClient.Builder

    @Mock
    private WebClient mockedWebClient; // Mocking WebClient if used in ProductService

    private ProductService productService; // ProductService with mocked dependencies

    @BeforeEach
    void setUp() {
        // Instantiate ProductService with mocked dependencies
        productService = new ProductService(mockedProductRepository, mockedWebClientBuilder);

        // Arrange Mock behaviors

        // Mocking the WebClient.Builder to return a mocked WebClient
        Mockito.when(mockedWebClientBuilder.build()).thenReturn(mockedWebClient);

        // Mocking the save method
        Product mockSavedProduct = new Product(1, "Test Product", 50.0f, "Test Description", 10, "test-image-url.jpg");
        Mockito.when(mockedProductRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(mockSavedProduct);

        // Mocking findAll with Pageable
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1, "Product A", 10.0f, "Description A", 100, "imageA.jpg"));
        productList.add(new Product(2, "Product B", 20.0f, "Description B", 200, "imageB.jpg"));
        productList.add(new Product(3, "Product C", 30.0f, "Description C", 300, "imageC.jpg"));

        Pageable pageableFirstPage = PageRequest.of(0, 2, Sort.by("name").ascending());
        Page<Product> firstPage = new PageImpl<>(productList.subList(0, 2), pageableFirstPage, productList.size());

        Pageable pageableSecondPage = PageRequest.of(1, 2, Sort.by("name").ascending());
        Page<Product> secondPage = new PageImpl<>(productList.subList(2, 3), pageableSecondPage, productList.size());

        // Define behavior for findAll with first page
        Mockito.when(mockedProductRepository.findAll(pageableFirstPage)).thenReturn(firstPage);
        // Define behavior for findAll with second page
        Mockito.when(mockedProductRepository.findAll(pageableSecondPage)).thenReturn(secondPage);
        // Define behavior for findAll with no products
        Pageable pageableEmpty = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Product> emptyPage = new PageImpl<>(new ArrayList<>(), pageableEmpty, 0);
        Mockito.when(mockedProductRepository.findAll(pageableEmpty)).thenReturn(emptyPage);

        // Mocking deleteById to throw exception for non-existing product
        doThrow(new RuntimeException("Product not found with id: 42")).when(mockedProductRepository).deleteById(42);
    }

    @Test
    void testCreateProduct() {
        // Act: Call the service method to create a product
        Product createdProduct = productService.createProduct("Test Product", 50.0f, "Test Description", 10, "test-image-url.jpg");

        // Assert: Verify that the product was created as expected
        assertNotNull(createdProduct, "Created product should not be null");
        assertEquals("Test Product", createdProduct.getName(), "Product name should match");
        assertEquals(1, createdProduct.getProductId(), "Product ID should match");
        assertEquals(50.0f, createdProduct.getPrice());
        assertEquals("Test Description", createdProduct.getDescription(), "Product description should match");
        assertEquals(10, createdProduct.getStockCount(), "Product stock count should match");
        assertEquals("test-image-url.jpg", createdProduct.getImageURL(), "Product image URL should match");

        // Verify interactions with WebClient if applicable
        Mockito.verify(mockedWebClientBuilder, Mockito.times(1)).build();
        Mockito.verify(mockedProductRepository, Mockito.times(1)).save(ArgumentMatchers.any(Product.class));
    }

    @Test
    void testFindAll_WithPagination_FirstPage_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        // Act
        Page<Product> productPage = productService.findAll(pageable);

        // Assert
        assertNotNull(productPage, "The returned Page<Product> should not be null");
        assertEquals(2, productPage.getContent().size(), "The page should contain 2 products");
        assertEquals(3, productPage.getTotalElements(), "Total elements should be 3");
        assertEquals(2, productPage.getTotalPages(), "Total pages should be 2");
        assertEquals("Product A", productPage.getContent().get(0).getName(), "First product should be 'Product A'");
        assertEquals("Product B", productPage.getContent().get(1).getName(), "Second product should be 'Product B'");

        // Verify repository interaction
        Mockito.verify(mockedProductRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    void testFindAll_WithPagination_SecondPage_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 2, Sort.by("name").ascending());

        // Act
        Page<Product> productPage = productService.findAll(pageable);

        // Assert
        assertNotNull(productPage, "The returned Page<Product> should not be null");
        assertEquals(1, productPage.getContent().size(), "The page should contain 1 product");
        assertEquals(3, productPage.getTotalElements(), "Total elements should be 3");
        assertEquals(2, productPage.getTotalPages(), "Total pages should be 2");
        assertEquals("Product C", productPage.getContent().get(0).getName(), "First product on second page should be 'Product C'");

        // Verify repository interaction
        Mockito.verify(mockedProductRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    void testFindAll_WithNoProducts_ReturnsEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        // Act
        Page<Product> productPage = productService.findAll(pageable);

        // Assert
        assertNotNull(productPage, "The returned Page<Product> should not be null");
        assertTrue(productPage.getContent().isEmpty(), "The page should be empty");
        assertEquals(0, productPage.getTotalElements(), "Total elements should be 0");
        assertEquals(0, productPage.getTotalPages(), "Total pages should be 0");

        // Verify repository interaction
        Mockito.verify(mockedProductRepository, Mockito.times(1)).findAll(pageable);
    }

}
