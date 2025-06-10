package com.example.project_c0824m1_jv103.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SaleDetails")
public class SaleDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saledetails_id")
    private Integer saledetailsId;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "unique_price", nullable = false)
    private Double uniquePrice;
    public SaleDetails() {}
    
    public SaleDetails(Product product, Sale sale, Integer quantity, Double uniquePrice) {
        this.product = product;
        this.sale = sale;
        this.quantity = quantity;
        this.uniquePrice = uniquePrice;
    }

    public Integer getSaledetailsId() {
        return saledetailsId;
    }
    
    public void setSaledetailsId(Integer saledetailsId) {
        this.saledetailsId = saledetailsId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Sale getSale() {
        return sale;
    }
    
    public void setSale(Sale sale) {
        this.sale = sale;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getUniquePrice() {
        return uniquePrice;
    }
    
    public void setUniquePrice(Double uniquePrice) {
        this.uniquePrice = uniquePrice;
    }
} 