package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StorageExportDTO {
    private Integer storageId;
    private Integer productId;
    private String productName;
    private String supplierName;
    private Integer remainingQuantity;
    
    @NotNull(message = "Số lượng xuất không được để trống")
    @Min(value = 1, message = "Số lượng xuất phải lớn hơn 0")
    private Integer exportQuantity;

    public StorageExportDTO() {
    }

    public StorageExportDTO(Integer storageId, Integer productId, String productName, String supplierName, Integer remainingQuantity, Integer exportQuantity) {
        this.storageId = storageId;
        this.productId = productId;
        this.productName = productName;
        this.supplierName = supplierName;
        this.remainingQuantity = remainingQuantity;
        this.exportQuantity = exportQuantity;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public Integer getExportQuantity() {
        return exportQuantity;
    }

    public void setExportQuantity(Integer exportQuantity) {
        this.exportQuantity = exportQuantity;
    }
} 