package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EmployeeEditDto {
    private Integer employeeId;

    @NotBlank(message = "Vui lòng nhập tên đầy đủ!")
    @Size(max = 50, message = "Tên không được vượt quá 50 ký tự")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên không được chứa số hoặc kí tự đặc biệt!")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập email đầy đủ!")
    @Size(max = 50, message = "Email không được vượt quá 50 ký tự")
//    @Email(message = "Vui lòng nhập email đúng định dạng!")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Vui lòng nhập email đúng định dạng!")
    private String email;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Vui lòng nhập số điện thoại với 10 hoặc 11 chữ số!")
    private String phone;

    @NotBlank(message = "Chọn vị trí")
    private String role;

    public EmployeeEditDto() {}

    public EmployeeEditDto(Integer employeeId, String fullName, String email, String phone, String role) {
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
