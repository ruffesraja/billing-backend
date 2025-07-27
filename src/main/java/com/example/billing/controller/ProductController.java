package com.example.billing.controller;

import com.example.billing.dto.product.CreateProductRequestDto;
import com.example.billing.dto.product.ProductResponseDto;
import com.example.billing.dto.product.UpdateProductRequestDto;
import com.example.billing.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String search) {
        log.info("GET /api/products - Fetching all products");
        List<ProductResponseDto> products;
        
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search);
        } else {
            products = productService.getAllProducts();
        }
        
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        log.info("GET /api/products/{} - Fetching product by id", id);
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductRequestDto createDto) {
        log.info("POST /api/products - Creating new product");
        ProductResponseDto product = productService.createProduct(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequestDto updateDto) {
        log.info("PUT /api/products/{} - Updating product", id);
        ProductResponseDto product = productService.updateProduct(id, updateDto);
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
