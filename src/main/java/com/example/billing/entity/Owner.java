package com.example.billing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "owners")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Owner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Business Information
    @Column(name = "business_name", nullable = false)
    private String businessName;
    
    @Column(name = "owner_name", nullable = false)
    private String ownerName;
    
    @Column(name = "gst_number")
    private String gstNumber;
    
    // Address Details
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Column(name = "address_line2")
    private String addressLine2;
    
    @Column(name = "area")
    private String area;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "pincode")
    private String pincode;
    
    @Column(name = "state")
    private String state;
    
    // Contact Information - contact_number is required
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "website")
    private String website;
    
    // Primary Bank Account Details
    @Column(name = "primary_bank_name")
    private String primaryBankName;
    
    @Column(name = "primary_account_number")
    private String primaryAccountNumber;
    
    @Column(name = "primary_ifsc_code")
    private String primaryIfscCode;
    
    @Column(name = "primary_account_holder_name")
    private String primaryAccountHolderName;
    
    // Secondary Bank Account Details (Corporation Bank, etc.)
    @Column(name = "secondary_bank_name")
    private String secondaryBankName;
    
    @Column(name = "secondary_account_number")
    private String secondaryAccountNumber;
    
    @Column(name = "secondary_ifsc_code")
    private String secondaryIfscCode;
    
    @Column(name = "secondary_account_holder_name")
    private String secondaryAccountHolderName;
    
    // Invoice Configuration
    @Column(name = "invoice_header_text")
    private String invoiceHeaderText;
    
    @Column(name = "invoice_footer_text")
    private String invoiceFooterText;
    
    @Column(name = "business_logo_path")
    private String businessLogoPath;
    
    // Header Configuration (God Symbol and Name)
    @Column(name = "header_god_symbol")
    private String headerGodSymbol;
    
    @Column(name = "header_name")
    private String headerName;
    
    // Default Tax Rates
    @Builder.Default
    @Column(name = "default_cgst_rate")
    private Double defaultCgstRate = 9.0;
    
    @Builder.Default
    @Column(name = "default_sgst_rate")
    private Double defaultSgstRate = 9.0;
    
    // Terms and Conditions
    @Column(name = "terms_and_conditions", length = 1000)
    private String termsAndConditions;
    
    @Column(name = "payment_terms")
    private String paymentTerms;
    
    // Signature (stored as base64)
    @Column(name = "signature_base64", columnDefinition = "TEXT")
    private String signatureBase64;
    
    // Legacy fields for backward compatibility (keep existing data working)
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "address", length = 1000)
    private String address;
    
    @Column(name = "tax_id")
    private String taxId;
    
    @Column(name = "registration_number")
    private String registrationNumber;
    
    @Column(name = "bank_name")
    private String bankName;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "routing_number")
    private String routingNumber;
    
    @Column(name = "account_holder_name")
    private String accountHolderName;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @Column(name = "invoice_terms", length = 2000)
    private String invoiceTerms;
    
    @Column(name = "invoice_footer", length = 1000)
    private String invoiceFooter;
    
    // Status and Metadata
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Helper method to get full address
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        if (addressLine1 != null && !addressLine1.trim().isEmpty()) {
            fullAddress.append(addressLine1);
        }
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(addressLine2);
        }
        if (area != null && !area.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(area);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(city);
        }
        if (pincode != null && !pincode.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(" - ");
            fullAddress.append(pincode);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(state);
        }
        return fullAddress.toString();
    }
    
    // Helper method to get contact info
    public String getContactInfo() {
        StringBuilder contact = new StringBuilder();
        if (contactNumber != null && !contactNumber.trim().isEmpty()) {
            contact.append("Ph: ").append(contactNumber);
        }
        if (email != null && !email.trim().isEmpty()) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append("Email: ").append(email);
        }
        if (website != null && !website.trim().isEmpty()) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append("Web: ").append(website);
        }
        return contact.toString();
    }
    
    // Method to ensure contact_number is never null
    @PrePersist
    @PreUpdate
    private void ensureContactNumber() {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            if (phone != null && !phone.trim().isEmpty()) {
                contactNumber = phone;
            } else {
                contactNumber = "N/A"; // Default value to prevent null constraint violation
            }
        }
    }
}
