package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductRetailDto {
    private Integer productId;

    private String productName;

    private Double price;

    @NotNull(message = "Vui lòng nhập giá")
    @DecimalMin(value = "10000", message = "Giá nhập phải từ 10,000 VND trở lên")
    @DecimalMax(value = "1000000000", message = "Giá nhập không được vượt quá 1,000,000,000 VND")
    private Double retailPrice;

    private String memory;

    private Integer quantity;

    private String categoryName;

    private String supplierName;

    public ProductRetailDto() {
    }

    public ProductRetailDto(Integer productId, String productName, Double price, Double retailPrice, String memory, Integer quantity, String categoryName, String supplierName) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.retailPrice = retailPrice;
        this.memory = memory;
        this.quantity = quantity;
        this.categoryName = categoryName;
        this.supplierName = supplierName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(Double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
