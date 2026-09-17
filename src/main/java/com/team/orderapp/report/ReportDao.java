package com.team.orderapp.report;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 보고서 및 통계 집계 쿼리를 전담하는 데이터베이스 접근 객체(DAO) 클래스입니다.
 */
public class ReportDao {

    /**
     * 특정 기간 동안의 주문 매출 및 건수를 집계합니다.
     *
     * @param startDate 시작일자
     * @param endDate 종료일자
     * @return 집계된 OrderAmountSummary 객체
     */
    @Select("SELECT COUNT(*) AS total_order_count, COALESCE(SUM(oi.quantity * oi.unit_price), 0) AS total_revenue, " +
            "COALESCE(AVG(oi.quantity * oi.unit_price), 0) AS average_order_amount " +
            "FROM orders o JOIN order_item oi ON o.order_id = oi.order_id " +
            "WHERE o.status = 'CONFIRMED' AND o.ordered_at BETWEEN #{startDate} AND #{endDate}")
    public OrderAmountSummary AggregateByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate) {
        // TODO: SELECT COUNT(*), SUM(total_amount), AVG(total_amount) FROM orders WHERE order_date BETWEEN ...
        return new OrderAmountSummary();
    }

    /**
     * 가장 많이 판매된 상위 상품 목록을 집계 조회합니다.
     *
     * @param limit 조회할 상위 N개 개수
     * @return 집계 결과 문자열 목록 (상품명: 판매수량)
     */
    @Select("SELECT p.product_name || ': ' || SUM(oi.quantity) || '개' " +
            "FROM order_item oi JOIN product p ON oi.product_id = p.product_id " +
            "JOIN orders o ON oi.order_id = o.order_id WHERE o.status = 'CONFIRMED' " +
            "GROUP BY p.product_name ORDER BY SUM(oi.quantity) DESC LIMIT #{limit}")
    public List<String> FindTopSellingProducts(@Param("limit") int limit) {
        // TODO: SELECT p.name, SUM(oi.quantity) FROM order_items oi JOIN products p ... GROUP BY ... ORDER BY ... LIMIT ?
        return new ArrayList<>();
    }

    /**
     * ResultSet 레코드를 OrderAmountSummary 객체로 매핑하는 헬퍼 메서드입니다.
     *
     * @param resultSet 쿼리 결과셋
     * @param periodName 기간 명칭
     * @return 매핑된 OrderAmountSummary 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private OrderAmountSummary MapResultSetToAmountSummary(ResultSet resultSet, String periodName) throws SQLException {
        OrderAmountSummary summary = new OrderAmountSummary();
        return summary;
    }

    /**
     * 기간별 주문 매출 및 통계 집계 결과를 담는 DTO 중첩 클래스입니다.
     */
    public static class OrderAmountSummary {
        private String period;
        private int totalOrderCount;
        private double totalRevenue;
        private double averageOrderAmount;

        public OrderAmountSummary() {
        }
    }
}
