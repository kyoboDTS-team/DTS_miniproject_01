package com.team.orderapp.order.query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 특정 주문의 상세 정보를 조회하기 위한 View DTO 클래스입니다.
 */
public class OrderDetailView {

    private Long orderId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private LocalDateTime orderDate;
    private String status;
    private double totalAmount;
    private List<OrderItemDetail> items;

    public OrderDetailView() {
        this.items = new ArrayList<>();
    }

    public OrderDetailView(Long InOrderId, String InCustomerName, String InCustomerPhone, String InCustomerAddress, LocalDateTime InOrderDate, String InStatus, double InTotalAmount) {
        this.orderId = InOrderId;
        this.customerName = InCustomerName;
        this.customerPhone = InCustomerPhone;
        this.customerAddress = InCustomerAddress;
        this.orderDate = InOrderDate;
        this.status = InStatus;
        this.totalAmount = InTotalAmount;
        this.items = new ArrayList<>();
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

    public String GetCustomerPhone() {
        return customerPhone;
    }

    public void SetCustomerPhone(String InCustomerPhone) {
        this.customerPhone = InCustomerPhone;
    }

    public String GetCustomerAddress() {
        return customerAddress;
    }

    public void SetCustomerAddress(String InCustomerAddress) {
        this.customerAddress = InCustomerAddress;
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

    public List<OrderItemDetail> GetItems() {
        return items;
    }

    public void SetItems(List<OrderItemDetail> InItems) {
        this.items = InItems;
    }

    /**
     * 주문 상품 상세 항목을 목록에 추가합니다.
     *
     * @param InDetail 추가할 상품 상세 항목
     */
    public void AddItemDetail(OrderItemDetail InDetail) {
        if (InDetail != null) {
            this.items.add(InDetail);
        }
    }

    /**
     * 주문 상세 내 상품 정보를 담는 중첩 정적 DTO 클래스입니다.
     */
    public static class OrderItemDetail {
        private Long productId;
        private String productName;
        private double unitPrice;
        private int quantity;
        private double subtotal;

        public OrderItemDetail() {
        }

        public OrderItemDetail(Long InProductId, String InProductName, double InUnitPrice, int InQuantity, double InSubtotal) {
            this.productId = InProductId;
            this.productName = InProductName;
            this.unitPrice = InUnitPrice;
            this.quantity = InQuantity;
            this.subtotal = InSubtotal;
        }

        public Long GetProductId() {
            return productId;
        }

        public void SetProductId(Long InProductId) {
            this.productId = InProductId;
        }

        public String GetProductName() {
            return productName;
        }

        public void SetProductName(String InProductName) {
            this.productName = InProductName;
        }

        public double GetUnitPrice() {
            return unitPrice;
        }

        public void SetUnitPrice(double InUnitPrice) {
            this.unitPrice = InUnitPrice;
        }

        public int GetQuantity() {
            return quantity;
        }

        public void SetQuantity(int InQuantity) {
            this.quantity = InQuantity;
        }

        public double GetSubtotal() {
            return subtotal;
        }

        public void SetSubtotal(double InSubtotal) {
            this.subtotal = InSubtotal;
        }
    }
}
