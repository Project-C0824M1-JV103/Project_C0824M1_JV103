package com.example.project_c0824m1_jv103.dto;

import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.model.Employee;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class PendingSaleDto implements Serializable {
    private SaleDto saleDto;
    private Employee employee;
    private Customer customer;
    private LocalDateTime createdAt;
    private String sessionKey;

    public PendingSaleDto() {
        this.createdAt = LocalDateTime.now();
    }

    public PendingSaleDto(SaleDto saleDto, Employee employee, Customer customer, String sessionKey) {
        this.saleDto = saleDto;
        this.employee = employee;
        this.customer = customer;
        this.sessionKey = sessionKey;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public SaleDto getSaleDto() {
        return saleDto;
    }

    public void setSaleDto(SaleDto saleDto) {
        this.saleDto = saleDto;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    // Kiểm tra xem session có còn hợp lệ không (ví dụ: 30 phút)
    public boolean isValid() {
        return createdAt.plusMinutes(30).isAfter(LocalDateTime.now());
    }
} 