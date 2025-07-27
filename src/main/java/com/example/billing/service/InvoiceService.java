package com.example.billing.service;

import com.example.billing.dto.invoice.CreateInvoiceRequestDto;
import com.example.billing.dto.invoice.InvoiceItemDto;
import com.example.billing.dto.invoice.InvoiceResponseDto;
import com.example.billing.dto.invoice.UpdateInvoiceRequestDto;
import com.example.billing.entity.Customer;
import com.example.billing.entity.Invoice;
import com.example.billing.entity.InvoiceItem;
import com.example.billing.entity.Product;
import com.example.billing.enums.InvoiceStatus;
import com.example.billing.mapper.InvoiceMapper;
import com.example.billing.repository.CustomerRepository;
import com.example.billing.repository.InvoiceRepository;
import com.example.billing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceMapper invoiceMapper;
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getAllInvoices() {
        log.debug("Fetching all invoices");
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoiceMapper.toResponseDtoList(invoices);
    }
    
    @Transactional(readOnly = true)
    public InvoiceResponseDto getInvoiceById(Long id) {
        log.debug("Fetching invoice with id: {}", id);
        Invoice invoice = invoiceRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
        return invoiceMapper.toResponseDto(invoice);
    }
    
    public InvoiceResponseDto createInvoice(CreateInvoiceRequestDto createDto) {
        log.debug("Creating new invoice for customer id: {}", createDto.getCustomerId());
        
        // Validate customer exists
        Customer customer = customerRepository.findById(createDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + createDto.getCustomerId()));
        
        // Create invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customer(customer)
                .invoiceDate(createDto.getInvoiceDate())
                .dueDate(createDto.getDueDate())
                .status(InvoiceStatus.UNPAID)
                .notes(createDto.getNotes())
                .totalAmount(BigDecimal.ZERO)
                .invoiceItems(new ArrayList<>())
                .build();
        
        // Create invoice items and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        
        for (InvoiceItemDto itemDto : createDto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDto.getProductId()));
            
            BigDecimal subtotal = product.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            BigDecimal taxAmount = subtotal.multiply(product.getTaxPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal itemTotal = subtotal.add(taxAmount);
            
            InvoiceItem invoiceItem = InvoiceItem.builder()
                    .invoice(invoice)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .taxPercent(product.getTaxPercent())
                    .total(itemTotal)
                    .build();
            
            invoiceItems.add(invoiceItem);
            totalAmount = totalAmount.add(itemTotal);
        }
        
        invoice.setInvoiceItems(invoiceItems);
        invoice.setTotalAmount(totalAmount);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully with id: {} and number: {}", savedInvoice.getId(), savedInvoice.getInvoiceNumber());
        
        return invoiceMapper.toResponseDto(savedInvoice);
    }
    
    public InvoiceResponseDto updateInvoice(Long id, UpdateInvoiceRequestDto updateDto) {
        log.debug("Updating invoice with id: {}", id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));

        // Update basic fields
        invoiceMapper.updateEntityFromDto(updateDto, invoice);

        // Update invoice items if provided
        if (updateDto.getItems() != null) {
            List<InvoiceItem> updatedItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (InvoiceItemDto itemDto : updateDto.getItems()) {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDto.getProductId()));
                BigDecimal subtotal = product.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
                BigDecimal taxAmount = subtotal.multiply(product.getTaxPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal itemTotal = subtotal.add(taxAmount);

                // Try to find existing item for this product
                InvoiceItem existingItem = invoice.getInvoiceItems().stream()
                        .filter(ii -> ii.getProduct().getId().equals(product.getId()))
                        .findFirst().orElse(null);
                if (existingItem != null) {
                    existingItem.setQuantity(itemDto.getQuantity());
                    existingItem.setUnitPrice(product.getUnitPrice());
                    existingItem.setTaxPercent(product.getTaxPercent());
                    existingItem.setTotal(itemTotal);
                    updatedItems.add(existingItem);
                } else {
                    InvoiceItem newItem = InvoiceItem.builder()
                            .invoice(invoice)
                            .product(product)
                            .quantity(itemDto.getQuantity())
                            .unitPrice(product.getUnitPrice())
                            .taxPercent(product.getTaxPercent())
                            .total(itemTotal)
                            .build();
                    updatedItems.add(newItem);
                }
                totalAmount = totalAmount.add(itemTotal);
            }
            // Set updated items directly; orphanRemoval will handle deletions
            invoice.setInvoiceItems(updatedItems);
            invoice.setTotalAmount(totalAmount);
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        log.info("Invoice updated successfully with id: {}", updatedInvoice.getId());
        return invoiceMapper.toResponseDto(updatedInvoice);
    }
    
    public void deleteInvoice(Long id) {
        log.debug("Deleting invoice with id: {}", id);
        
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("Invoice not found with id: " + id);
        }
        
        invoiceRepository.deleteById(id);
        log.info("Invoice deleted successfully with id: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getInvoicesByCustomerId(Long customerId) {
        log.debug("Fetching invoices for customer id: {}", customerId);
        List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);
        return invoiceMapper.toResponseDtoList(invoices);
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getInvoicesByStatus(InvoiceStatus status) {
        log.debug("Fetching invoices with status: {}", status);
        List<Invoice> invoices = invoiceRepository.findByStatus(status);
        return invoiceMapper.toResponseDtoList(invoices);
    }
    
    private String generateInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "INV-" + timestamp;
    }
}
