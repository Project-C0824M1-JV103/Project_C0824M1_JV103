package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public class SaleDto {
    private Integer saleId;

    private String employeeName;

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

    private String productName;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

//    @NotNull(message = "Đơn giá không được để trống")
//    @Min(value = 0, message = "Đơn giá không được âm")
    private Double uniquePrice;
    
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    private boolean printPDF;

    private BigDecimal amount;

//    private List<SaleDetailsDto> saleDetails;


    public boolean isPrintPDF() {
        return printPDF;
    }

    public void setPrintPDF(boolean printPDF) {
        this.printPDF = printPDF;
    }

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
    
//    public List<SaleDetailsDto> getSaleDetails() {
//        return saleDetails;
//    }
//
//    public void setSaleDetails(List<SaleDetailsDto> saleDetails) {
//        this.saleDetails = saleDetails;
//    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public @NotNull(message = "Số lượng không được để trống") @Min(value = 1, message = "Số lượng phải lớn hơn 0") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "Số lượng không được để trống") @Min(value = 1, message = "Số lượng phải lớn hơn 0") Integer quantity) {
        this.quantity = quantity;
    }

    public @NotNull(message = "Đơn giá không được để trống") @Min(value = 0, message = "Đơn giá không được âm") Double getUniquePrice() {
        return uniquePrice;
    }

    public void setUniquePrice(@NotNull(message = "Đơn giá không được để trống") @Min(value = 0, message = "Đơn giá không được âm") Double uniquePrice) {
        this.uniquePrice = uniquePrice;
    }
}
