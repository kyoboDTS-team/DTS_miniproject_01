package com.team.orderapp.order.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 엔티티/도메인 모델 클래스입니다.
 */
public class Order {

    private Long orderId;
    private Long customerId;
    private LocalDateTime orderDate;
    private String status;
    private double totalAmount;
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Order(Long InOrderId, Long InCustomerId, String InStatus) {
        this.orderId = InOrderId;
        this.customerId = InCustomerId;
        this.orderDate = LocalDateTime.now();
        this.status = InStatus;
        this.totalAmount = 0.0;
        this.items = new ArrayList<>();
    }

    public Order(Long InOrderId, Long InCustomerId, LocalDateTime InOrderDate, String InStatus, double InTotalAmount, List<OrderItem> InItems) {
        this.orderId = InOrderId;
        this.customerId = InCustomerId;
        this.orderDate = InOrderDate;
        this.status = InStatus;
        this.totalAmount = InTotalAmount;
        this.items = InItems != null ? InItems : new ArrayList<>();
    }

    public Long GetOrderId() {
        return orderId;
    }

    public void SetOrderId(Long InOrderId) {
        this.orderId = InOrderId;
    }

    public Long GetCustomerId() {
        return customerId;
    }

    public void SetCustomerId(Long InCustomerId) {
        this.customerId = InCustomerId;
    }

    public LocalDateTime GetOrderDate() {
        return orderDate;
    }

    public void SetOrderDate(LocalDateTime InOrderDate) {
        this.orderDate = InOrderDate;
    }

    public String GetStatus() {
        return status;
    }

    public void SetStatus(String InStatus) {
        this.status = InStatus;
    }

    public double GetTotalAmount() {
        return totalAmount;
    }

    public void SetTotalAmount(double InTotalAmount) {
        this.totalAmount = InTotalAmount;
    }

    public List<OrderItem> GetItems() {
        return items;
    }

    public void SetItems(List<OrderItem> InItems) {
        this.items = InItems;
        this.totalAmount = RecalculateTotalAmount();
    }

    /**
     * 주문 항목을 추가하고 총 금액을 갱신합니다.
     *
     * @param InItem 추가할 주문 항목
     */
    public void AddItem(OrderItem InItem) {
        if (InItem != null) {
            this.items.add(InItem);
            this.totalAmount += InItem.GetSubtotal();
        }
    }

    /**
     * 전체 주문 항목의 소계를 합산하여 총 주문 금액을 재계산하는 헬퍼 메서드입니다.
     *
     * @return 재계산된 총 주문 금액
     */
    public double RecalculateTotalAmount() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.GetSubtotal();
        }
        return sum;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + items.size() +
                '}';
    }
}
