package com.example.billing.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequestDto {
    
    @NotBlank(message = "Client name is required")
    private String clientName;
    
    private String clientGstNumber;
    
    private String clientLocation;
    
    private String clientBranch;
    
    private String contactPerson;
    
    private String phoneNumber;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String addressLine1;
    
    private String addressLine2;
    
    private String city;
    
    private String state;
    
    private String pincode;
    
    private String businessType;
    
    private String industry;
    
    private String paymentTerms;
    
    private Double creditLimit;
}
