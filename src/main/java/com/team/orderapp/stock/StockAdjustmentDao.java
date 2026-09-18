package com.team.orderapp.stock;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

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
}