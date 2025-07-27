package com.example.billing.dto.invoice;

import com.example.billing.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceRequestDto {
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private String notes;
    private List<InvoiceItemDto> items;
}
