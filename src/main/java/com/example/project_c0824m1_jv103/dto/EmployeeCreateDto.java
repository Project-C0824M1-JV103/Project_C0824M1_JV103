package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.NotBlank;

public class EmployeeCreateDto {

    private Integer employeeId;

    @NotBlank(message = "Vui lòng nhập tên đầy đủ")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập email người dùng")
    private String email;

    private String password;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    private String phone;

    @NotBlank(message = "Chọn vị trí")
    private String role;

    public EmployeeCreateDto() {}

    public EmployeeCreateDto(Integer employeeId, String fullName, String email, String password, String phone, String role) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
