package com.example.billing.dto.invoice;

import com.example.billing.dto.customer.CustomerResponseDto;
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
    private CustomerResponseDto customer;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private BigDecimal totalAmount;
    private String notes;
    private LocalDateTime createdAt;
    private List<InvoiceItemResponseDto> invoiceItems;
}
