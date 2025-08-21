package com.example.billing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "invoice_counter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCounter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "year", nullable = false, unique = true)
    private Integer year;
    
    @Column(name = "current_sequence", nullable = false)
    private Long currentSequence;
    
    @Column(name = "last_updated", nullable = false)
    private java.time.LocalDateTime lastUpdated;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = java.time.LocalDateTime.now();
    }
}
