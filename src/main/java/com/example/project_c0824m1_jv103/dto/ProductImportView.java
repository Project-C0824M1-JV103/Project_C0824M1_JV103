//package com.example.project_c0824m1_jv103.dto;
//
//import jakarta.validation.constraints.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//public class ProductImportView {
//    private Integer productId; // Cho update
//
//    @NotBlank(message = "Tên hàng không được để trống")
//    @Size(max = 100, message = "Tên hàng không được vượt quá 100 ký tự")
//    private String productName;
//
//    private Double price = 0.0;
//
//    @Size(max = 50, message = "Kích thước màn hình không được vượt quá 50 ký tự")
//    private String size;
//
//    @Size(max = 50, message = "Thông tin camera không được vượt quá 50 ký tự")
//    private String cameraBack;
//
//    @Size(max = 50, message = "Thông tin selfie không được vượt quá 50 ký tự")
//    private String cameraFront;
//
//    @Size(max = 50, message = "Thông tin CPU không được vượt quá 50 ký tự")
//    private String cpu;
//
//    @Size(max = 50, message = "Thông tin lưu trữ không được vượt quá 50 ký tự")
//    private String memory;
//
//    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
//    private String description; // Mô tả thêm
//
//    private Integer quantity = 0;
//
//
//    // Cho upload ảnh
//    @NotNull(message = "Ảnh không được để trống")
//    private List<MultipartFile> imageFiles;
//    private List<String> existingImageUrls;
//
//
//    private Integer categoryId;
//    private Integer supplierId;
//
//    private String imageUrl;
//
//    private String categoryName;
//    private String supplierName;
//
//    public ProductImportView() {
//        this.price = 0.0;
//        this.quantity = 0;
//    }
//
//
//    public Integer getProductId() {
//        return productId;
//    }
//
//    public void setProductId(Integer productId) {
//        this.productId = productId;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public Double getPrice() {
//        return price;
//    }
//
//    public void setPrice(Double price) {
//        this.price = price;
//    }
//
//    public String getSize() {
//        return size;
//    }
//
//    public void setSize(String size) {
//        this.size = size;
//    }
//
//    public String getCameraBack() {
//        return cameraBack;
//    }
//
//    public void setCameraBack(String cameraBack) {
//        this.cameraBack = cameraBack;
//    }
//
//    public String getCameraFront() {
//        return cameraFront;
//    }
//
//    public void setCameraFront(String cameraFront) {
//        this.cameraFront = cameraFront;
//    }
//
//    public String getCpu() {
//        return cpu;
//    }
//
//    public void setCpu(String cpu) {
//        this.cpu = cpu;
//    }
//
//    public String getMemory() {
//        return memory;
//    }
//
//    public void setMemory(String memory) {
//        this.memory = memory;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }
//
//    public List<MultipartFile> getImageFiles() {
//        return imageFiles;
//    }
//
//    public void setImageFiles(List<MultipartFile> imageFiles) {
//        this.imageFiles = imageFiles;
//    }
//
//    public List<String> getExistingImageUrls() {
//        return existingImageUrls;
//    }
//
//    public void setExistingImageUrls(List<String> existingImageUrls) {
//        this.existingImageUrls = existingImageUrls;
//    }
//
//    public Integer getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(Integer categoryId) {
//        this.categoryId = categoryId;
//    }
//
//    public Integer getSupplierId() {
//        return supplierId;
//    }
//
//    public void setSupplierId(Integer supplierId) {
//        this.supplierId = supplierId;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public String getCategoryName() {
//        return categoryName;
//    }
//
//    public void setCategoryName(String categoryName) {
//        this.categoryName = categoryName;
//    }
//
//    public String getSupplierName() {
//        return supplierName;
//    }
//
//    public void setSupplierName(String supplierName) {
//        this.supplierName = supplierName;
//    }
//}
//
