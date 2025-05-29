package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Import")
public class Import {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer importId;

    @Column(name = "importCode", nullable = false, unique = true)
    private String importCode;

    @Column(name = "importDate", nullable = false)
    private LocalDate importDate;

    @ManyToOne
    @JoinColumn(name = "phoneId", nullable = false)
    private Phone phone;

    @ManyToOne
    @JoinColumn(name = "supplierId", nullable = false)
    private Supplier supplier;

    @Column(name = "importQuantity", nullable = false)
    private Integer importQuantity;

    @Column(name = "importUnitPrice", nullable = false)
    private Double importUnitPrice;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public Import() {
    }

    public Import(Integer importId, String importCode, LocalDate importDate, Phone phone, Supplier supplier, Integer importQuantity, Double importUnitPrice, Employee employee) {
        this.importId = importId;
        this.importCode = importCode;
        this.importDate = importDate;
        this.phone = phone;
        this.supplier = supplier;
        this.importQuantity = importQuantity;
        this.importUnitPrice = importUnitPrice;
        this.employee = employee;
    }

    public Integer getImportId() {
        return importId;
    }

    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    public String getImportCode() {
        return importCode;
    }

    public void setImportCode(String importCode) {
        this.importCode = importCode;
    }

    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDate importDate) {
        this.importDate = importDate;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Integer getImportQuantity() {
        return importQuantity;
    }

    public void setImportQuantity(Integer importQuantity) {
        this.importQuantity = importQuantity;
    }

    public Double getImportUnitPrice() {
        return importUnitPrice;
    }

    public void setImportUnitPrice(Double importUnitPrice) {
        this.importUnitPrice = importUnitPrice;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
