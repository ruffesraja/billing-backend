package com.example.billing.entity;

import com.example.billing.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;
    
    // Customer details (direct entry)
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "customer_email")
    private String customerEmail;
    
    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;
    
    @Column(name = "customer_address", length = 500)
    private String customerAddress;
    
    @Column(name = "customer_gst_number")
    private String customerGstNumber;
    
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;
    
    // Amount breakdown
    @Column(name = "subtotal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;
    
    // Configurable GST Settings per invoice
    @Builder.Default
    @Column(name = "gst_applicable", nullable = false)
    private Boolean gstApplicable = false;
    
    @Column(name = "cgst_rate", precision = 5, scale = 2)
    private BigDecimal cgstRate;
    
    @Column(name = "sgst_rate", precision = 5, scale = 2)
    private BigDecimal sgstRate;
    
    @Column(name = "cgst_amount", precision = 12, scale = 2)
    private BigDecimal cgstAmount;
    
    @Column(name = "sgst_amount", precision = 12, scale = 2)
    private BigDecimal sgstAmount;
    
    @Column(name = "total_gst_amount", precision = 12, scale = 2)
    private BigDecimal totalGstAmount;
    
    // Additional Charges
    @Column(name = "transport_charges_label")
    private String transportChargesLabel;
    
    @Column(name = "transport_charges", precision = 12, scale = 2)
    private BigDecimal transportCharges;
    
    @Column(name = "misc_charges_label")
    private String miscChargesLabel;
    
    @Column(name = "misc_charges", precision = 12, scale = 2)
    private BigDecimal miscCharges;
    
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;
    
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Line items stored as JSON (eliminates need for separate table)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "line_items", columnDefinition = "jsonb")
    private List<InvoiceLineItem> lineItems;
    
    // Inner class for line items (stored as JSON) - enhanced with serial number
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceLineItem {
        private Integer serialNumber; // S.No.
        private Long productId; // Can be null for custom products
        private String productName;
        private String description; // Particulars/Description
        private Integer quantity;
        private BigDecimal unitPrice; // Rate
        private BigDecimal lineTotal; // Amount (quantity * unitPrice)
        private Boolean isCustomProduct; // Flag to indicate custom product
    }
}
