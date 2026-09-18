package com.team.orderapp.product;

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
}