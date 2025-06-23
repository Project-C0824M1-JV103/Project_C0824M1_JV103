package com.example.project_c0824m1_jv103.mapper;

import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.ProductImages;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    
    /**
     * Convert Product Entity -> ProductDTO (cho form edit)
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setSize(product.getSize());
        dto.setCameraBack(product.getCameraBack());
        dto.setCameraFront(product.getCameraFront());
        dto.setCpu(product.getCpu());
        dto.setMemory(product.getMemory());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());
        
        // Map category và supplier
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getCategoryId());
            dto.setCategoryName(product.getCategory().getCategoryName());
        }

        if (product.getSupplier() != null) {
            dto.setSupplierId(product.getSupplier().getSuplierId());
            dto.setSupplierName(product.getSupplier().getSuplierName());
        }
        
        // Map images
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            List<String> imageUrls = product.getProductImages().stream()
                    .map(ProductImages::getImageUrl)
                    .filter(url -> url != null && !url.isEmpty())
                    .collect(Collectors.toList());
            dto.setExistingImageUrls(imageUrls);
        }
        
        return dto;
    }
    
    /**
     * Convert ProductDTO -> Product Entity (cho save)
     */
    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setSize(dto.getSize());
        product.setCameraBack(dto.getCameraBack());
        product.setCameraFront(dto.getCameraFront());
        product.setCpu(dto.getCpu());
        product.setMemory(dto.getMemory());
        product.setDescription(dto.getDescription());
        product.setQuantity(dto.getQuantity());
        
        // Category và Supplier sẽ được set trong service
        return product;
    }
    
    /**
     * Update Entity từ DTO (cho update)
     */
    public void updateEntityFromDTO(ProductDTO dto, Product product) {
        if (dto == null || product == null) {
            return;
        }
        
        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setSize(dto.getSize());
        product.setCameraBack(dto.getCameraBack());
        product.setCameraFront(dto.getCameraFront());
        product.setCpu(dto.getCpu());
        product.setMemory(dto.getMemory());
        product.setDescription(dto.getDescription());
        product.setQuantity(dto.getQuantity());
        
        // Category và Supplier sẽ được update trong service nếu cần
    }
} 