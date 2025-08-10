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
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Client Basic Information
    @Column(name = "client_name", nullable = false)
    private String clientName;
    
    @Column(name = "client_gst_number")
    private String clientGstNumber;
    
    @Column(name = "client_location")
    private String clientLocation;
    
    @Column(name = "client_branch")
    private String clientBranch;
    
    // Contact Information
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
    
    // Address Details
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Column(name = "address_line2")
    private String addressLine2;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "pincode")
    private String pincode;
    
    // Business Details
    @Column(name = "business_type")
    private String businessType;
    
    @Column(name = "industry")
    private String industry;
    
    // Payment Terms
    @Column(name = "payment_terms")
    private String paymentTerms;
    
    @Column(name = "credit_limit")
    private Double creditLimit;
    
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
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null && !addressLine1.trim().isEmpty()) {
            address.append(addressLine1);
        }
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(addressLine2);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(state);
        }
        if (pincode != null && !pincode.trim().isEmpty()) {
            if (address.length() > 0) address.append(" - ");
            address.append(pincode);
        }
        return address.toString();
    }
    
    // Helper method to get display name with location
    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder(clientName);
        if (clientLocation != null && !clientLocation.trim().isEmpty()) {
            displayName.append(" (").append(clientLocation).append(")");
        }
        if (clientBranch != null && !clientBranch.trim().isEmpty()) {
            displayName.append(" - ").append(clientBranch);
        }
        return displayName.toString();
    }
}
