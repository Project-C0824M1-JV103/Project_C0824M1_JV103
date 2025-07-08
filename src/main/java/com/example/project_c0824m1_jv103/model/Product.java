package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "Product")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
    
    @Column(name = "size", length = 50)
    private String size;
    
    @Column(name = "price", nullable = false)
    private Double price;
    
    @Column(name = "camera_front", length = 50)
    private String cameraFront;
    
    @Column(name = "camera_back", length = 50)
    private String cameraBack;
    
    @Column(name = "memory", length = 50)
    private String memory;
    
    @Column(name = "cpu", length = 50)
    private String cpu;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "suplier_id")
    @JsonIgnore
    private Supplier supplier;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProductImages> productImages;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SaleDetails> saleDetails;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Storage> storages;
    
    // Constructors
    public Product() {}
    
    public Product(String productName, String size, Double price, String cameraFront, String cameraBack, 
                  String memory, String cpu, String description, Integer quantity, Category category, Supplier supplier) {
        this.productName = productName;
        this.size = size;
        this.price = price;
        this.cameraFront = cameraFront;
        this.cameraBack = cameraBack;
        this.memory = memory;
        this.cpu = cpu;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
        this.supplier = supplier;
    }
    
    // Getters and Setters
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
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getCameraFront() {
        return cameraFront;
    }
    
    public void setCameraFront(String cameraFront) {
        this.cameraFront = cameraFront;
    }
    
    public String getCameraBack() {
        return cameraBack;
    }
    
    public void setCameraBack(String cameraBack) {
        this.cameraBack = cameraBack;
    }
    
    public String getMemory() {
        return memory;
    }
    
    public void setMemory(String memory) {
        this.memory = memory;
    }
    
    public String getCpu() {
        return cpu;
    }
    
    public void setCpu(String cpu) {
        this.cpu = cpu;
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
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public List<ProductImages> getProductImages() {
        return productImages;
    }
    
    public void setProductImages(List<ProductImages> productImages) {
        this.productImages = productImages;
    }
    
    public List<SaleDetails> getSaleDetails() {
        return saleDetails;
    }
    
    public void setSaleDetails(List<SaleDetails> saleDetails) {
        this.saleDetails = saleDetails;
    }
    
    public List<Storage> getStorages() {
        return storages;
    }
    
    public void setStorages(List<Storage> storages) {
        this.storages = storages;
    }
} 