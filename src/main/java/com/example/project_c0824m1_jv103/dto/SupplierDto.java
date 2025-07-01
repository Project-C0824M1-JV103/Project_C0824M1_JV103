package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class SupplierDto {
    
    private Integer suplierId;
    
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    @Size(min = 2, max = 50, message = "Tên nhà cung cấp quá dài")
    private String suplierName;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    private String address;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 10, max = 11, message = "Số điện thoại phải từ 10-11 ký tự")
    private String phoneNumber;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;
    
    private String imageUrl;
    
    private MultipartFile imageFile;
    
    // Constructors
    public SupplierDto() {}
    
    public SupplierDto(String suplierName, String address, String phoneNumber, String email) {
        this.suplierName = suplierName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
    
    // Getters and Setters
    public Integer getSuplierId() {
        return suplierId;
    }
    
    public void setSuplierId(Integer suplierId) {
        this.suplierId = suplierId;
    }
    
    public String getSuplierName() {
        return suplierName;
    }
    
    public void setSuplierName(String suplierName) {
        this.suplierName = suplierName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
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
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public MultipartFile getImageFile() {
        return imageFile;
    }
    
    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
} 