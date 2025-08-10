package com.example.billing.service;

import com.example.billing.dto.invoice.InvoiceResponseDto;
import com.example.billing.dto.owner.OwnerResponseDto;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {
    
    private final TemplateEngine templateEngine;
    private final OwnerService ownerService;
    
    public byte[] generateInvoicePdf(InvoiceResponseDto invoice) {
        log.debug("Generating PDF for invoice: {}", invoice.getInvoiceNumber());
        
        try {
            // Get owner details for invoice header
            OwnerResponseDto owner = null;
            try {
                owner = ownerService.getActiveOwner();
            } catch (RuntimeException e) {
                log.warn("No active owner found, using default values");
            }
            
            // Create Thymeleaf context
            Context context = new Context(Locale.getDefault());
            context.setVariable("invoice", invoice);
            context.setVariable("owner", owner);
            
            // Use the calculated amounts from the invoice entity
            context.setVariable("subtotal", invoice.getSubtotalAmount());
            context.setVariable("cgstRate", invoice.getCgstRate());
            context.setVariable("sgstRate", invoice.getSgstRate());
            context.setVariable("cgstAmount", invoice.getCgstAmount());
            context.setVariable("sgstAmount", invoice.getSgstAmount());
            context.setVariable("totalGstAmount", invoice.getTotalGstAmount());
            context.setVariable("gstApplicable", invoice.getGstApplicable());
            context.setVariable("transportChargesLabel", invoice.getTransportChargesLabel());
            context.setVariable("transportCharges", invoice.getTransportCharges());
            context.setVariable("miscChargesLabel", invoice.getMiscChargesLabel());
            context.setVariable("miscCharges", invoice.getMiscCharges());
            
            // Process the template
            String htmlContent = templateEngine.process("invoice-template", context);
            
            // Convert HTML to PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            
            log.debug("PDF generated successfully for invoice: {}", invoice.getInvoiceNumber());
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating PDF for invoice: {}", invoice.getInvoiceNumber(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}
