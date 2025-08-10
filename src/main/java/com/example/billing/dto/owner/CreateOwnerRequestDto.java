package com.example.billing.dto.owner;

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
public class CreateOwnerRequestDto {
    
    @NotBlank(message = "Business name is required")
    private String businessName;
    
    @NotBlank(message = "Owner name is required")
    private String ownerName;
    
    private String gstNumber;
    
    // Address Details
    private String addressLine1;
    private String addressLine2;
    private String area;
    private String city;
    private String pincode;
    private String state;
    
    // Contact Information - contactNumber is required
    @NotBlank(message = "Contact number is required")
    private String contactNumber;
    
    @Email(message = "Please provide a valid email address")
    private String email;
    
    private String website;
    
    // Primary Bank Account Details
    private String primaryBankName;
    private String primaryAccountNumber;
    private String primaryIfscCode;
    private String primaryAccountHolderName;
    
    // Secondary Bank Account Details
    private String secondaryBankName;
    private String secondaryAccountNumber;
    private String secondaryIfscCode;
    private String secondaryAccountHolderName;
    
    // Invoice Configuration
    private String invoiceHeaderText;
    private String invoiceFooterText;
    private String businessLogoPath;
    private Double defaultCgstRate;
    private Double defaultSgstRate;
    private String invoicePrefix;
    private Long nextInvoiceNumber;
    private String termsAndConditions;
    private String paymentTerms;
    
    // Legacy fields for backward compatibility
    private String phone;
    private String address;
    private String taxId;
    private String registrationNumber;
    private String bankName;
    private String accountNumber;
    private String routingNumber;
    private String accountHolderName;
    private String logoUrl;
    private String invoiceTerms;
    private String invoiceFooter;
}
