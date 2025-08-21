package com.example.billing.service;

import com.example.billing.entity.InvoiceCounter;
import com.example.billing.repository.InvoiceCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InvoiceNumberService {
    
    @Autowired
    private InvoiceCounterRepository invoiceCounterRepository;
    
    /**
     * Generates the next invoice number in format 000000000001
     * Simple 12-digit sequential number
     */
    @Transactional
    public String generateNextInvoiceNumber() {
        // Get or create the main counter
        InvoiceCounter counter = invoiceCounterRepository.findByYearWithLock(0)
                .orElse(null);
        
        if (counter == null) {
            // Create new counter
            counter = new InvoiceCounter();
            counter.setYear(0); // Use 0 to indicate main counter
            counter.setCurrentSequence(0L);
            counter.setLastUpdated(LocalDateTime.now());
        }
        
        // Increment sequence
        counter.setCurrentSequence(counter.getCurrentSequence() + 1);
        
        // Save the counter
        counter = invoiceCounterRepository.save(counter);
        
        // Generate invoice number in format 000000000001 (12 digits)
        return String.format("%012d", counter.getCurrentSequence());
    }
    
    /**
     * Gets the current sequence number
     */
    public Long getCurrentSequence() {
        return invoiceCounterRepository.findByYear(0)
                .map(InvoiceCounter::getCurrentSequence)
                .orElse(0L);
    }
    
    /**
     * Resets the sequence (useful for testing or manual reset)
     */
    @Transactional
    public void resetSequence(Long newSequence) {
        InvoiceCounter counter = invoiceCounterRepository.findByYear(0)
                .orElse(new InvoiceCounter());
        
        counter.setYear(0);
        counter.setCurrentSequence(newSequence);
        counter.setLastUpdated(LocalDateTime.now());
        
        invoiceCounterRepository.save(counter);
    }
}
