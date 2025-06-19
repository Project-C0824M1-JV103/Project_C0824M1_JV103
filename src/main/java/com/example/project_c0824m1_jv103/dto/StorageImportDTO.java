package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StorageImportDTO {

    @NotNull(message = "ID sản phẩm là bắt buộc")
    private Integer productId;

    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer importQuantity;

    @NotNull(message = "Giá nhập là bắt buộc")
    @Min(value = 0, message = "Giá nhập không được âm")
    private Double cost;

    @NotNull(message = "ID nhân viên là bắt buộc")
    private Integer employeeId;

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
}