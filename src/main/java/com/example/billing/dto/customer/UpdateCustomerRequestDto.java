package com.example.billing.dto.customer;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequestDto {
    
    private String name;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String phone;
    private String address;
}
