package com.team.orderapp.order.model;

/**
 * 주문 상세 항목(개별 주문 상품) 엔티티/도메인 모델 클래스입니다.
 */
public class OrderItem {

    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private double unitPrice;
    private int quantity;
    private double subtotal;

    public OrderItem() {
    }

    public OrderItem(Long InOrderItemId, Long InOrderId, Long InProductId, double InUnitPrice, int InQuantity) {
        this.orderItemId = InOrderItemId;
        this.orderId = InOrderId;
        this.productId = InProductId;
        this.unitPrice = InUnitPrice;
        this.quantity = InQuantity;
        this.subtotal = CalculateSubtotal(InUnitPrice, InQuantity);
    }

    public Long GetOrderItemId() {
        return orderItemId;
    }

    public void SetOrderItemId(Long InOrderItemId) {
        this.orderItemId = InOrderItemId;
    }

    public Long GetOrderId() {
        return orderId;
    }

    public void SetOrderId(Long InOrderId) {
        this.orderId = InOrderId;
    }

    public Long GetProductId() {
        return productId;
    }

    public void SetProductId(Long InProductId) {
        this.productId = InProductId;
    }

    public double GetUnitPrice() {
        return unitPrice;
    }

    public void SetUnitPrice(double InUnitPrice) {
        this.unitPrice = InUnitPrice;
        this.subtotal = CalculateSubtotal(InUnitPrice, this.quantity);
    }

    public int GetQuantity() {
        return quantity;
    }

    public void SetQuantity(int InQuantity) {
        this.quantity = InQuantity;
        this.subtotal = CalculateSubtotal(this.unitPrice, InQuantity);
    }

    public double GetSubtotal() {
        return subtotal;
    }

    public void SetSubtotal(double InSubtotal) {
        this.subtotal = InSubtotal;
    }

    /**
     * 단가와 수량을 곱하여 소계를 계산하는 헬퍼 메서드입니다.
     *
     * @param InUnitPrice 단가
     * @param InQuantity 수량
     * @return 계산된 소계 금액
     */
    public static double CalculateSubtotal(double InUnitPrice, int InQuantity) {
        return InUnitPrice * InQuantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                '}';
    }
}
