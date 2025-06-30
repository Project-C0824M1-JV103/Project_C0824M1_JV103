package com.example.project_c0824m1_jv103.dto;

import jakarta.validation.constraints.*;

public class StorageImportProductDTO {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 100, message = "Tên sản phẩm tối đa 100 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.]+$", message = "Tên sản phẩm không được chứa ký tự đặc biệt")
    private String productName;

    @NotBlank(message = "Kích thước màn hình không được để trống")
    @Size(max = 50, message = "Kích thước màn hình tối đa 50 ký tự")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$", message = "Kích thước màn hình chỉ được nhập số")
    private String size;

    @NotBlank(message = "Camera sau không được để trống")
    @Size(max = 50, message = "Camera sau tối đa 50 ký tự")
    @Pattern(regexp = "^[0-9]+$", message = "Camera sau chỉ được nhập số")
    private String cameraBack;

    @NotBlank(message = "Camera trước không được để trống")
    @Size(max = 50, message = "Camera trước tối đa 50 ký tự")
    @Pattern(regexp = "^[0-9]+$", message = "Camera trước chỉ được nhập số")
    private String cameraFront;

    @NotBlank(message = "CPU không được để trống")
    @Size(max = 50, message = "CPU tối đa 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.]+$", message = "CPU không được chứa ký tự đặc biệt")
    private String cpu;

    @NotBlank(message = "Bộ nhớ không được để trống")
    @Size(max = 50, message = "Bộ nhớ tối đa 50 ký tự")
    @Pattern(regexp = "^[0-9]+$", message = "Bộ nhớ chỉ được nhập số")
    private String memory;

    @NotNull(message = "Danh mục không được để trống")
    private Integer categoryId;

    @NotNull(message = "Nhà cung cấp không được để trống")
    private Integer supplierId;

    // Getters and Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getCameraBack() { return cameraBack; }
    public void setCameraBack(String cameraBack) { this.cameraBack = cameraBack; }
    public String getCameraFront() { return cameraFront; }
    public void setCameraFront(String cameraFront) { this.cameraFront = cameraFront; }
    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
}
