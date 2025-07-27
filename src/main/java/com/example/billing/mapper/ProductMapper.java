package com.example.billing.mapper;

import com.example.billing.dto.product.CreateProductRequestDto;
import com.example.billing.dto.product.ProductResponseDto;
import com.example.billing.dto.product.UpdateProductRequestDto;
import com.example.billing.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductResponseDto toResponseDto(Product product);
    
    List<ProductResponseDto> toResponseDtoList(List<Product> products);
    
    Product toEntity(CreateProductRequestDto createDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateProductRequestDto updateDto, @MappingTarget Product product);
}
