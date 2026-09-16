package com.team.orderapp.report;

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
     * @param InStartDate 시작일자
     * @param InEndDate 종료일자
     * @return 집계된 OrderAmountSummary 객체
     */
    public OrderAmountSummary AggregateByPeriod(LocalDate InStartDate, LocalDate InEndDate) {
        // TODO: SELECT COUNT(*), SUM(total_amount), AVG(total_amount) FROM orders WHERE order_date BETWEEN ...
        return new OrderAmountSummary(InStartDate + " ~ " + InEndDate, 0, 0.0, 0.0);
    }

    /**
     * 가장 많이 판매된 상위 상품 목록을 집계 조회합니다.
     *
     * @param InLimit 조회할 상위 N개 개수
     * @return 집계 결과 문자열 목록 (상품명: 판매수량)
     */
    public List<String> FindTopSellingProducts(int InLimit) {
        // TODO: SELECT p.name, SUM(oi.quantity) FROM order_items oi JOIN products p ... GROUP BY ... ORDER BY ... LIMIT ?
        return new ArrayList<>();
    }

    /**
     * ResultSet 레코드를 OrderAmountSummary 객체로 매핑하는 헬퍼 메서드입니다.
     *
     * @param InResultSet 쿼리 결과셋
     * @param InPeriodPeriodName 기간 명칭
     * @return 매핑된 OrderAmountSummary 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private OrderAmountSummary MapResultSetToAmountSummary(ResultSet InResultSet, String InPeriodPeriodName) throws SQLException {
        OrderAmountSummary summary = new OrderAmountSummary();
        summary.SetPeriod(InPeriodPeriodName);
        summary.SetTotalOrderCount(InResultSet.getInt(1));
        summary.SetTotalRevenue(InResultSet.getDouble(2));
        summary.SetAverageOrderAmount(InResultSet.getDouble(3));
        return summary;
    }
}
