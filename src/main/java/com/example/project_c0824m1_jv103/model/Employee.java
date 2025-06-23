package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Employee")
public class Employee {
    
    public enum Role {
        Admin, Sales, Business, Warehouse
    }
    
    public enum Status {
        active, inactive
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Column(name = "email", unique = true, length = 255)
    private String email;
    
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.active;
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Sale> sales;
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Storage> storages;
    
    // Constructors
    public Employee() {}
    
    public Employee(String fullName, String email, String password, String phone, Role role, Status status) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }
    

    
    // Getters and Setters
    public Integer getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public List<Sale> getSales() {
        return sales;
    }
    
    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }
    
    public List<Storage> getStorages() {
        return storages;
    }
    
    public void setStorages(List<Storage> storages) {
        this.storages = storages;
    }
} 