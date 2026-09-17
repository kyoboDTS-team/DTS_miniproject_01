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
    //
    // 이 메서드는 지금부터 상품 등록 기능에서는 사용하지 않을 예정 나중에 삭제해도 됨
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


    /**
     * 식별자(ID)로 상품 정보를 조회합니다.
     */

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


    /**
     * 전체 상품 목록을 조회합니다.
     */
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


    /**
     * 실제 상품 등록에서 사용할 INSERT
     */
    // =========================================================
    // 앞으로 ProductService에서는 이 메서드를 사용
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

    // DB에서 자동 생성한 product_id를
    // INSERT 후 Product 객체에 다시 넣어줌
    @Options(
            useGeneratedKeys = true,
            keyProperty = "productId",
            keyColumn = "product_id"
    )
    boolean Insert(Product product);


    /**
     * 상품 정보를 수정합니다.
     */
    @Update("""
        UPDATE product
        SET
            product_name = #{productName},
            price = #{price},
            stock_quantity = #{stockQuantity},
            sale_status = #{saleStatus}
        WHERE product_id = #{productId}
        """)
    boolean Update(Product product);


    /**
     * 상품 재고 증감
     */
    @Update("""
        UPDATE product
        SET stock_quantity = stock_quantity + #{delta}
        WHERE product_id = #{productId}
        """)
    boolean UpdateStock(
            @Param("productId") Long productId,
            @Param("delta") int delta
    );


    /**
     * 상품 삭제
     */
    @Delete("""
        DELETE FROM product
        WHERE product_id = #{productId}
        """)
    boolean DeleteById(
            @Param("productId") Long productId
    );
}