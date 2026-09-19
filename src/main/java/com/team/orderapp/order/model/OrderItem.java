package com.team.orderapp.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItem {

    /*
     * 작업: order_item 테이블과 매핑되는 주문 상세 품목(식별자, 주문 ID, 상품 ID, 수량, 결제 단가 등) 도메인 모델 구현
     *
     * 작업자: 김상진(Dorazee0209)
     */

    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private Integer quantity;

    // 주문 당시 단가. 이후 상품 가격이 바뀌어도 주문 금액은 유지된다.
    private BigDecimal unitPrice;
}
