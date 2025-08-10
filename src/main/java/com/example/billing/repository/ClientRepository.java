package com.example.billing.repository;

import com.example.billing.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    List<Client> findByIsActiveTrue();
    
    List<Client> findByClientNameContainingIgnoreCaseAndIsActiveTrue(String clientName);
    
    Optional<Client> findByClientGstNumberAndIsActiveTrue(String gstNumber);
    
    List<Client> findByClientLocationContainingIgnoreCaseAndIsActiveTrue(String location);
    
    @Query("SELECT c FROM Client c WHERE " +
           "(LOWER(c.clientName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.clientLocation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.clientGstNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "c.isActive = true")
    List<Client> searchClients(@Param("searchTerm") String searchTerm);
    
    boolean existsByClientNameAndIsActiveTrue(String clientName);
    
    boolean existsByClientGstNumberAndIsActiveTrue(String gstNumber);
}
