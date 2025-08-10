package com.example.billing.controller;

import com.itextpdf.html2pdf.HtmlConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/test")
@Slf4j
@CrossOrigin(origins = "*")
public class TestController {
    
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateTestPdf() {
        log.info("GET /api/test/pdf - Generating test PDF");
        
        try {
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Test PDF</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; }
                        .header { text-align: center; color: #2563eb; margin-bottom: 30px; }
                        .content { line-height: 1.6; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>Test PDF Generation</h1>
                        <p>This is a simple test to verify PDF generation works</p>
                    </div>
                    <div class="content">
                        <h2>Business Information</h2>
                        <p><strong>Business Name:</strong> Test Business</p>
                        <p><strong>Owner:</strong> Test Owner</p>
                        <p><strong>Date:</strong> August 10, 2025</p>
                        
                        <h2>Test Invoice</h2>
                        <table border="1" style="width: 100%; border-collapse: collapse;">
                            <tr>
                                <th style="padding: 8px;">Item</th>
                                <th style="padding: 8px;">Quantity</th>
                                <th style="padding: 8px;">Price</th>
                                <th style="padding: 8px;">Total</th>
                            </tr>
                            <tr>
                                <td style="padding: 8px;">Test Product</td>
                                <td style="padding: 8px;">1</td>
                                <td style="padding: 8px;">₹100.00</td>
                                <td style="padding: 8px;">₹100.00</td>
                            </tr>
                        </table>
                        
                        <p style="margin-top: 20px;"><strong>Total: ₹100.00</strong></p>
                        <p style="margin-top: 30px; font-style: italic;">PDF generation is working correctly!</p>
                    </div>
                </body>
                </html>
                """;
            
            // Convert HTML to PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            
            byte[] pdfBytes = outputStream.toByteArray();
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test-invoice.pdf");
            headers.setContentLength(pdfBytes.length);
            
            log.info("Test PDF generated successfully, size: {} bytes", pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error generating test PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
