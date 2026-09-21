package com.team.orderapp.order.query;

import com.team.orderapp.order.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 목록 한 줄을 표현하는 조회 전용 DTO입니다.
 * 작성자 : 박형준
 * orders, customer, order_item 조회 결과를 함께 저장합니다.
 */
@Getter
@Setter
public class OrderSummaryView {

    // orders
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private LocalDateTime orderedAt;
    private OrderStatus status;

    // customer
    // 비회원 주문이면 null일 수 있음
    private String customerName;

    // order_item 집계 결과
    private Long itemCount;
    private Long totalQuantity;
    private BigDecimal totalAmount;


    /**
     * 회원 주문인지 확인합니다.
     */
    public boolean IsMemberOrder() {
        return customerId != null;
    }


    /**
     * 화면에 출력할 주문자 구분을 반환합니다.
     */
    public String GetCustomerTypeName() {

        if (IsMemberOrder()) {
            return "회원";
        }

        return "비회원";
    }
}