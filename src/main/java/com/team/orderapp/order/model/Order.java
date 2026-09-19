package com.team.orderapp.order.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Order {

    /*
     * 작업: orders 테이블과 매핑되는 주문 기본 정보(식별자, 주문번호, 고객 ID, 주문일시, 상태, 반품일시 등) 도메인 모델 구현
     *
     * 작업자: 김상진(Dorazee0209)
     */

    private Long orderId;
    private String orderNo;
    private Long customerId;
    private LocalDateTime orderedAt;
    private String status;
    private LocalDateTime returnedAt;
}
