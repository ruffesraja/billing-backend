package com.example.billing.config;

import com.example.billing.entity.Customer;
import com.example.billing.entity.Product;
import com.example.billing.repository.CustomerRepository;
import com.example.billing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (customerRepository.count() == 0) {
            loadSampleData();
        }
    }
    
    private void loadSampleData() {
        log.info("Loading sample data...");
        
        // Create sample customers
        Customer customer1 = Customer.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+1-555-0123")
                .address("123 Main St, Anytown, USA")
                .build();
        
        Customer customer2 = Customer.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phone("+1-555-0456")
                .address("456 Oak Ave, Somewhere, USA")
                .build();
        
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        
        // Create sample products
        Product product1 = Product.builder()
                .name("Laptop")
                .description("High-performance laptop for business use")
                .unitPrice(new BigDecimal("999.99"))
                .taxPercent(new BigDecimal("8.25"))
                .build();
        
        Product product2 = Product.builder()
                .name("Wireless Mouse")
                .description("Ergonomic wireless mouse")
                .unitPrice(new BigDecimal("29.99"))
                .taxPercent(new BigDecimal("8.25"))
                .build();
        
        Product product3 = Product.builder()
                .name("Office Chair")
                .description("Comfortable ergonomic office chair")
                .unitPrice(new BigDecimal("199.99"))
                .taxPercent(new BigDecimal("8.25"))
                .build();
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        
        log.info("Sample data loaded successfully!");
    }
}
