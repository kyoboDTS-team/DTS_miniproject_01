package com.team.orderapp.product;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 상품 정보 데이터베이스 접근 객체(DAO/Mapper) 인터페이스입니다.
 */
public interface ProductDao {

    // =========================================================
    // 기존 테스트용 상품 등록
    // =========================================================
    @Insert("""
            INSERT INTO product (
                product_code,
                category_id,
                product_name,
                price,
                stock_quantity,
                reorder_level,
                sale_status,
                requires_serial
            )
            VALUES (
                #{productCode},
                #{categoryId},
                #{productName},
                #{price},
                #{stockQuantity},
                #{reorderLevel},
                #{saleStatus},
                #{requiresSerial}
            )
            """)
    void InsertProduct(Product product);


    // =========================================================
    // 기존 간단 테스트용 INSERT
    // 현재 실제 상품 등록에서는 사용하지 않음
    // =========================================================
    @Insert("""
            INSERT INTO product (
                product_code,
                category_id,
                product_name,
                price,
                stock_quantity
            )
            VALUES (
                #{productCode},
                #{categoryId},
                #{productName},
                #{price},
                #{stockQuantity}
            )
            ON CONFLICT (product_code)
            DO UPDATE SET
                price = EXCLUDED.price,
                product_name = EXCLUDED.product_name
            """)
    void InsertProductSimple(
            @Param("productCode") String productCode,
            @Param("categoryId") Long categoryId,
            @Param("productName") String productName,
            @Param("price") BigDecimal price,
            @Param("stockQuantity") int stockQuantity
    );


    // =========================================================
    // 전체 상품 조회
    // =========================================================
    @Select("""
            SELECT
                product_id,
                product_code,
                category_id,
                product_name,
                price,
                stock_quantity,
                reorder_level,
                sale_status,
                requires_serial,
                created_at
            FROM product
            ORDER BY product_id
            """)
    List<Product> GetAllProducts();


    // =========================================================
    // 상품 ID로 조회
    // =========================================================
    @Select("""
            SELECT
                product_id,
                product_code,
                category_id,
                product_name,
                price,
                stock_quantity,
                reorder_level,
                sale_status,
                requires_serial,
                created_at
            FROM product
            WHERE product_id = #{productId}
            """)
    Optional<Product> FindById(
            @Param("productId") Long productId
    );


    // =========================================================
    // 전체 상품 조회
    // 박형준 담당 조회 기능에서 사용
    // =========================================================
    @Select("""
            SELECT
                product_id,
                product_code,
                category_id,
                product_name,
                price,
                stock_quantity,
                reorder_level,
                sale_status,
                requires_serial,
                created_at
            FROM product
            ORDER BY product_id
            """)
    List<Product> FindAll();


    // =========================================================
    // 실제 상품 등록
    // 담당 : 백종민
    // =========================================================
    @Insert("""
            INSERT INTO product (
                product_code,
                category_id,
                product_name,
                price,
                stock_quantity,
                reorder_level,
                sale_status,
                requires_serial
            )
            VALUES (
                #{productCode},
                #{categoryId},
                #{productName},
                #{price},
                #{stockQuantity},
                #{reorderLevel},
                #{saleStatus},
                #{requiresSerial}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "productId",
            keyColumn = "product_id"
    )
    boolean Insert(Product product);


    // =========================================================
    // 상품 수정
    // 담당 : 백종민
    //
    // 상품명 / 카테고리 / 가격 / 안전재고 기준만 수정
    //
    // 재고(stock_quantity)는 재고 관리에서 수정
    // 판매상태(sale_status)는 판매상태 변경 기능에서 수정
    // =========================================================
    @Update("""
            UPDATE product
            SET
                product_name = #{productName},
                category_id = #{categoryId},
                price = #{price},
                reorder_level = #{reorderLevel}
            WHERE product_id = #{productId}
            """)
    boolean Update(Product product);


    // =========================================================
    // 상품 판매 상태 변경
    // 백종민
    //
    // SELLING  : 판매중
    // STOPPED  : 판매중지
    // =========================================================
    @Update("""
            UPDATE product
            SET sale_status = #{saleStatus}
            WHERE product_id = #{productId}
            """)
    boolean UpdateSaleStatus(
            @Param("productId") Long productId,
            @Param("saleStatus") String saleStatus
    );

    // =========================================================
    // 상품 삭제 가능 여부 확인
    // 백종민
    //
    // 주문 / 재고조정 / 시리얼 이력이 하나라도 있으면 true
    // =========================================================
    @Select("""
            SELECT EXISTS (
                SELECT 1
                FROM order_item
                WHERE product_id = #{productId}
            
                UNION ALL
            
                SELECT 1
                FROM stock_adjustment
                WHERE product_id = #{productId}
            
                UNION ALL
            
                SELECT 1
                FROM product_unit
                WHERE product_id = #{productId}
            )
            """)
    boolean HasDeleteHistory(
            @Param("productId") Long productId
    );


    // =========================================================
    // 상품 삭제
    // 실제 삭제 가능한 상품만 호출
    // =========================================================
    @Delete("""
            DELETE FROM product
            WHERE product_id = #{productId}
            """)
    boolean DeleteById(
            @Param("productId") Long productId
    );


    // =========================================================
    // 상품 재고 증감
    //
    // 나중에 재고 관리 기능에서 사용
    // =========================================================
    @Update("""
            UPDATE product
            SET stock_quantity = stock_quantity + #{delta}
            WHERE product_id = #{productId}
            """)
    boolean UpdateStock(
            @Param("productId") Long productId,
            @Param("delta") int delta
    );
}