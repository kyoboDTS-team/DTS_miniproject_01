package com.team.orderapp.product;

import com.team.orderapp.stock.SerialStockConsistency;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    // =========================================================
    // 주문 배정용: 판매 가능한 시리얼을 수량만큼 조회
    // 작업: 주문/반품 시리얼 배정 / 작업자: 김상진(Dorazee0209)
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
          AND unit_status = 'AVAILABLE'
        ORDER BY product_unit_id
        LIMIT #{limit}
        """)
    List<ProductUnit> FindAvailableByProductId(
            @Param("productId") Long productId,
            @Param("limit") int limit
    );


    // =========================================================
    // 주문/반품: 시리얼 상태 변경 (AVAILABLE <-> SOLD)
    // 현재 상태가 기대값과 같을 때만 바뀐다(조건부 UPDATE).
    // 바뀐 행 수가 0이면 그 사이 다른 처리가 선점한 것이므로 호출한 쪽에서 거절한다.
    // 작업: 주문/반품 시리얼 배정 / 작업자: 김상진(Dorazee0209)
    // =========================================================
    @Update("""
        UPDATE product_unit
        SET unit_status = #{newStatus}
        WHERE product_unit_id = #{productUnitId}
          AND unit_status = #{expectedStatus}
        """)
    int UpdateStatus(
            @Param("productUnitId") Long productUnitId,
            @Param("expectedStatus") String expectedStatus,
            @Param("newStatus") String newStatus
    );
}
