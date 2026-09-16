package com.team.orderapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * product 테이블과 매핑되는 상품 DTO 클래스입니다.
 */
public class Product {
    private Long productId;
    private String productCode;
    private Long categoryId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;
    private int reorderLevel;
    private String saleStatus;
    private LocalDateTime createdAt;

    public Product() {
    }

    public Product(String InProductCode, Long InCategoryId, String InProductName, BigDecimal InPrice, int InStockQuantity) {
        this.productCode = InProductCode;
        this.categoryId = InCategoryId;
        this.productName = InProductName;
        this.price = InPrice;
        this.stockQuantity = InStockQuantity;
        this.reorderLevel = 10;
        this.saleStatus = "SELLING";
    }

    public Long GetProductId() {
        return productId;
    }

    public void SetProductId(Long InProductId) {
        this.productId = InProductId;
    }

    // 편의용 별칭 메서드
    public Long GetId() {
        return productId;
    }

    public String GetProductCode() {
        return productCode;
    }

    public void SetProductCode(String InProductCode) {
        this.productCode = InProductCode;
    }

    public Long GetCategoryId() {
        return categoryId;
    }

    public void SetCategoryId(Long InCategoryId) {
        this.categoryId = InCategoryId;
    }

    public String GetProductName() {
        return productName;
    }

    public void SetProductName(String InProductName) {
        this.productName = InProductName;
    }

    // 편의용 별칭 메서드
    public String GetName() {
        return productName;
    }

    public BigDecimal GetPrice() {
        return price;
    }

    public void SetPrice(BigDecimal InPrice) {
        this.price = InPrice;
    }

    public int GetStockQuantity() {
        return stockQuantity;
    }

    public void SetStockQuantity(int InStockQuantity) {
        this.stockQuantity = InStockQuantity;
    }

    public int GetReorderLevel() {
        return reorderLevel;
    }

    public void SetReorderLevel(int InReorderLevel) {
        this.reorderLevel = InReorderLevel;
    }

    public String GetSaleStatus() {
        return saleStatus;
    }

    public void SetSaleStatus(String InSaleStatus) {
        this.saleStatus = InSaleStatus;
    }

    public LocalDateTime GetCreatedAt() {
        return createdAt;
    }

    public void SetCreatedAt(LocalDateTime InCreatedAt) {
        this.createdAt = InCreatedAt;
    }
}
