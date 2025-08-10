package com.example.billing.mapper;

import com.example.billing.dto.invoice.InvoiceResponseDto;
import com.example.billing.dto.invoice.UpdateInvoiceRequestDto;
import com.example.billing.entity.Invoice;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    
    // Map Invoice entity to response DTO
    @Mapping(source = "lineItems", target = "lineItems")
    InvoiceResponseDto toResponseDto(Invoice invoice);
    
    List<InvoiceResponseDto> toResponseDtoList(List<Invoice> invoices);
    
    // Map Invoice.InvoiceLineItem to InvoiceResponseDto.InvoiceLineItemDto
    InvoiceResponseDto.InvoiceLineItemDto toLineItemDto(Invoice.InvoiceLineItem lineItem);
    
    List<InvoiceResponseDto.InvoiceLineItemDto> toLineItemDtoList(List<Invoice.InvoiceLineItem> lineItems);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceNumber", ignore = true)
    @Mapping(target = "subtotalAmount", ignore = true)
    @Mapping(target = "cgstAmount", ignore = true)
    @Mapping(target = "sgstAmount", ignore = true)
    @Mapping(target = "totalGstAmount", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lineItems", ignore = true)
    void updateEntityFromDto(UpdateInvoiceRequestDto updateDto, @MappingTarget Invoice invoice);
}
