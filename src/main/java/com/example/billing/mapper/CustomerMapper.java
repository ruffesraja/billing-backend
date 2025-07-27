package com.example.billing.mapper;

import com.example.billing.dto.customer.CreateCustomerRequestDto;
import com.example.billing.dto.customer.CustomerResponseDto;
import com.example.billing.dto.customer.UpdateCustomerRequestDto;
import com.example.billing.entity.Customer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    CustomerResponseDto toResponseDto(Customer customer);
    
    List<CustomerResponseDto> toResponseDtoList(List<Customer> customers);
    
    Customer toEntity(CreateCustomerRequestDto createDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateCustomerRequestDto updateDto, @MappingTarget Customer customer);
}
