package com.example.billing.dto.invoice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDto {
    
    // Serial number for the item (S.No.)
    private Integer serialNumber;
    
    // Optional - can be null for custom products
    private Long productId;
    
    @NotBlank(message = "Product name is required")
    private String productName;
    
    // Description/Particulars (e.g., SALES, TECHNICIAN, SERVICE ADVISOR, CHUDITHAR Material, T-SHIRT)
    private String description;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    // Flag to indicate if this is a custom product (not from products table)
    @Builder.Default
    private Boolean isCustomProduct = false;
}
