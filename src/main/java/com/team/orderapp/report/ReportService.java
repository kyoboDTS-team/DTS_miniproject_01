package com.team.orderapp.report;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 보고서 생성 및 통계 분석 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
public class ReportService {

    private final ReportDao reportDao;

    public ReportService() {
        this.reportDao = new ReportDao();
    }

    public ReportService(ReportDao InReportDao) {
        this.reportDao = InReportDao;
    }

    /**
     * 특정 일자의 일간 매출 및 주문 요약 보고서를 생성합니다.
     *
     * @param InDate 조회 대상 일자
     * @return 일간 집계 요약 객체
     */
    public OrderAmountSummary GetDailyRevenueSummary(LocalDate InDate) {
        return reportDao.AggregateByPeriod(InDate, InDate);
    }

    /**
     * 특정 년/월의 월간 매출 및 주문 요약 보고서를 생성합니다.
     *
     * @param InYear 대상 연도
     * @param InMonth 대상 월 (1~12)
     * @return 월간 집계 요약 객체
     */
    public OrderAmountSummary GetMonthlyRevenueSummary(int InYear, int InMonth) {
        LocalDate startDate = LocalDate.of(InYear, InMonth, 1);
        LocalDate endDate = YearMonth.of(InYear, InMonth).atEndOfMonth();
        return reportDao.AggregateByPeriod(startDate, endDate);
    }

    /**
     * 가장 많이 판매된 상위 N개 상품 목록을 조회합니다.
     *
     * @param InLimit 조회할 상위 상품 수
     * @return 상위 상품 목록
     */
    public List<String> GetTopSellingProducts(int InLimit) {
        return reportDao.FindTopSellingProducts(InLimit);
    }
}
