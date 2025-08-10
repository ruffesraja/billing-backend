package com.example.billing.dto.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequestDto {
    
    // Customer details (direct entry)
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    // Email is optional
    @Email(message = "Customer email must be valid")
    private String customerEmail;
    
    // Mobile number is required
    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
    
    private String customerAddress;
    
    private String customerGstNumber;
    
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    private String notes;
    
    // GST Settings - now with separate CGST and SGST rates
    @Builder.Default
    private Boolean gstApplicable = false;
    
    @DecimalMin(value = "0.0", message = "CGST rate must be greater than or equal to 0")
    private BigDecimal cgstRate;
    
    @DecimalMin(value = "0.0", message = "SGST rate must be greater than or equal to 0")
    private BigDecimal sgstRate;
    
    // Additional Charges
    private String transportChargesLabel;
    
    @DecimalMin(value = "0.0", message = "Transport charges must be greater than or equal to 0")
    private BigDecimal transportCharges;
    
    private String miscChargesLabel;
    
    @DecimalMin(value = "0.0", message = "Miscellaneous charges must be greater than or equal to 0")
    private BigDecimal miscCharges;
    
    @NotEmpty(message = "Invoice items are required")
    @Valid
    private List<InvoiceItemDto> items;
}
