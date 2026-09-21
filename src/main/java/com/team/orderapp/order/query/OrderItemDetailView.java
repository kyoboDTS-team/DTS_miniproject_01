package com.team.orderapp.order.query;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 주문 상세 화면의 상품 한 줄을 표현하는 조회 전용 DTO입니다.
 * 작성자: 박형준
 * order_item과 product를 JOIN한 결과를 저장합니다.
 */
@Getter
@Setter
public class OrderItemDetailView {

    // order_item
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;

    // product JOIN 결과
    private String productCode;
    private String productName;

    // SQL에서 unitPrice × quantity로 계산
    private BigDecimal subtotal;
}