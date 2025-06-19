package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class StorageImportDto {

    @NotNull(message = "Mã sản phẩm là bắt buộc")
    private Integer productId;

    @NotNull(message = "Giá nhập là bắt buộc")
    private Double cost;

    @NotNull(message = "Số lượng là bắt buộc")
    @Positive(message = "Số lượng phải là số dương")
    private Integer quantity;

    @NotNull(message = "Mã nhà cung cấp là bắt buộc")
    private Integer supplierId;

    private Integer employeeId;

    public StorageImportDto() {}

    // Constructor đầy đủ
    public StorageImportDto(Integer productId, Double cost, Integer quantity, Integer supplierId, Integer employeeId) {
        this.productId = productId;
        this.cost = cost;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.employeeId = employeeId;
    }

    // Getters và Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
}