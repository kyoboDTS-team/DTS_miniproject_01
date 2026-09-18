package com.team.orderapp.report;

import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 관리자 통계 조회 DAO
 *
 * 통계 기능은 DB 데이터를 변경하지 않고
 * SELECT 조회만 수행합니다.
 */
public interface ReportDao {


    // =========================================================
    // 1. 전체 매출 합계
    //
    // CONFIRMED 주문만 계산
    // RETURNED 주문은 제외
    // =========================================================
    @Select("""
        SELECT
            COALESCE(
                SUM(oi.quantity * oi.unit_price),
                0
            )
        FROM orders o
        JOIN order_item oi
            ON o.order_id = oi.order_id
        WHERE o.status = 'CONFIRMED'
        """)
    BigDecimal GetTotalSales();


    // =========================================================
    // 2. 일별 주문 / 매출 통계
    //
    // COUNT(DISTINCT o.order_id)
    // → 주문 건수를 order_item 행 수로 세지 않도록 주의
    // =========================================================
    @Select("""
        SELECT
            CAST(o.ordered_at AS DATE) AS "orderDate",

            COUNT(DISTINCT o.order_id) AS "orderCount",

            SUM(oi.quantity) AS "totalQuantity",

            SUM(
                oi.quantity * oi.unit_price
            ) AS "totalSales"

        FROM orders o

        JOIN order_item oi
            ON o.order_id = oi.order_id

        WHERE o.status = 'CONFIRMED'

        GROUP BY
            CAST(o.ordered_at AS DATE)

        ORDER BY
            CAST(o.ordered_at AS DATE)
        """)
    List<DailySalesStat> GetDailySalesStats();


    // =========================================================
    // 3. 상품별 판매 통계
    //
    // 반품된 주문은 제외하고
    // 상품별 판매 수량과 매출 계산
    // =========================================================
    @Select("""
        SELECT
            p.product_id AS "productId",

            p.product_code AS "productCode",

            p.product_name AS "productName",

            SUM(oi.quantity) AS "totalQuantity",

            SUM(
                oi.quantity * oi.unit_price
            ) AS "totalSales"

        FROM orders o

        JOIN order_item oi
            ON o.order_id = oi.order_id

        JOIN product p
            ON oi.product_id = p.product_id

        WHERE o.status = 'CONFIRMED'

        GROUP BY
            p.product_id,
            p.product_code,
            p.product_name

        ORDER BY
            "totalSales" DESC
        """)
    List<ProductSalesStat> GetProductSalesStats();
}