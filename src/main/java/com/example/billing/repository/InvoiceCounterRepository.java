package com.example.billing.repository;

import com.example.billing.entity.InvoiceCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface InvoiceCounterRepository extends JpaRepository<InvoiceCounter, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ic FROM InvoiceCounter ic WHERE ic.year = :year")
    Optional<InvoiceCounter> findByYearWithLock(@Param("year") Integer year);
    
    Optional<InvoiceCounter> findByYear(Integer year);
}
