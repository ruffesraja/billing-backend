package com.example.billing.repository;

import com.example.billing.entity.Invoice;
import com.example.billing.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByCustomerId(Long customerId);
    
    List<Invoice> findByStatus(InvoiceStatus status);
    
    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.invoiceItems ii LEFT JOIN FETCH ii.product WHERE i.id = :id")
    Optional<Invoice> findByIdWithItems(@Param("id") Long id);
    
    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.customer WHERE i.id = :id")
    Optional<Invoice> findByIdWithCustomer(@Param("id") Long id);
}
