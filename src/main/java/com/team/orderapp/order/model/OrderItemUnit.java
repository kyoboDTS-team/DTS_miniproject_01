package com.team.orderapp.order.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderItemUnit {

    /*
     * 작업: order_item_unit 테이블과 매핑되는 주문 품목-시리얼 상품 배정 이력(식별자, 주문품목 ID, 시리얼상품 ID, 배정일시, 반품일시 등) 도메인 모델 구현
     *
     * 작업자: 김상진(Dorazee0209)
     */

    private Long orderItemUnitId;
    private Long orderItemId;
    private Long productUnitId;
    private LocalDateTime assignedAt;

    // 반품 전에는 null
    private LocalDateTime returnedAt;
}
