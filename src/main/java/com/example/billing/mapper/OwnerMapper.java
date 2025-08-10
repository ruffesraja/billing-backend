package com.example.billing.mapper;

import com.example.billing.dto.owner.CreateOwnerRequestDto;
import com.example.billing.dto.owner.OwnerResponseDto;
import com.example.billing.dto.owner.UpdateOwnerRequestDto;
import com.example.billing.entity.Owner;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    
    @Mapping(target = "fullAddress", expression = "java(owner.getFullAddress())")
    @Mapping(target = "contactInfo", expression = "java(owner.getContactInfo())")
    OwnerResponseDto toResponseDto(Owner owner);
    
    List<OwnerResponseDto> toResponseDtoList(List<Owner> owners);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Owner toEntity(CreateOwnerRequestDto createDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateOwnerRequestDto updateDto, @MappingTarget Owner owner);
}
