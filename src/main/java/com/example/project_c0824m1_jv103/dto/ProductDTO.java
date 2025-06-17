package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ProductDTO {
    
    private Integer productId; // Cho update
    
    @NotBlank(message = "Tên hàng không được để trống")
    @Size(max = 100, message = "Tên hàng không được vượt quá 100 ký tự")
    private String productName;
    
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @DecimalMax(value = "999999999.99", message = "Giá quá lớn")
    private Double price;
    
    @Size(max = 50, message = "Kích thước màn hình không được vượt quá 50 ký tự")
    private String size;
    
    @Size(max = 50, message = "Thông tin camera không được vượt quá 50 ký tự")
    private String cameraBack;
    
    @Size(max = 50, message = "Thông tin selfie không được vượt quá 50 ký tự")
    private String cameraFront;
    
    @Size(max = 50, message = "Thông tin CPU không được vượt quá 50 ký tự")
    private String cpu;
    
    @Size(max = 50, message = "Thông tin lưu trữ không được vượt quá 50 ký tự")
    private String memory;
    
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description; // Mô tả thêm
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải >= 0")
    private Integer quantity;
    
    // Cho upload ảnh
    @NotNull(message = "Ảnh không được để trống")
    private List<MultipartFile> imageFiles;
    private List<String> existingImageUrls;
    

    private Integer categoryId;
    private Integer supplierId;

    private String imageUrl;
    
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Constructors
    public ProductDTO() {}
    
    public ProductDTO(String productName, Double price, String size, String cameraBack, 
                     String cameraFront, String cpu, String memory, String description, Integer quantity) {
        this.productName = productName;
        this.price = price;
        this.size = size;
        this.cameraBack = cameraBack;
        this.cameraFront = cameraFront;
        this.cpu = cpu;
        this.memory = memory;
        this.description = description;
        this.quantity = quantity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<MultipartFile> getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(List<MultipartFile> imageFiles) {
        this.imageFiles = imageFiles;
    }

    public List<String> getExistingImageUrls() {
        return existingImageUrls;
    }

    public void setExistingImageUrls(List<String> existingImageUrls) {
        this.existingImageUrls = existingImageUrls;
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


    public String getFormattedPrice() {
        if (price == null) return "0 VNĐ";
        return String.format("%,.0f VNĐ", price);
    }
} 