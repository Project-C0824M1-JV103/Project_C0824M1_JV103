package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Export")
public class Export {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer exportId;

    @Column(name = "exportCode", nullable = false, unique = true)
    private String exportCode;

    @Column(name = "exportDate", nullable = false)
    private LocalDate exportDate;

    @ManyToOne
    @JoinColumn(name = "phoneId", nullable = false)
    private Phone phone;

    @ManyToOne
    @JoinColumn(name = "supplierId", nullable = false)
    private Supplier supplier;

    @Column(name = "exportQuantity", nullable = false)
    private Integer exportQuantity;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public Export() {
    }

    public Export(Integer exportId, String exportCode, LocalDate exportDate, Phone phone, Supplier supplier, Integer exportQuantity, Employee employee) {
        this.exportId = exportId;
        this.exportCode = exportCode;
        this.exportDate = exportDate;
        this.phone = phone;
        this.supplier = supplier;
        this.exportQuantity = exportQuantity;
        this.employee = employee;
    }

    public Integer getExportId() {
        return exportId;
    }

    public void setExportId(Integer exportId) {
        this.exportId = exportId;
    }

    public String getExportCode() {
        return exportCode;
    }

    public void setExportCode(String exportCode) {
        this.exportCode = exportCode;
    }

    public LocalDate getExportDate() {
        return exportDate;
    }

    public void setExportDate(LocalDate exportDate) {
        this.exportDate = exportDate;
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

    public Integer getExportQuantity() {
        return exportQuantity;
    }

    public void setExportQuantity(Integer exportQuantity) {
        this.exportQuantity = exportQuantity;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
