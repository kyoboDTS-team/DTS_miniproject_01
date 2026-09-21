package com.team.orderapp.report;

import com.team.orderapp.common.DbConnectionFactory;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.util.List;

/**
 * 관리자 통계 Service
 */
public class ReportService {


    // ============================================================
    // 전체 매출 합계
    // ============================================================

    public BigDecimal GetTotalSales() {

        try (SqlSession session = OpenSession()) {

            ReportDao reportDao =
                    session.getMapper(ReportDao.class);

            BigDecimal totalSales =
                    reportDao.GetTotalSales();


            // 데이터가 없을 경우에도 0 반환
            if (totalSales == null) {
                return BigDecimal.ZERO;
            }

            return totalSales;
        }
    }


    // ============================================================
    // 일별 주문 / 매출 통계
    // ============================================================

    public List<DailySalesStat> GetDailySalesStats() {

        try (SqlSession session = OpenSession()) {

            ReportDao reportDao =
                    session.getMapper(ReportDao.class);

            return reportDao.GetDailySalesStats();
        }
    }


    // ============================================================
    // 상품별 판매 통계
    // ============================================================

    public List<ProductSalesStat> GetProductSalesStats() {

        try (SqlSession session = OpenSession()) {

            ReportDao reportDao =
                    session.getMapper(ReportDao.class);

            return reportDao.GetProductSalesStats();
        }
    }


    // ============================================================
    // DB 연결 Helper
    // ============================================================

    private SqlSession OpenSession() {

        SqlSession session =
                DbConnectionFactory.OpenSession();

        if (session == null) {

            throw new IllegalStateException(
                    "DB 연결 설정이 초기화되지 않았습니다."
            );
        }

        return session;
    }

    // ============================================================
    // 관리자 현황 요약 조회
    // ============================================================
    public AdminDashboardStat GetAdminDashboardStat() {

        try (SqlSession session = OpenSession()) {
            ReportDao reportDao = session.getMapper(ReportDao.class);

            AdminDashboardStat stat = reportDao.GetAdminDashboardStat();

            // 기존 전체 매출 조회 기능 재사용
            BigDecimal totalSales = reportDao.GetTotalSales();
            stat.setTotalSales(totalSales != null ? totalSales : BigDecimal.ZERO);

            return stat;
        }
    }
}