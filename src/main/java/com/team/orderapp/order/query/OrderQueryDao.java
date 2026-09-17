package com.team.orderapp.order.query;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 주문 조회(Read/Query)를 전담하는 데이터베이스 접근 객체(DAO) 클래스입니다.
 */
public class OrderQueryDao {

    /**
     * 전체 주문 요약 목록을 조회합니다.
     *
     * @return 주문 요약 View 목록
     */
    @Select("SELECT o.order_id, o.order_no, c.customer_name, o.status, COUNT(oi.order_item_id) AS item_count, SUM(oi.quantity * oi.unit_price) AS total_amount " +
            "FROM orders o LEFT JOIN customer c ON o.customer_id = c.customer_id " +
            "LEFT JOIN order_item oi ON o.order_id = oi.order_id " +
            "GROUP BY o.order_id, o.order_no, c.customer_name, o.status ORDER BY o.ordered_at DESC")
    public List<OrderSummaryView> FindSummaries() {
        // TODO: orders 테이블과 customers 테이블을 조인하여 요약 목록 조회 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * 주문 식별자(ID)로 단건 상세 정보를 조회합니다.
     *
     * @param orderId 주문 식별자
     * @return 주문 상세 View Optional 객체
     */
    public Optional<OrderDetailView> FindDetailById(Long orderId) {
        // TODO: orders, customers, order_items, products를 조인하여 상세 조회 쿼리 구현
        return Optional.empty();
    }

    /**
     * 주문 번호로 단건 상세 정보를 조회합니다.
     *
     * @param orderNo 주문 번호
     * @return 주문 상세 View Optional 객체
     */
    public Optional<OrderDetailView> FindDetailByOrderNo(String orderNo) {
        return Optional.empty();
    }

    /**
     * 특정 고객의 주문 요약 목록을 조회합니다.
     *
     * @param customerId 고객 식별자
     * @return 해당 고객의 주문 요약 View 목록
     */
    @Select("SELECT o.order_id, o.order_no, c.customer_name, o.status, COUNT(oi.order_item_id) AS item_count, SUM(oi.quantity * oi.unit_price) AS total_amount " +
            "FROM orders o LEFT JOIN customer c ON o.customer_id = c.customer_id " +
            "LEFT JOIN order_item oi ON o.order_id = oi.order_id " +
            "WHERE o.customer_id = #{customerId} " +
            "GROUP BY o.order_id, o.order_no, c.customer_name, o.status ORDER BY o.ordered_at DESC")
    public List<OrderSummaryView> FindSummariesByCustomerId(@Param("customerId") Long customerId) {
        // TODO: 특정 고객 ID 기준 주문 요약 목록 조회 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * ResultSet 레코드를 OrderSummaryView 객체로 변환하는 헬퍼 메서드입니다.
     *
     * @param resultSet 쿼리 결과셋
     * @return 매핑된 OrderSummaryView 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private OrderSummaryView MapResultSetToSummary(ResultSet resultSet) throws SQLException {
        OrderSummaryView summary = new OrderSummaryView();
        return summary;
    }

    /**
     * 주문 요약 정보 DTO 중첩 클래스입니다.
     */
    public static class OrderSummaryView {
        private Long orderId;
        private String orderNo;
        private String customerName;
        private String status;
        private int itemCount;
        private double totalAmount;

        public OrderSummaryView() {
        }
    }

    /**
     * 주문 상세 정보 DTO 중첩 클래스입니다.
     */
    public static class OrderDetailView {
        private Long orderId;
        private String orderNo;
        private String customerName;
        private String customerPhone;
        private String customerAddress;
        private LocalDateTime orderDate;
        private String status;
        private double totalAmount;
        private List<OrderItemDetail> items = new ArrayList<>();

        public OrderDetailView() {
        }

        public static class OrderItemDetail {
            private String productName;
            private double unitPrice;
            private int quantity;
            private double subtotal;

            public OrderItemDetail() {
            }
        }
    }
}
