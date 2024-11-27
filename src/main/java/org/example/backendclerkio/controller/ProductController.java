package org.example.backendclerkio.controller;


import org.example.backendclerkio.dto.ProductDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
@RequestMapping("api/v1")
@RestController
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;

    }

    @GetMapping("/products")
    public Page<Product> getAllProducts(Pageable pageable) {
       return productService.findAll(pageable);
}


    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStockCount(),
                product.getImageURL()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProduct(@RequestParam int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("product deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the booking.");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestParam int id, @RequestBody Product updatedProduct) {
        try {
            Product updated = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the booking.");
        }
    }

    @GetMapping("/product")
    public ResponseEntity<?> getProductById(@RequestParam int id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found.");
            }
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}



