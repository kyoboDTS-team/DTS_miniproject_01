package com.team.orderapp.stock;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 상품 재고 조정 이력
 */
@Getter
@Setter
public class StockAdjustment {

    private Long adjustmentId;
    private Long productId;

    // +10 : 입고
    // -3  : 파손 / 수동 차감
    private Integer quantityDelta;

    private String reason;

    // 작업한 관리자 user_id
    private Long adjustedByUserId;

    private LocalDateTime adjustedAt;
}