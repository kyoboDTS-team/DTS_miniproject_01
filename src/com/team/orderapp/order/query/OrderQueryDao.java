package com.team.orderapp.order.query;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<OrderSummaryView> FindSummaries() {
        // TODO: orders 테이블과 customers 테이블을 조인하여 요약 목록 조회 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * 주문 식별자(ID)로 단건 상세 정보를 조회합니다.
     *
     * @param InOrderId 주문 식별자
     * @return 주문 상세 View Optional 객체
     */
    public Optional<OrderDetailView> FindDetailById(Long InOrderId) {
        // TODO: orders, customers, order_items, products를 조인하여 상세 조회 쿼리 구현
        return Optional.empty();
    }

    /**
     * 특정 고객의 주문 요약 목록을 조회합니다.
     *
     * @param InCustomerId 고객 식별자
     * @return 해당 고객의 주문 요약 View 목록
     */
    public List<OrderSummaryView> FindSummariesByCustomerId(Long InCustomerId) {
        // TODO: 특정 고객 ID 기준 주문 요약 목록 조회 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * ResultSet 레코드를 OrderSummaryView 객체로 변환하는 헬퍼 메서드입니다.
     *
     * @param InResultSet 쿼리 결과셋
     * @return 매핑된 OrderSummaryView 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private OrderSummaryView MapResultSetToSummary(ResultSet InResultSet) throws SQLException {
        OrderSummaryView summary = new OrderSummaryView();
        summary.SetOrderId(InResultSet.getLong("order_id"));
        summary.SetCustomerName(InResultSet.getString("customer_name"));
        summary.SetStatus(InResultSet.getString("status"));
        summary.SetItemCount(InResultSet.getInt("item_count"));
        summary.SetTotalAmount(InResultSet.getDouble("total_amount"));
        return summary;
    }
}
