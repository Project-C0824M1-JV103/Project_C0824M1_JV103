package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.*;

public class StorageImportDTO {
    @NotNull(message = "Vui lòng chọn sản phẩm")
    private Integer productId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;

    @NotNull(message = "Nhà cung cấp không được để trống")
    private Integer supplierId;

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String supplierName;

    @NotNull(message = "Số lượng nhập không được để trống")
    @Min(value = 1, message = "Số lượng nhập phải lớn hơn 0")
    private Integer importQuantity;

    @NotNull(message = "Giá nhập không được để trống")
    @DecimalMin(value = "10000", message = "Giá nhập phải từ 10,000 VND trở lên")
    @DecimalMax(value = "1000000000", message = "Giá nhập không được vượt quá 1,000,000,000 VND")
    private Double cost;

    // Getters and Setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Integer getImportQuantity() { return importQuantity; }
    public void setImportQuantity(Integer importQuantity) { this.importQuantity = importQuantity; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
}