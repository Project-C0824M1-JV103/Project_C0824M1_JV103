package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id")
    private Integer storageId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull(message = "Sản phẩm không được để trống")
    private Product product;

    @Column(name = "cost", nullable = false)
    @NotNull(message = "Giá nhập không được để trống")
    @DecimalMin(value = "1000", message = "Giá nhập phải từ 1,000 VND trở lên")
    @DecimalMax(value = "1000000000", message = "Giá nhập không vượt quá 1,000,000,000 VND")
    private Double cost;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @NotNull(message = "Nhân viên không được để trống")
    private Employee employee;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải từ 1 trở lên")
    @Max(value = 10000, message = "Số lượng không vượt quá 10,000")
    private Integer quantity;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();
    // Constructors
    public Storage() {}
    
    public Storage(Product product, Double cost, Employee employee, Integer quantity) {
        this.product = product;
        this.cost = cost;
        this.employee = employee;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Integer getStorageId() {
        return storageId;
    }
    
    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Double getCost() {
        return cost;
    }
    
    public void setCost(Double cost) {
        this.cost = cost;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    
    // public Double getTotalCost() {
    //     return quantity != null && cost != null ? quantity * cost : 0.0;
    // }
} 