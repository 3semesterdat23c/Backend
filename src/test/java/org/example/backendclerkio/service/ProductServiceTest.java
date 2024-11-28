package org.example.backendclerkio.service;

import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WebClient.Builder webClientBuilder; // Mocking WebClient.Builder

    @Mock
    private WebClient webClient; // Mocking WebClient if used in ProductService

    @InjectMocks
    private ProductService productService; // ProductService with mocked dependencies

    @Test
    void testCreateProduct() {
        // Arrange: Define the behavior of the mocked repository
        Product mockProduct = new Product(1, "Test product", 5.0f, "Test description", 5, "test-image-url");
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // If ProductService uses WebClient, set up its behavior
        when(webClientBuilder.build()).thenReturn(webClient);
        // Optionally, mock any WebClient interactions if necessary
        // e.g., when(webClient.someMethod()).thenReturn(someValue);

        // Act: Call the service method to create a product
        Product createdProduct = productService.createProduct("Test product", 5.0f, "Test description", 5, "test-image-url");

        // Assert: Verify that the product was created as expected
        assertNotNull(createdProduct, "Created product should not be null");
        assertEquals("Test product", createdProduct.getName(), "Product name should match");
        assertEquals(1, createdProduct.getProductId(), "Product ID should match");
        assertEquals(5.0f, createdProduct.getPrice(), 0.001, "Product price should match");
        assertEquals("Test description", createdProduct.getDescription(), "Product description should match");
        assertEquals(5, createdProduct.getStockCount(), "Product stock count should match");
        assertEquals("test-image-url", createdProduct.getImageURL(), "Product image URL should match");


        // Optionally, verify interactions with WebClient if applicable
        verify(webClientBuilder, times(1)).build();
        // e.g., verify(webClient, times(1)).someMethod();
    }
}
