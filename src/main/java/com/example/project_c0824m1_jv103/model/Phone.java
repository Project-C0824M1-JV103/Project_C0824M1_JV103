package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;


import java.util.List;

@Entity
@Table(name = "Phone")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer phoneId;

    @Column(name = "phoneName", nullable = false)
    private String phoneName;

    @Column(name = "salePrice", nullable = false)
    private Double salePrice;

    @Column(name = "importPrice")
    private Double importPrice;

    @Column(name = "size")
    private String size;

    @Column(name = "camera")
    private String camera;

    @Column(name = "selfieCamera")
    private String selfieCamera;

    @Column(name = "cpu")
    private String cpu;

    @Column(name = "storage")
    private String storage;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "stockQuantity")
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    private List<SaleDetail> saleDetails;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    private List<Import> imports;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    private List<Export> exports;

    public Phone() {
    }

    public Phone(Integer phoneId, String phoneName, Double salePrice, Double importPrice, String size, String camera, String selfieCamera, String cpu, String storage, String description, String image, Integer stockQuantity, Category category, List<SaleDetail> saleDetails, List<Import> imports, List<Export> exports) {
        this.phoneId = phoneId;
        this.phoneName = phoneName;
        this.salePrice = salePrice;
        this.importPrice = importPrice;
        this.size = size;
        this.camera = camera;
        this.selfieCamera = selfieCamera;
        this.cpu = cpu;
        this.storage = storage;
        this.description = description;
        this.image = image;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.saleDetails = saleDetails;
        this.imports = imports;
        this.exports = exports;
    }

    public Integer getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Integer phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(Double importPrice) {
        this.importPrice = importPrice;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getSelfieCamera() {
        return selfieCamera;
    }

    public void setSelfieCamera(String selfieCamera) {
        this.selfieCamera = selfieCamera;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<SaleDetail> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetail> saleDetails) {
        this.saleDetails = saleDetails;
    }

    public List<Import> getImports() {
        return imports;
    }

    public void setImports(List<Import> imports) {
        this.imports = imports;
    }

    public List<Export> getExports() {
        return exports;
    }

    public void setExports(List<Export> exports) {
        this.exports = exports;
    }
}
