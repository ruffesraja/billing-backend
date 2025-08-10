package com.example.billing.repository;

import com.example.billing.entity.Invoice;
import com.example.billing.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByStatus(InvoiceStatus status);
    
    // Date filtering methods
    List<Invoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Invoice> findByStatusAndInvoiceDateBetween(InvoiceStatus status, LocalDate startDate, LocalDate endDate);
    
    List<Invoice> findByInvoiceDate(LocalDate invoiceDate);
    
    List<Invoice> findByStatusAndInvoiceDate(InvoiceStatus status, LocalDate invoiceDate);
    
    // Due date filtering methods
    List<Invoice> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Invoice> findByStatusAndDueDateBetween(InvoiceStatus status, LocalDate startDate, LocalDate endDate);
    
    // Find invoices by customer name (useful for search)
    List<Invoice> findByCustomerNameContainingIgnoreCase(String customerName);
    
    // Find invoices by customer email
    List<Invoice> findByCustomerEmail(String customerEmail);
    
    // Check if any invoice line items contain a specific product (using JSON query)
    @Query(value = "SELECT COUNT(*) > 0 FROM invoices WHERE line_items @> '[{\"productId\": ?1}]'", nativeQuery = true)
    boolean existsByProductId(Long productId);
    
    // Find invoices containing a specific product (using JSON query)
    @Query(value = "SELECT * FROM invoices WHERE line_items @> '[{\"productId\": ?1}]'", nativeQuery = true)
    List<Invoice> findByProductId(Long productId);
    
    // Search invoices by product name in line items (using JSON query)
    @Query(value = "SELECT * FROM invoices WHERE line_items @> '[{\"productName\": ?1}]'", nativeQuery = true)
    List<Invoice> findByProductName(String productName);
}
