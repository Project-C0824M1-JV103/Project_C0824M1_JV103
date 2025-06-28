package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmployeePersonalPasswordDto {
    @NotBlank(message = "Mật khẩu cũ không được để trống!")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống!")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự!")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống!")
    @Size(min = 6, message = "Xác nhận mật khẩu phải có ít nhất 6 ký tự!")
    private String confirmPassword;


    public EmployeePersonalPasswordDto() {
    }

    public EmployeePersonalPasswordDto(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
