package com.example.billing.controller;

import com.example.billing.dto.invoice.CreateInvoiceRequestDto;
import com.example.billing.dto.invoice.InvoiceResponseDto;
import com.example.billing.dto.invoice.UpdateInvoiceRequestDto;
import com.example.billing.enums.InvoiceStatus;
import com.example.billing.service.InvoiceService;
import com.example.billing.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    private final PdfService pdfService;
    
    @GetMapping
    public ResponseEntity<List<InvoiceResponseDto>> getAllInvoices(
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String dateFilter) {
        log.info("GET /api/invoices - Fetching invoices with filters: status={}, startDate={}, endDate={}, dateFilter={}", 
                status, startDate, endDate, dateFilter);
        
        List<InvoiceResponseDto> invoices = invoiceService.getInvoicesWithFilters(status, startDate, endDate, dateFilter);
        
        return ResponseEntity.ok(invoices);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> getInvoiceById(@PathVariable Long id) {
        log.info("GET /api/invoices/{} - Fetching invoice by id", id);
        InvoiceResponseDto invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        log.info("GET /api/invoices/{}/pdf - Generating PDF for invoice", id);
        
        try {
            // Get invoice details
            InvoiceResponseDto invoice = invoiceService.getInvoiceById(id);
            
            // Generate PDF
            byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "invoice-" + invoice.getInvoiceNumber() + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error generating PDF for invoice {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
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
