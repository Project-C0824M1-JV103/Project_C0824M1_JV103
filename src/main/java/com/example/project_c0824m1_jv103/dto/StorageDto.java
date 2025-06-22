package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class StorageDto {

    private Integer storageId;

    @NotNull(message = "Vui lòng chọn sản phẩm")
    private Integer productId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải từ 1 trở lên")
    @Max(value = 10000, message = "Số lượng không được vượt quá 10,000")
    private Integer quantity;

    @NotNull(message = "Giá nhập không được để trống")
    @DecimalMin(value = "1000", message = "Giá nhập phải từ 1,000 VND trở lên")
    @DecimalMax(value = "1000000000", message = "Giá nhập không được vượt quá 1,000,000,000 VND")
    private Double cost;

    @NotNull(message = "Vui lòng chọn nhân viên")
    private Integer employeeId;

    private LocalDateTime transactionDate;

    // Getters and Setters
    public Integer getStorageId() { return storageId; }
    public void setStorageId(Integer storageId) { this.storageId = storageId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}