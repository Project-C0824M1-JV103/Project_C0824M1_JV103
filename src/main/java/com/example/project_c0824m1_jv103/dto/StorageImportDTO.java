package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StorageImportDTO {
    @NotNull(message = "ID sản phẩm không được để trống")
    private Integer productId;
    private String productName;
    private String supplierName;
    @NotNull(message = "Số lượng nhập không được để trống")
    @Min(value = 1, message = "Số lượng nhập phải lớn hơn 0")
    private Integer importQuantity;
    @Min(value = 0, message = "Giá nhập phải lớn hơn hoặc bằng 0")
    private Double cost;
    @NotNull(message = "ID nhà cung cấp không được để trống")
    private Integer supplierId;

    public StorageImportDTO() {}

    public StorageImportDTO(Integer productId, String productName, String supplierName, Integer importQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.supplierName = supplierName;
        this.importQuantity = importQuantity;
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

    public Integer getImportQuantity() {
        return importQuantity;
    }

    public void setImportQuantity(Integer importQuantity) {
        this.importQuantity = importQuantity;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
}