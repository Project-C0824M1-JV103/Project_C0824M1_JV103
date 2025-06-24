package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class StorageImportProduct {
    private Integer productId;

    @NotBlank(message = " không được để trống")
    @Size(max = 100, message = "Tối đa 100 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_\\.]+$", message = " không được chứa ký tự đặc biệt")
    private String productName;

    private Double price;

    @NotBlank(message = " không được để trống")
    @Size(max = 50, message = "Tối đa 50 ký tự")
    @Pattern(regexp = "^[0-9]+(\\.?[0-9]+)?$", message = " chỉ được nhập số")
    private String size;

    @NotBlank(message = " không được để trống")
    @Size(max = 50, message = "Tối đa 50 ký tự")
    @Pattern(regexp = "^[0-9]+$", message = " chỉ được nhập số")
    private String cameraBack;

    @NotBlank(message = " không được để trống")
    @Size(max = 50, message = "Tối đa 50 ký tự")
    @Pattern(regexp = "^[0-9]+$", message = " chỉ được nhập số")
    private String cameraFront;

    @NotBlank(message = " không được để trống")
    @Size(max = 50, message = "Tối đa 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_\\.]+$", message = " không được chứa ký tự đặc biệt")
    private String cpu;

    @NotBlank(message = " không được để trống")
    @Size(max = 50, message = "Tối đa 50 ký tự")
    @Pattern(regexp = "^[0-9]+$", message = " chỉ được nhập số")
    private String memory;

    private Integer quantity;

    @NotNull(message = "Không được để trống")
    private Integer categoryId;
    @NotNull(message = "Không được để trống")
    private Integer supplierId;

    public StorageImportProduct(Integer productId, String productName, Double price, String size, String cameraBack, String cameraFront, String cpu, String memory, Integer quantity, Integer categoryId, Integer supplierId) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.size = size;
        this.cameraBack = cameraBack;
        this.cameraFront = cameraFront;
        this.cpu = cpu;
        this.memory = memory;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
    }

    public StorageImportProduct() {
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCameraBack() {
        return cameraBack;
    }

    public void setCameraBack(String cameraBack) {
        this.cameraBack = cameraBack;
    }

    public String getCameraFront() {
        return cameraFront;
    }

    public void setCameraFront(String cameraFront) {
        this.cameraFront = cameraFront;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

