package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer saleId;

    @Column(name = "orderCode", nullable = false, unique = true)
    private String orderCode;

    @Column(name = "saleDate", nullable = false)
    private LocalDateTime saleDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentMethod", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "totalAmount", nullable = false)
    private Double totalAmount;

    @Column(name = "printInvoice", nullable = false)
    private Boolean printInvoice = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetail> saleDetails;

    public Sale() {
    }

    public Sale(Integer saleId, String orderCode, LocalDateTime saleDate, PaymentMethod paymentMethod, Double totalAmount, Boolean printInvoice, Status status, Customer customer, Employee employee, List<SaleDetail> saleDetails) {
        this.saleId = saleId;
        this.orderCode = orderCode;
        this.saleDate = saleDate;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.printInvoice = printInvoice;
        this.status = status;
        this.customer = customer;
        this.employee = employee;
        this.saleDetails = saleDetails;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Boolean getPrintInvoice() {
        return printInvoice;
    }

    public void setPrintInvoice(Boolean printInvoice) {
        this.printInvoice = printInvoice;
    }

    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
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

    public List<SaleDetail> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetail> saleDetails) {
        this.saleDetails = saleDetails;
    }

    public enum Status {
        PENDING("pending"),
        COMPLETED("completed"), 
        CANCELLED("cancelled");
        
        private final String value;
        
        Status(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    

    public enum PaymentMethod {
        CASH("cash"),
        BANK_TRANSFER("bank_transfer");
        
        private final String value;
        
        PaymentMethod(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
}
