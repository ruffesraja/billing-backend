package com.example.billing.controller;

import com.example.billing.dto.owner.CreateOwnerRequestDto;
import com.example.billing.dto.owner.OwnerResponseDto;
import com.example.billing.dto.owner.UpdateOwnerRequestDto;
import com.example.billing.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {
    
    private final OwnerService ownerService;
    
    @GetMapping
    public ResponseEntity<List<OwnerResponseDto>> getAllOwners() {
        log.info("GET /api/owners - Fetching all owners");
        List<OwnerResponseDto> owners = ownerService.getAllOwners();
        return ResponseEntity.ok(owners);
    }
    
    @GetMapping("/active")
    public ResponseEntity<OwnerResponseDto> getActiveOwner() {
        log.info("GET /api/owners/active - Fetching active owner");
        try {
            OwnerResponseDto owner = ownerService.getActiveOwner();
            return ResponseEntity.ok(owner);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponseDto> getOwnerById(@PathVariable Long id) {
        log.info("GET /api/owners/{} - Fetching owner by id", id);
        OwnerResponseDto owner = ownerService.getOwnerById(id);
        return ResponseEntity.ok(owner);
    }
    
    @PostMapping
    public ResponseEntity<OwnerResponseDto> createOwner(@Valid @RequestBody CreateOwnerRequestDto createDto) {
        log.info("POST /api/owners - Creating new owner");
        OwnerResponseDto owner = ownerService.createOwner(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(owner);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponseDto> updateOwner(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOwnerRequestDto updateDto) {
        log.info("PUT /api/owners/{} - Updating owner", id);
        OwnerResponseDto owner = ownerService.updateOwner(id, updateDto);
        return ResponseEntity.ok(owner);
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<OwnerResponseDto> setActiveOwner(@PathVariable Long id) {
        log.info("PUT /api/owners/{}/activate - Setting owner as active", id);
        OwnerResponseDto owner = ownerService.setActiveOwner(id);
        return ResponseEntity.ok(owner);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        log.info("DELETE /api/owners/{} - Deleting owner", id);
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}
