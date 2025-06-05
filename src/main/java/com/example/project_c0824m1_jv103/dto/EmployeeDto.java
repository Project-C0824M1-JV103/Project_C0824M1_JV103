package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UniqueElements;

public class EmployeeDto {

    private Integer employeeId;

    @NotBlank(message = "Nhập đầy đủ họ và tên!")
    private String fullName;

    @NotBlank(message = "Nhập email người dùng!")
    private String email;

    @NotBlank(message = "Nhập số điện thoại!")
    private String phone;

    @NotBlank(message = "Chọn vai trò!")
    private String role;

    public EmployeeDto() {}

    public EmployeeDto(Integer employeeId, String fullName, String email, String phone, String role) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
