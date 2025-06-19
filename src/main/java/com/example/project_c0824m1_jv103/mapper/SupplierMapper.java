package com.example.project_c0824m1_jv103.mapper;

import com.example.project_c0824m1_jv103.dto.SupplierDto;
import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
    
    /**
     * Convert Supplier Entity -> SupplierDto
     */
    public SupplierDto toDto(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        
        SupplierDto dto = new SupplierDto();
        dto.setSuplierId(supplier.getSuplierId());
        dto.setSuplierName(supplier.getSuplierName());
        dto.setAddress(supplier.getAddress());
        dto.setPhoneNumber(supplier.getPhoneNumber());
        dto.setEmail(supplier.getEmail());
        dto.setImageUrl(supplier.getImageUrl());
        
        return dto;
    }
    
    /**
     * Convert SupplierDto -> Supplier Entity
     */
    public Supplier toEntity(SupplierDto dto) {
        if (dto == null) {
            return null;
        }
        
        Supplier supplier = new Supplier();
        supplier.setSuplierId(dto.getSuplierId());
        supplier.setSuplierName(dto.getSuplierName());
        supplier.setAddress(dto.getAddress());
        supplier.setPhoneNumber(dto.getPhoneNumber());
        supplier.setEmail(dto.getEmail());
        supplier.setImageUrl(dto.getImageUrl());
        
        return supplier;
    }
    
    /**
     * Update Entity từ DTO (cho update)
     */
    public void updateEntityFromDto(SupplierDto dto, Supplier supplier) {
        if (dto == null || supplier == null) {
            return;
        }
        
        supplier.setSuplierName(dto.getSuplierName());
        supplier.setAddress(dto.getAddress());
        supplier.setPhoneNumber(dto.getPhoneNumber());
        supplier.setEmail(dto.getEmail());
        
        // ImageUrl sẽ được update trong service nếu có file mới
    }
} 