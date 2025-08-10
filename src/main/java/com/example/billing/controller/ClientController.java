package com.example.billing.controller;

import com.example.billing.dto.client.ClientResponseDto;
import com.example.billing.dto.client.CreateClientRequestDto;
import com.example.billing.dto.client.UpdateClientRequestDto;
import com.example.billing.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ClientController {
    
    private final ClientService clientService;
    
    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        log.info("GET /api/clients - Fetching all active clients");
        List<ClientResponseDto> clients = clientService.getAllActiveClients();
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        log.info("GET /api/clients/{} - Fetching client by id", id);
        ClientResponseDto client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }
    
    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody CreateClientRequestDto requestDto) {
        log.info("POST /api/clients - Creating new client: {}", requestDto.getClientName());
        ClientResponseDto client = clientService.createClient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientRequestDto requestDto) {
        log.info("PUT /api/clients/{} - Updating client", id);
        ClientResponseDto client = clientService.updateClient(id, requestDto);
        return ResponseEntity.ok(client);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.info("DELETE /api/clients/{} - Deleting client", id);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ClientResponseDto>> searchClients(@RequestParam String q) {
        log.info("GET /api/clients/search?q={} - Searching clients", q);
        List<ClientResponseDto> clients = clientService.searchClients(q);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/location/{location}")
    public ResponseEntity<List<ClientResponseDto>> getClientsByLocation(@PathVariable String location) {
        log.info("GET /api/clients/location/{} - Fetching clients by location", location);
        List<ClientResponseDto> clients = clientService.getClientsByLocation(location);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/gst/{gstNumber}")
    public ResponseEntity<ClientResponseDto> getClientByGstNumber(@PathVariable String gstNumber) {
        log.info("GET /api/clients/gst/{} - Fetching client by GST number", gstNumber);
        ClientResponseDto client = clientService.getClientByGstNumber(gstNumber);
        return ResponseEntity.ok(client);
    }
}
