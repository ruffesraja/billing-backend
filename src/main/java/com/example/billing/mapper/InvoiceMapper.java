package com.example.billing.mapper;

import com.example.billing.dto.invoice.InvoiceItemResponseDto;
import com.example.billing.dto.invoice.InvoiceResponseDto;
import com.example.billing.dto.invoice.UpdateInvoiceRequestDto;
import com.example.billing.entity.Invoice;
import com.example.billing.entity.InvoiceItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, ProductMapper.class})
public interface InvoiceMapper {
    
    InvoiceResponseDto toResponseDto(Invoice invoice);
    
    List<InvoiceResponseDto> toResponseDtoList(List<Invoice> invoices);
    
    InvoiceItemResponseDto toInvoiceItemResponseDto(InvoiceItem invoiceItem);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateInvoiceRequestDto updateDto, @MappingTarget Invoice invoice);
}
