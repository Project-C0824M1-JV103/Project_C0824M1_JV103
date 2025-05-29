package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SaleDetail")
public class SaleDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer saleDetailId;

    @ManyToOne
    @JoinColumn(name = "saleId", nullable = false)
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "phoneId", nullable = false)
    private Phone phone;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unitPrice", nullable = false)
    private Double unitPrice;

    public SaleDetail() {
    }

    public SaleDetail(Integer saleDetailId, Sale sale, Phone phone, Integer quantity, Double unitPrice) {
        this.saleDetailId = saleDetailId;
        this.sale = sale;
        this.phone = phone;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Integer getSaleDetailId() {
        return saleDetailId;
    }

    public void setSaleDetailId(Integer saleDetailId) {
        this.saleDetailId = saleDetailId;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
