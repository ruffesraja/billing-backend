package com.example.billing.service;

import com.example.billing.dto.client.ClientResponseDto;
import com.example.billing.dto.client.CreateClientRequestDto;
import com.example.billing.dto.client.UpdateClientRequestDto;
import com.example.billing.entity.Client;
import com.example.billing.mapper.ClientMapper;
import com.example.billing.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    
    @Transactional(readOnly = true)
    public List<ClientResponseDto> getAllActiveClients() {
        log.debug("Fetching all active clients");
        List<Client> clients = clientRepository.findByIsActiveTrue();
        return clientMapper.toResponseDtoList(clients);
    }
    
    @Transactional(readOnly = true)
    public ClientResponseDto getClientById(Long id) {
        log.debug("Fetching client with id: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        return clientMapper.toResponseDto(client);
    }
    
    public ClientResponseDto createClient(CreateClientRequestDto requestDto) {
        log.debug("Creating client: {}", requestDto.getClientName());
        
        // Check if client name already exists
        if (clientRepository.existsByClientNameAndIsActiveTrue(requestDto.getClientName())) {
            throw new RuntimeException("Client with name '" + requestDto.getClientName() + "' already exists");
        }
        
        // Check if GST number already exists (if provided)
        if (requestDto.getClientGstNumber() != null && !requestDto.getClientGstNumber().trim().isEmpty()) {
            if (clientRepository.existsByClientGstNumberAndIsActiveTrue(requestDto.getClientGstNumber())) {
                throw new RuntimeException("Client with GST number '" + requestDto.getClientGstNumber() + "' already exists");
            }
        }
        
        Client client = clientMapper.toEntity(requestDto);
        client.setIsActive(true);
        
        Client savedClient = clientRepository.save(client);
        log.debug("Created client with id: {}", savedClient.getId());
        
        return clientMapper.toResponseDto(savedClient);
    }
    
    public ClientResponseDto updateClient(Long id, UpdateClientRequestDto requestDto) {
        log.debug("Updating client with id: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        
        // Check if client name already exists (excluding current client)
        if (requestDto.getClientName() != null && !requestDto.getClientName().equals(client.getClientName())) {
            if (clientRepository.existsByClientNameAndIsActiveTrue(requestDto.getClientName())) {
                throw new RuntimeException("Client with name '" + requestDto.getClientName() + "' already exists");
            }
        }
        
        // Check if GST number already exists (excluding current client)
        if (requestDto.getClientGstNumber() != null && !requestDto.getClientGstNumber().equals(client.getClientGstNumber())) {
            if (clientRepository.existsByClientGstNumberAndIsActiveTrue(requestDto.getClientGstNumber())) {
                throw new RuntimeException("Client with GST number '" + requestDto.getClientGstNumber() + "' already exists");
            }
        }
        
        clientMapper.updateEntity(requestDto, client);
        
        Client savedClient = clientRepository.save(client);
        log.debug("Updated client with id: {}", savedClient.getId());
        
        return clientMapper.toResponseDto(savedClient);
    }
    
    public void deleteClient(Long id) {
        log.debug("Soft deleting client with id: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        
        client.setIsActive(false);
        clientRepository.save(client);
        
        log.debug("Soft deleted client with id: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<ClientResponseDto> searchClients(String searchTerm) {
        log.debug("Searching clients with term: {}", searchTerm);
        List<Client> clients = clientRepository.searchClients(searchTerm);
        return clientMapper.toResponseDtoList(clients);
    }
    
    @Transactional(readOnly = true)
    public List<ClientResponseDto> getClientsByLocation(String location) {
        log.debug("Fetching clients by location: {}", location);
        List<Client> clients = clientRepository.findByClientLocationContainingIgnoreCaseAndIsActiveTrue(location);
        return clientMapper.toResponseDtoList(clients);
    }
    
    @Transactional(readOnly = true)
    public ClientResponseDto getClientByGstNumber(String gstNumber) {
        log.debug("Fetching client by GST number: {}", gstNumber);
        Client client = clientRepository.findByClientGstNumberAndIsActiveTrue(gstNumber)
                .orElseThrow(() -> new RuntimeException("Client not found with GST number: " + gstNumber));
        return clientMapper.toResponseDto(client);
    }
}
