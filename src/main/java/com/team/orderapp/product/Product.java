package com.team.orderapp.product;

import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
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
    @Override
    public String toString() {
        return "상품정보{" +
                "상품번호=" + productId +
                ", 상품코드='" + productCode + '\'' +
                ", 카테고리=" + categoryId +
                ", 상품명='" + productName + '\'' +
                ", 가격=" + price +
                ", 재고=" + stockQuantity +
                ", 재고 수위=" + reorderLevel +
                ", 판매 상태='" + saleStatus + '\'' +
                ", 시리얼 번호 유무=" + requiresSerial +
                ", 제조사=" + createdAt +
                '}';
    }

}