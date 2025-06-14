package com.example.project_c0824m1_jv103.dto;

import com.example.project_c0824m1_jv103.model.Employee.Role;
import com.example.project_c0824m1_jv103.model.Employee.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EmployeePersonalDto {

    private Integer employeeId;

    @NotBlank(message = "không được để trống.")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\p{L}]*$", message = "không được chứa ký tự đặc biệt.")
    private String fullName;

    @Email(message = "không hợp lệ.")
    @NotBlank(message = "không được để trống.")
    private String email;

    @NotBlank(message = "không được để trống.")
    @Pattern(regexp = "\\d{10}", message = "phải đủ 10 chữ số và chỉ chứa số.")
    private String phone;

    private Role role;

    // Getters & Setters
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
