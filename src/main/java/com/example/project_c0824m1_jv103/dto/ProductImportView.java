package com.example.project_c0824m1_jv103.dto;

import com.example.project_c0824m1_jv103.model.ProductImages;

import java.util.List;

public class ProductImportView {
    private StorageImportDTO storageImportDTO;
    private List<ProductImages> productImages;

    // Constructors
    public ProductImportView() {}
    public ProductImportView(StorageImportDTO dto, List<ProductImages> images) {
        this.storageImportDTO = dto;
        this.productImages = images;
    }

    public StorageImportDTO getStorageImportDTO() {
        return storageImportDTO;
    }

    public void setStorageImportDTO(StorageImportDTO storageImportDTO) {
        this.storageImportDTO = storageImportDTO;
    }

    public List<ProductImages> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImages> productImages) {
        this.productImages = productImages;
    }
}

