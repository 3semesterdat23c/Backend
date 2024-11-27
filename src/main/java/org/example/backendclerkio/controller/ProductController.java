package org.example.backendclerkio.controller;


import org.example.backendclerkio.dto.ProductDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
@RequestMapping("api")
@RestController
@CrossOrigin
public class ProductController {
private final ProductRepository productRepository;
private final ProductService productService;

public ProductController(ProductService productService, ProductRepository productRepository) {
    this.productService = productService;

}

    @GetMapping("/products")
    public List<Product> getAllProducts(){
    return productService.findAll();
    }

}
