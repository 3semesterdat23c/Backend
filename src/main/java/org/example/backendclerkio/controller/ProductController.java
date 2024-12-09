package org.example.backendclerkio.controller;


import org.example.backendclerkio.dto.ProductRequestDTO;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.service.CategoryService;
import org.example.backendclerkio.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/products")
@RestController
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;

    }

    @GetMapping("")
    public Page<Product> findAll(
            Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "false") boolean lowStock,
            @RequestParam(defaultValue = "false") boolean outOfStock,
            @RequestParam(required = false) String sort // e.g., "price,asc" or "price,desc"
    ) {
        // Pass all filters to the service layer
        return productService.getFilteredProducts(pageable, category, search, lowStock, outOfStock, sort);
    }

    @GetMapping("/list")
    public List<Product> getProductList(){
        return productService.getProductList();
    }

    @GetMapping("/categories/{categoryID}")
    public Page<Product> findByCategory(
            Pageable pageable,
            @PathVariable int categoryID,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "false") boolean lowStock,
            @RequestParam(defaultValue = "false") boolean outOfStock
    ) {
        if (category != null) {
            return productService.findByCategory(category, pageable);
        }
        if (!search.isEmpty()) {
            return productService.searchProductsByName(search, pageable);
        } else if (lowStock || outOfStock) {
            return productService.findFilteredProducts(pageable, lowStock, outOfStock);
        }
        return productService.findAllByCategory(pageable, categoryID);
    }



    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        Product createdProduct = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("product deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the booking.");
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody ProductRequestDTO updatedProduct) {
        try {
            Product updated = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the booking.");
        }
    }

    @PutMapping("/{id}/update/stock")
    public ResponseEntity<?> updateStock(@PathVariable int id, @RequestBody int stock) {
        try {
            Product updated = productService.updateStock(id, stock);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the booking.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
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



