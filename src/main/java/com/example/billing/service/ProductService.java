package com.example.billing.service;

import com.example.billing.dto.product.CreateProductRequestDto;
import com.example.billing.dto.product.ProductResponseDto;
import com.example.billing.dto.product.UpdateProductRequestDto;
import com.example.billing.entity.Product;
import com.example.billing.mapper.ProductMapper;
import com.example.billing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        log.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        return productMapper.toResponseDtoList(products);
    }
    
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toResponseDto(product);
    }
    
    public ProductResponseDto createProduct(CreateProductRequestDto createDto) {
        log.debug("Creating new product with name: {}", createDto.getName());
        
        Product product = productMapper.toEntity(createDto);
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return productMapper.toResponseDto(savedProduct);
    }
    
    public ProductResponseDto updateProduct(Long id, UpdateProductRequestDto updateDto) {
        log.debug("Updating product with id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        productMapper.updateEntityFromDto(updateDto, product);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Product updated successfully with id: {}", updatedProduct.getId());
        return productMapper.toResponseDto(updatedProduct);
    }
    
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(String name) {
        log.debug("Searching products with name containing: {}", name);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return productMapper.toResponseDtoList(products);
    }
}
