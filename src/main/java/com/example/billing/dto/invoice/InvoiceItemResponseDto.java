package com.example.billing.dto.invoice;

import com.example.billing.dto.product.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponseDto {
    private Long id;
    private ProductResponseDto product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal taxPercent;
    private BigDecimal total;
}
