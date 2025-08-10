package com.example.billing.mapper;

import com.example.billing.dto.client.ClientResponseDto;
import com.example.billing.dto.client.CreateClientRequestDto;
import com.example.billing.dto.client.UpdateClientRequestDto;
import com.example.billing.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {
    
    @Mapping(target = "fullAddress", expression = "java(client.getFullAddress())")
    @Mapping(target = "displayName", expression = "java(client.getDisplayName())")
    ClientResponseDto toResponseDto(Client client);
    
    List<ClientResponseDto> toResponseDtoList(List<Client> clients);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client toEntity(CreateClientRequestDto requestDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateClientRequestDto requestDto, @MappingTarget Client client);
}
