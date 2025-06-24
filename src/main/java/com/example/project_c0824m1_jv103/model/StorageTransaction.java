package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "storage_transaction")
public class StorageTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull(message = "Sản phẩm không được để trống")
    private Product product;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity; // Cho phép âm (xuất kho) và dương (nhập kho)

    @Column(name = "cost")
    private Double cost;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(name = "description")
    private String description;

    // Enum cho loại giao dịch
    public enum TransactionType {
        IMPORT, EXPORT
    }

    // Constructors
    public StorageTransaction() {}

    public StorageTransaction(Product product, Integer quantity, Double cost, Employee employee, 
                            TransactionType transactionType, String description) {
        this.product = product;
        this.quantity = quantity;
        this.cost = cost;
        this.employee = employee;
        this.transactionType = transactionType;
        this.description = description;
    }

    // Getters and Setters
    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 