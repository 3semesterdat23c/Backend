package org.example.backendclerkio;


import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.service.ProductService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequestMapping("api")
@RestController
@CrossOrigin
public class ProductController {
private final ProductRepository productRepository;
private final ProductService productService;

public ProductController(ProductService productService, ProductRepository productRepository) {
    this.productService = productService;
    this.productRepository = productRepository;
}

    @GetMapping("/products")
    public List<Product> getAllProducts(){
    return productRepository.findAll();
    }

}
