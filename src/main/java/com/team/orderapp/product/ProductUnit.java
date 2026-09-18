package com.team.orderapp.product;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 시리얼 관리 상품의 실제 개별 상품
 * 시리얼 넘버 필요한 비싼 상품들
 */
@Getter
@Setter
public class ProductUnit {

    private Long productUnitId;
    private Long productId;
    private String serialNumber;

    // AVAILABLE / SOLD
    private String unitStatus;

    private LocalDateTime createdAt;
}