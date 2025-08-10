package com.example.billing.config;

import com.example.billing.entity.Product;
import com.example.billing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final ProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            loadSampleProducts();
        }
    }
    
    private void loadSampleProducts() {
        log.info("Loading sample products...");
        
        // Create sample products (name only)
        Product product1 = Product.builder()
                .name("Laptop")
                .build();
        
        Product product2 = Product.builder()
                .name("Desktop Computer")
                .build();
        
        Product product3 = Product.builder()
                .name("Monitor")
                .build();
        
        Product product4 = Product.builder()
                .name("Keyboard")
                .build();
        
        Product product5 = Product.builder()
                .name("Mouse")
                .build();
        
        Product product6 = Product.builder()
                .name("Printer")
                .build();
        
        Product product7 = Product.builder()
                .name("Software License")
                .build();
        
        Product product8 = Product.builder()
                .name("Consulting Services")
                .build();
        
        Product product9 = Product.builder()
                .name("Training Services")
                .build();
        
        Product product10 = Product.builder()
                .name("Support Services")
                .build();
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
        productRepository.save(product5);
        productRepository.save(product6);
        productRepository.save(product7);
        productRepository.save(product8);
        productRepository.save(product9);
        productRepository.save(product10);
        
        log.info("Sample products loaded successfully!");
    }
}
