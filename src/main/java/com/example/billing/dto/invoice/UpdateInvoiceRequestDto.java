package com.example.billing.dto.invoice;

import com.example.billing.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceRequestDto {
    
    // Customer details
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    private String customerEmail;
    
    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
    
    private String customerAddress;
    private String customerGstNumber;
    
    // Invoice details
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @NotNull(message = "Status is required")
    private InvoiceStatus status;
    
    private String notes;
    
    // GST settings
    private Boolean gstApplicable;
    
    @DecimalMin(value = "0.0", message = "CGST rate must be greater than or equal to 0")
    private BigDecimal cgstRate;
    
    @DecimalMin(value = "0.0", message = "SGST rate must be greater than or equal to 0")
    private BigDecimal sgstRate;
    
    // Additional charges
    private String transportChargesLabel;
    
    @DecimalMin(value = "0.0", message = "Transport charges must be greater than or equal to 0")
    private BigDecimal transportCharges;
    
    private String miscChargesLabel;
    
    @DecimalMin(value = "0.0", message = "Miscellaneous charges must be greater than or equal to 0")
    private BigDecimal miscCharges;
    
    // Line items
    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<InvoiceItemDto> items;
}
