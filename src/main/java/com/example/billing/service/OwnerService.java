package com.example.billing.service;

import com.example.billing.dto.owner.CreateOwnerRequestDto;
import com.example.billing.dto.owner.OwnerResponseDto;
import com.example.billing.dto.owner.UpdateOwnerRequestDto;
import com.example.billing.entity.Owner;
import com.example.billing.mapper.OwnerMapper;
import com.example.billing.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OwnerService {
    
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    
    @Transactional(readOnly = true)
    public List<OwnerResponseDto> getAllOwners() {
        log.debug("Fetching all owners");
        List<Owner> owners = ownerRepository.findAll();
        return ownerMapper.toResponseDtoList(owners);
    }
    
    @Transactional(readOnly = true)
    public OwnerResponseDto getOwnerById(Long id) {
        log.debug("Fetching owner with id: {}", id);
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + id));
        return ownerMapper.toResponseDto(owner);
    }
    
    @Transactional(readOnly = true)
    public OwnerResponseDto getActiveOwner() {
        log.debug("Fetching active owner");
        Owner owner = ownerRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active owner found"));
        return ownerMapper.toResponseDto(owner);
    }
    
    public OwnerResponseDto createOwner(CreateOwnerRequestDto requestDto) {
        log.debug("Creating owner: {}", requestDto.getBusinessName());
        
        // Deactivate existing active owner if any
        ownerRepository.findByIsActiveTrue().ifPresent(existingOwner -> {
            existingOwner.setIsActive(false);
            ownerRepository.save(existingOwner);
            log.debug("Deactivated existing owner: {}", existingOwner.getBusinessName());
        });
        
        Owner owner = ownerMapper.toEntity(requestDto);
        owner.setIsActive(true); // New owner is active by default
        
        // Ensure contact_number is not null
        if (owner.getContactNumber() == null || owner.getContactNumber().trim().isEmpty()) {
            if (owner.getPhone() != null && !owner.getPhone().trim().isEmpty()) {
                owner.setContactNumber(owner.getPhone());
            } else {
                throw new RuntimeException("Contact number is required");
            }
        }
        
        // Set default values if not provided
        if (owner.getDefaultCgstRate() == null) {
            owner.setDefaultCgstRate(9.0);
        }
        if (owner.getDefaultSgstRate() == null) {
            owner.setDefaultSgstRate(9.0);
        }
        if (owner.getInvoicePrefix() == null || owner.getInvoicePrefix().trim().isEmpty()) {
            owner.setInvoicePrefix("INV");
        }
        if (owner.getNextInvoiceNumber() == null) {
            owner.setNextInvoiceNumber(1L);
        }
        
        Owner savedOwner = ownerRepository.save(owner);
        log.debug("Created owner with id: {}", savedOwner.getId());
        
        return ownerMapper.toResponseDto(savedOwner);
    }
    
    public OwnerResponseDto updateOwner(Long id, UpdateOwnerRequestDto requestDto) {
        log.debug("Updating owner with id: {}", id);
        
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + id));
        
        ownerMapper.updateEntityFromDto(requestDto, owner);
        
        // Ensure contact_number is not null after update
        if (owner.getContactNumber() == null || owner.getContactNumber().trim().isEmpty()) {
            if (owner.getPhone() != null && !owner.getPhone().trim().isEmpty()) {
                owner.setContactNumber(owner.getPhone());
            } else {
                throw new RuntimeException("Contact number is required");
            }
        }
        
        Owner savedOwner = ownerRepository.save(owner);
        log.debug("Updated owner with id: {}", savedOwner.getId());
        
        return ownerMapper.toResponseDto(savedOwner);
    }
    
    public void deleteOwner(Long id) {
        log.debug("Deleting owner with id: {}", id);
        
        if (!ownerRepository.existsById(id)) {
            throw new RuntimeException("Owner not found with id: " + id);
        }
        
        ownerRepository.deleteById(id);
        log.debug("Deleted owner with id: {}", id);
    }
    
    public OwnerResponseDto setActiveOwner(Long id) {
        log.debug("Setting owner {} as active", id);
        
        // Deactivate all owners
        List<Owner> allOwners = ownerRepository.findAll();
        allOwners.forEach(owner -> owner.setIsActive(false));
        ownerRepository.saveAll(allOwners);
        
        // Activate the specified owner
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + id));
        owner.setIsActive(true);
        
        Owner savedOwner = ownerRepository.save(owner);
        log.debug("Set owner {} as active", savedOwner.getBusinessName());
        
        return ownerMapper.toResponseDto(savedOwner);
    }
}
