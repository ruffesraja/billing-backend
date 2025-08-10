package com.example.billing.dto.invoice;

import com.example.billing.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDto {
    private Long id;
    private String invoiceNumber;
    
    // Customer details stored directly in invoice
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private String customerGstNumber;
    
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    
    // Amount breakdown
    private BigDecimal subtotalAmount;
    
    // GST details - separate CGST and SGST rates
    private Boolean gstApplicable;
    private BigDecimal cgstRate;
    private BigDecimal sgstRate;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal totalGstAmount;
    
    // Additional charges
    private String transportChargesLabel;
    private BigDecimal transportCharges;
    private String miscChargesLabel;
    private BigDecimal miscCharges;
    
    private BigDecimal totalAmount;
    private String notes;
    private LocalDateTime createdAt;
    
    // Line items (from JSON field)
    private List<InvoiceLineItemDto> lineItems;
    
    // DTO for line items - enhanced with serial number
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceLineItemDto {
        private Integer serialNumber; // S.No.
        private Long productId;
        private String productName;
        private String description; // Particulars/Description
        private Integer quantity;
        private BigDecimal unitPrice; // Rate
        private BigDecimal lineTotal; // Amount
        private Boolean isCustomProduct;
    }
}
