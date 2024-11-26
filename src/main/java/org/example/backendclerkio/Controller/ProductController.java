package org.example.backendclerkio.controller;


import org.example.backendclerkio.dto.ProductDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.service.ProductService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@CrossOrigin
public class ProductController {

private final ProductService productService;

public ProductController(ProductService productService) {
    this.productService = productService;
}

  /*  @GetMapping("/getProducts")
    public Mono<ProductDTO> getProducts() {
        return productService.getProductsFromIkea();
    }*/


    @GetMapping("/getProducts")
    public Mono<ProductsResponseDTO> getProducts() {
        return productService.getProductsFromDummy();
    }

}
