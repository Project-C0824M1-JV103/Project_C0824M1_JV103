package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "Supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suplier_id")
    private Integer suplierId;

    @Column(name = "suplier_name", nullable = false, length = 100)
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    @Size(min = 2, max = 100, message = "Tên nhà cung cấp phải từ 2-100 ký tự")
    private String suplierName;
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 10, max = 11, message = "Số điện thoại phải từ 10-11 ký tự")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<Product> products;

    // Constructors
    public Supplier() {}

    public Supplier(String suplierName, String address, String phoneNumber, String email, String imageUrl) {
        this.suplierName = suplierName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.imageUrl = imageUrl;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
} 