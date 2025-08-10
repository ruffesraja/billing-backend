package com.example.billing.repository;

import com.example.billing.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    
    Optional<Owner> findByIsActiveTrue();
    
    boolean existsByIsActiveTrue();
}
