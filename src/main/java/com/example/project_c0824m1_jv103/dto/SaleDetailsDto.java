//package com.example.project_c0824m1_jv103.dto;
//
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotNull;
//
//public class SaleDetailsDto {
//    private Integer saledetailsId;
//
//    @NotNull(message = "Sản phẩm không được để trống")
//    private Integer productId;
//
//    private String productName;
//
//    @NotNull(message = "Số lượng không được để trống")
//    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
//    private Integer quantity;
//
//    @NotNull(message = "Đơn giá không được để trống")
//    @Min(value = 0, message = "Đơn giá không được âm")
//    private Double uniquePrice;
//
//    public SaleDetailsDto() {}
//
//    public SaleDetailsDto(Integer saledetailsId, Integer productId, String productName, Integer quantity, Double uniquePrice) {
//        this.saledetailsId = saledetailsId;
//        this.productId = productId;
//        this.productName = productName;
//        this.quantity = quantity;
//        this.uniquePrice = uniquePrice;
//    }
//
//    public Integer getSaledetailsId() {
//        return saledetailsId;
//    }
//
//    public void setSaledetailsId(Integer saledetailsId) {
//        this.saledetailsId = saledetailsId;
//    }
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
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }
//
//    public Double getUniquePrice() {
//        return uniquePrice;
//    }
//
//    public void setUniquePrice(Double uniquePrice) {
//        this.uniquePrice = uniquePrice;
//    }
//}