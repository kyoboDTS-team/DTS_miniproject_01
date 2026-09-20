package com.team.orderapp.order.model;

/**
 * 주문 상태
 *
 * CONFIRMED : 주문이 정상적으로 확정된 상태
 * RETURNED  : 반품이 완료된 상태
 * 작업자: 박형준
 * MyBatis에서 orders.order_status 값을 OrderStatus 타입으로 받을 때
 * DB에 CONFIRMED가 저장되어 있으면 자동으로 다음 값에 매핑.
 * DB의 orders.order_status 값과 이름이 반드시 같아야 함.
 */
public enum OrderStatus {

    CONFIRMED("주문 확정"),
    RETURNED("반품 완료");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 콘솔 화면에 출력할 한글 상태명을 반환합니다.
     */
    public String GetDisplayName() {
        return displayName;
    }
}