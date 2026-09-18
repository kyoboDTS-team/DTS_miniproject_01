package com.team.orderapp.product;

import com.team.orderapp.stock.SerialStockConsistency;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 시리얼 관리 상품 DAO
 */
public interface ProductUnitDao {

    // =========================================================
    // 시리얼 번호 중복 확인
    // =========================================================
    @Select("""
        SELECT
            product_unit_id,
            product_id,
            serial_number,
            unit_status,
            created_at
        FROM product_unit
        WHERE serial_number = #{serialNumber}
        """)
    Optional<ProductUnit> FindBySerialNumber(
            @Param("serialNumber") String serialNumber
    );


    // =========================================================
    // 새로운 시리얼 상품 등록
    // =========================================================
    @Insert("""
        INSERT INTO product_unit (
            product_id,
            serial_number,
            unit_status
        )
        VALUES (
            #{productId},
            #{serialNumber},
            #{unitStatus}
        )
        """)
    boolean Insert(ProductUnit productUnit);


    // =========================================================
    // 특정 상품의 시리얼 목록 조회
    // =========================================================
    @Select("""
        SELECT
            product_unit_id,
            product_id,
            serial_number,
            unit_status,
            created_at
        FROM product_unit
        WHERE product_id = #{productId}
        ORDER BY product_unit_id
        """)
    List<ProductUnit> FindByProductId(
            @Param("productId") Long productId
    );

    // ============================================================
    // 전체 시리얼 상품 재고 정합성 조회
    // ============================================================
    @Select("""
    SELECT
        p.product_id,
        p.product_code,
        p.product_name,
        p.stock_quantity,
        COALESCE(
            SUM(
                CASE
                    WHEN pu.unit_status = 'AVAILABLE' THEN 1
                    ELSE 0
                END
            ),
            0
        ) AS available_unit_count
    FROM product p
    LEFT JOIN product_unit pu
        ON p.product_id = pu.product_id
    WHERE p.requires_serial = TRUE
    GROUP BY
        p.product_id,
        p.product_code,
        p.product_name,
        p.stock_quantity
    ORDER BY p.product_id
    """)
    List<SerialStockConsistency> FindSerialStockConsistency();
}