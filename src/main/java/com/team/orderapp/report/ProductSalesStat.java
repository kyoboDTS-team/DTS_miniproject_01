package com.team.orderapp.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 상품별 판매 통계 결과 객체
 */
@Getter
@Setter
public class ProductSalesStat {

    private Long productId;
    private String productCode;
    private String productName;

    // 판매 수량
    private Long totalQuantity;

    // 상품별 매출
    private BigDecimal totalSales;
}