package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Customer")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;
    
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "birthday_date")
    private LocalDate birthdayDate;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Sale> sales;
    
    // Constructors
    public Customer() {}
    
    public Customer(String customerName, String phoneNumber, String address, LocalDate birthdayDate, String email) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.birthdayDate = birthdayDate;
        this.email = email;
    }
    
    // Getters and Setters
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getBirthdayDate() {
        return birthdayDate;
    }
    
    public void setBirthdayDate(LocalDate birthdayDate) {
        this.birthdayDate = birthdayDate;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<Sale> getSales() {
        return sales;
    }
    
    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }
} 