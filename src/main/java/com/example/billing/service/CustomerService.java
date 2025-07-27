package com.example.billing.service;

import com.example.billing.dto.customer.CreateCustomerRequestDto;
import com.example.billing.dto.customer.CustomerResponseDto;
import com.example.billing.dto.customer.UpdateCustomerRequestDto;
import com.example.billing.entity.Customer;
import com.example.billing.mapper.CustomerMapper;
import com.example.billing.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        log.debug("Fetching all customers");
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toResponseDtoList(customers);
    }
    
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {
        log.debug("Fetching customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return customerMapper.toResponseDto(customer);
    }
    
    public CustomerResponseDto createCustomer(CreateCustomerRequestDto createDto) {
        log.debug("Creating new customer with email: {}", createDto.getEmail());
        
        if (customerRepository.existsByEmail(createDto.getEmail())) {
            throw new RuntimeException("Customer already exists with email: " + createDto.getEmail());
        }
        
        Customer customer = customerMapper.toEntity(createDto);
        Customer savedCustomer = customerRepository.save(customer);
        
        log.info("Customer created successfully with id: {}", savedCustomer.getId());
        return customerMapper.toResponseDto(savedCustomer);
    }
    
    public CustomerResponseDto updateCustomer(Long id, UpdateCustomerRequestDto updateDto) {
        log.debug("Updating customer with id: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(updateDto.getEmail())) {
                throw new RuntimeException("Customer already exists with email: " + updateDto.getEmail());
            }
        }
        
        customerMapper.updateEntityFromDto(updateDto, customer);
        Customer updatedCustomer = customerRepository.save(customer);
        
        log.info("Customer updated successfully with id: {}", updatedCustomer.getId());
        return customerMapper.toResponseDto(updatedCustomer);
    }
    
    public void deleteCustomer(Long id) {
        log.debug("Deleting customer with id: {}", id);
        
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        
        customerRepository.deleteById(id);
        log.info("Customer deleted successfully with id: {}", id);
    }
}
