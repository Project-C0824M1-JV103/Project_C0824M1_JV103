package com.example.project_c0824m1_jv103.dto;

import java.math.BigDecimal;

public class CustomerReportDto {

    private Integer customerId;
    private String customerName;
    private String phoneNumber;
    private String email;
    private Integer totalSales;
    private BigDecimal totalAmount;
    private Integer age;
    private String gender;

    // Constructors
    public CustomerReportDto() {}

    public CustomerReportDto(Integer customerId, String customerName, String phoneNumber, String email, Integer totalSales, BigDecimal totalAmount, Integer age, String gender) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.totalSales = totalSales;
        this.totalAmount = totalAmount;
        this.age = age;
        this.gender = gender;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Integer totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}