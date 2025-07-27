package com.example.billing.controller;

import com.example.billing.dto.customer.CreateCustomerRequestDto;
import com.example.billing.dto.customer.CustomerResponseDto;
import com.example.billing.dto.customer.UpdateCustomerRequestDto;
import com.example.billing.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        log.info("GET /api/customers - Fetching all customers");
        List<CustomerResponseDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
        log.info("GET /api/customers/{} - Fetching customer by id", id);
        CustomerResponseDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CreateCustomerRequestDto createDto) {
        log.info("POST /api/customers - Creating new customer");
        CustomerResponseDto customer = customerService.createCustomer(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequestDto updateDto) {
        log.info("PUT /api/customers/{} - Updating customer", id);
        CustomerResponseDto customer = customerService.updateCustomer(id, updateDto);
        return ResponseEntity.ok(customer);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("DELETE /api/customers/{} - Deleting customer", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
