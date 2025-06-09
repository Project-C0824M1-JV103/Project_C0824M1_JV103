package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public class SaleDto {
    @NotBlank(message = "Nhập tên khách hàng")
    private String customerName;

    @NotBlank(message = "Nhập số điện thoại khách hàng")
    @Size(max = 15, message = "Nhập lại số điện thoại!")
    private String phoneNumber;

    @NotBlank(message = "Nhập địa chỉ khách hàng")
    private String address;

    @NotBlank(message = "Nhập ngày sinh")
    private String birthdayDate;
    
    @Email(message = "Email không hợp lệ")
    private String email;

    private Integer saleId;
    
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    private BigDecimal amount;

    private List<SaleDetailsDto> saleDetails;

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
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getBirthdayDate() {
        return birthdayDate;
    }
    
    public void setBirthdayDate(String birthdayDate) {
        this.birthdayDate = birthdayDate;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getSaleId() {
        return saleId;
    }
    
    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public List<SaleDetailsDto> getSaleDetails() {
        return saleDetails;
    }
    
    public void setSaleDetails(List<SaleDetailsDto> saleDetails) {
        this.saleDetails = saleDetails;
    }
}
