package com.team.orderapp.stock;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 관리자 재고 변경 이력 조회용 객체
 */
@Getter
@Setter
public class StockAdjustmentHistory {

    private Long adjustmentId;

    private Long productId;
    private String productCode;
    private String productName;

    private Integer quantityDelta;
    private String reason;

    private Long adjustedByUserId;
    private String adjustedByEmail;

    private LocalDateTime adjustedAt;
}