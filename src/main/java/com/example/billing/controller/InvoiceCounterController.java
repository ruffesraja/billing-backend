package com.example.billing.controller;

import com.example.billing.service.InvoiceNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invoice-counters")
@RequiredArgsConstructor
@Slf4j
public class InvoiceCounterController {

    private final InvoiceNumberService invoiceNumberService;

    /**
     * Get current sequence
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentSequence() {
        try {
            Long currentSequence = invoiceNumberService.getCurrentSequence();
            Map<String, Object> response = new HashMap<>();
            response.put("currentSequence", currentSequence);
            response.put("nextInvoiceNumber", String.format("%012d", currentSequence + 1));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting current sequence", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reset sequence (admin only)
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetSequence(@RequestParam Long newSequence) {
        try {
            invoiceNumberService.resetSequence(newSequence);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Sequence reset successfully");
            response.put("newSequence", newSequence);
            response.put("nextInvoiceNumber", String.format("%012d", newSequence + 1));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error resetting sequence", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Generate next invoice number (for testing)
     */
    @PostMapping("/generate-next")
    public ResponseEntity<Map<String, Object>> generateNextInvoiceNumber() {
        try {
            String nextInvoiceNumber = invoiceNumberService.generateNextInvoiceNumber();
            Map<String, Object> response = new HashMap<>();
            response.put("nextInvoiceNumber", nextInvoiceNumber);
            response.put("year", Integer.parseInt(nextInvoiceNumber.substring(0, 4)));
            response.put("sequence", Long.parseLong(nextInvoiceNumber.substring(4)));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating next invoice number", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
