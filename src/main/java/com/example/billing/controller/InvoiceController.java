package com.example.billing.controller;

import com.example.billing.dto.invoice.CreateInvoiceRequestDto;
import com.example.billing.dto.invoice.InvoiceResponseDto;
import com.example.billing.dto.invoice.UpdateInvoiceRequestDto;
import com.example.billing.enums.InvoiceStatus;
import com.example.billing.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    @GetMapping
    public ResponseEntity<List<InvoiceResponseDto>> getAllInvoices(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) InvoiceStatus status) {
        log.info("GET /api/invoices - Fetching invoices");
        
        List<InvoiceResponseDto> invoices;
        
        if (customerId != null) {
            invoices = invoiceService.getInvoicesByCustomerId(customerId);
        } else if (status != null) {
            invoices = invoiceService.getInvoicesByStatus(status);
        } else {
            invoices = invoiceService.getAllInvoices();
        }
        
        return ResponseEntity.ok(invoices);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> getInvoiceById(@PathVariable Long id) {
        log.info("GET /api/invoices/{} - Fetching invoice by id", id);
        InvoiceResponseDto invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }
    
    @PostMapping
    public ResponseEntity<InvoiceResponseDto> createInvoice(@Valid @RequestBody CreateInvoiceRequestDto createDto) {
        log.info("POST /api/invoices - Creating new invoice");
        InvoiceResponseDto invoice = invoiceService.createInvoice(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInvoiceRequestDto updateDto) {
        log.info("PUT /api/invoices/{} - Updating invoice", id);
        InvoiceResponseDto invoice = invoiceService.updateInvoice(id, updateDto);
        return ResponseEntity.ok(invoice);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        log.info("DELETE /api/invoices/{} - Deleting invoice", id);
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
