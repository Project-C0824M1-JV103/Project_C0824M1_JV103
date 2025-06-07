package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Sale")
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Integer saleId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate = LocalDateTime.now();
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetails> saleDetails;
    
    // Constructors
    public Sale() {}
    
    public Sale(Customer customer, Employee employee, BigDecimal amount) {
        this.customer = customer;
        this.employee = employee;
        this.amount = amount;
    }
    
    // Getters and Setters
    public Integer getSaleId() {
        return saleId;
    }
    
    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public LocalDateTime getSaleDate() {
        return saleDate;
    }
    
    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public List<SaleDetails> getSaleDetails() {
        return saleDetails;
    }
    
    public void setSaleDetails(List<SaleDetails> saleDetails) {
        this.saleDetails = saleDetails;
    }
} 