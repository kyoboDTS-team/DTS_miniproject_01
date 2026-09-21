package com.team.orderapp.order.query;

import com.team.orderapp.order.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 상세 화면 전체를 표현하는 조회 전용 DTO입니다.
 *
 * 작성자: 박형준
 * DAO에서는 주문 헤더와 품목을 따로 조회하고,
 * Service에서 하나의 OrderDetailView로 결합합니다.
 */
@Getter
@Setter
public class OrderDetailView {

    // orders
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private LocalDateTime orderedAt;
    private OrderStatus status;
    private LocalDateTime returnedAt;

    // customer JOIN 결과
    // 비회원 주문이면 null일 수 있음
    private String customerName;

    // order_item 집계 결과
    private Long itemCount;
    private Long totalQuantity;
    private BigDecimal totalAmount;

    // Service에서 설정할 주문 상품 목록
    private List<OrderItemDetailView> items =
            new ArrayList<>();


    /**
     * 회원 주문인지 확인합니다.
     */
    public boolean IsMemberOrder() {
        return customerId != null;
    }


    /**
     * 화면 출력용 주문자 구분
     */
    public String GetCustomerTypeName() {

        if (IsMemberOrder()) {
            return "회원";
        }

        return "비회원";
    }
}