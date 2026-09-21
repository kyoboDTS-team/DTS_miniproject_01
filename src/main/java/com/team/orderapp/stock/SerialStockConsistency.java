package com.team.orderapp.stock;

import lombok.Getter;
import lombok.Setter;

/**
 * 시리얼 상품의 재고 정합성 조회 결과
 */
@Getter
@Setter
public class SerialStockConsistency {

    private Long productId;
    private String productCode;
    private String productName;

    // product 테이블에 저장된 재고 수량
    private Integer stockQuantity;

    // AVAILABLE 상태인 실제 시리얼 개수
    private Long availableUnitCount;


    // ============================================================
    // 상품 재고와 AVAILABLE 시리얼 개수가 일치하는지 확인
    // ============================================================
    public boolean IsConsistent() {
        if (stockQuantity == null || availableUnitCount == null) {
            return false;
        }

        return stockQuantity.longValue() == availableUnitCount;
    }


    // ============================================================
    // 상품 재고와 AVAILABLE 시리얼 개수의 차이 계산
    // ============================================================
    public long GetDifference() {
        long stock = stockQuantity == null ? 0 : stockQuantity;
        long available = availableUnitCount == null ? 0 : availableUnitCount;

        return stock - available;
    }
}