package com.team.orderapp.stock;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 재고 조정 이력 DAO
 */
public interface StockAdjustmentDao {

    // =========================================================
    // 재고 조정 이력 저장
    // =========================================================
    @Insert("""
        INSERT INTO stock_adjustment (
            product_id,
            quantity_delta,
            reason,
            adjusted_by_user_id
        )
        VALUES (
            #{productId},
            #{quantityDelta},
            #{reason},
            #{adjustedByUserId}
        )
        """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "adjustmentId",
            keyColumn = "adjustment_id"
    )
    boolean Insert(StockAdjustment adjustment);

    // 전체 재고 변경 이력
    @Select("""
        SELECT
            sa.adjustment_id,
            sa.product_id,
            p.product_code,
            p.product_name,
            sa.quantity_delta,
            sa.reason,
            sa.adjusted_by_user_id,
            u.email AS adjusted_by_email,
            sa.adjusted_at
        FROM stock_adjustment sa
        JOIN product p ON sa.product_id = p.product_id
        LEFT JOIN app_user u ON sa.adjusted_by_user_id = u.user_id
        ORDER BY sa.adjusted_at DESC, sa.adjustment_id DESC
        """)
    List<StockAdjustmentHistory> FindAllHistory();


    // 특정 상품의 재고 변경 이력
    @Select("""
        SELECT
            sa.adjustment_id,
            sa.product_id,
            p.product_code,
            p.product_name,
            sa.quantity_delta,
            sa.reason,
            sa.adjusted_by_user_id,
            u.email AS adjusted_by_email,
            sa.adjusted_at
        FROM stock_adjustment sa
        JOIN product p ON sa.product_id = p.product_id
        LEFT JOIN app_user u ON sa.adjusted_by_user_id = u.user_id
        WHERE sa.product_id = #{productId}
        ORDER BY sa.adjusted_at DESC, sa.adjustment_id DESC
        """)
    List<StockAdjustmentHistory> FindHistoryByProductId(
            @Param("productId") Long productId
    );


    // 기간별 재고 변경 이력
    @Select("""
        SELECT
            sa.adjustment_id,
            sa.product_id,
            p.product_code,
            p.product_name,
            sa.quantity_delta,
            sa.reason,
            sa.adjusted_by_user_id,
            u.email AS adjusted_by_email,
            sa.adjusted_at
        FROM stock_adjustment sa
        JOIN product p ON sa.product_id = p.product_id
        LEFT JOIN app_user u ON sa.adjusted_by_user_id = u.user_id
        WHERE DATE(sa.adjusted_at) BETWEEN #{startDate} AND #{endDate}
        ORDER BY sa.adjusted_at DESC, sa.adjustment_id DESC
        """)
    List<StockAdjustmentHistory> FindHistoryByPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}