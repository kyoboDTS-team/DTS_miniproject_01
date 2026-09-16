package com.team.orderapp.order.query;

import java.time.LocalDateTime;

/**
 * 주문 목록 조회를 위한 읽기 전용 요약 View DTO 클래스입니다.
 */
public class OrderSummaryView {

    private Long orderId;
    private String customerName;
    private LocalDateTime orderDate;
    private String status;
    private int itemCount;
    private double totalAmount;

    public OrderSummaryView() {
    }

    public OrderSummaryView(Long InOrderId, String InCustomerName, LocalDateTime InOrderDate, String InStatus, int InItemCount, double InTotalAmount) {
        this.orderId = InOrderId;
        this.customerName = InCustomerName;
        this.orderDate = InOrderDate;
        this.status = InStatus;
        this.itemCount = InItemCount;
        this.totalAmount = InTotalAmount;
    }

    public Long GetOrderId() {
        return orderId;
    }

    public void SetOrderId(Long InOrderId) {
        this.orderId = InOrderId;
    }

    public String GetCustomerName() {
        return customerName;
    }

    public void SetCustomerName(String InCustomerName) {
        this.customerName = InCustomerName;
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

    public int GetItemCount() {
        return itemCount;
    }

    public void SetItemCount(int InItemCount) {
        this.itemCount = InItemCount;
    }

    public double GetTotalAmount() {
        return totalAmount;
    }

    public void SetTotalAmount(double InTotalAmount) {
        this.totalAmount = InTotalAmount;
    }

    @Override
    public String toString() {
        return "OrderSummaryView{" +
                "orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", itemCount=" + itemCount +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
