package com.example.project_c0824m1_jv103.dto;

public class StorageImportId {
    private Integer productId;
    private Integer employeeId;

    public StorageImportId() {}

    public StorageImportId(Integer productId, Integer employeeId) {
        this.productId = productId;
        this.employeeId = employeeId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
} 