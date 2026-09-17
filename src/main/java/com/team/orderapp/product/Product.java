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

}