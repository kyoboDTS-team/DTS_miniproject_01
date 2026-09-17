package com.team.orderapp.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {

    private Long productId;
    private String productCode;
    private Long categoryId;
    private String productName;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private String saleStatus;
    private Boolean requiresSerial;
    private LocalDateTime createdAt;

    // MyBatis가 객체를 만들 때 사용할 기본 생성자
    public Product() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public String getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public Boolean getRequiresSerial() {
        return requiresSerial;
    }

    public void setRequiresSerial(Boolean requiresSerial) {
        this.requiresSerial = requiresSerial;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productCode='" + productCode + '\'' +
                ", categoryId=" + categoryId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", reorderLevel=" + reorderLevel +
                ", saleStatus='" + saleStatus + '\'' +
                ", requiresSerial=" + requiresSerial +
                ", createdAt=" + createdAt +
                '}';
    }
}