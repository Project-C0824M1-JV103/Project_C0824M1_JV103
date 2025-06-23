package com.example.project_c0824m1_jv103.dto;

import java.util.List;

public class StorageImportModal {
    private Integer productId;
    private String productName;
    private Double cost;
    private String cpu;
    private String memory;
    private List<String> productImages;
    private Integer supplierId;
    private String supplierName;

    public StorageImportModal() {}

    public StorageImportModal(Integer productId, String productName, Double cost, String cpu, String memory, List<String> productImages, Integer supplierId, String supplierName) {
        this.productId = productId;
        this.productName = productName;
        this.cost = cost;
        this.cpu = cpu;
        this.memory = memory;
        this.productImages = productImages;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
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
    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
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
    public List<String> getProductImages() {
        return productImages;
    }
    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }
    public Integer getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
    public String getSupplierName() {
        return supplierName;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
} 