package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

public class EmployeeDto {

    private Integer employeeId;

    @NotBlank(message = "Nhập tên đầy đủ!")
    private String fullName;

    @NotBlank(message = "Nhập email người dùng!")
    @UniqueElements(message = "Email đã tồn tại!")
    private String email;

    @NotBlank(message = "Nhập mật khẩu!")
    @Size(min = 3, max = 8, message = "Mật khẩu phải từ 3 đến 8 ký tự!")
    private String password;

    @NotBlank(message = "Nhập mật khẩu!")
    @Size(min = 3, max = 8, message = "Mật khẩu phải từ 3 đến 8 ký tự!")
    private String passwordConfirm;

    @NotBlank(message = "Nhập số điện thoại!")
    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải có đúng 10 chữ số!")
    @UniqueElements(message = "Số điện thoại đã tồn tại!")
    private String phone;

    @NotBlank(message = "Chọn vị trí")
    private String role;

    public EmployeeDto() {}

    public EmployeeDto(Integer employeeId, String fullName, String email, String password, String passwordConfirm, String phone, String role) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
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
