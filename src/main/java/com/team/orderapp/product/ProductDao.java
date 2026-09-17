package com.team.orderapp.product;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

    // 상품 객체 삽입 쿼리
    @Insert("INSERT INTO product (product_code, category_id, product_name, price, stock_quantity, reorder_level, sale_status) " +
            "VALUES (#{productCode}, #{categoryId}, #{productName}, #{price}, #{stockQuantity}, #{reorderLevel}, #{saleStatus})")
    void InsertProduct(Product product);

    // 단순 필드 기반 상품 삽입 쿼리 (이미 동일한 코드가 있으면 가격/이름 갱신)
    @Insert("INSERT INTO product (product_code, category_id, product_name, price, stock_quantity) " +
            "VALUES (#{productCode}, #{categoryId}, #{productName}, #{price}, #{stockQuantity}) " +
            "ON CONFLICT (product_code) DO UPDATE SET price = EXCLUDED.price, product_name = EXCLUDED.product_name")
    void InsertProductSimple(@Param("productCode") String productCode,
                             @Param("categoryId") Long categoryId,
                             @Param("productName") String productName,
                             @Param("price") BigDecimal price,
                             @Param("stockQuantity") int stockQuantity);

    // 전체 상품 조회 쿼리
    @Select("SELECT product_id, product_code, category_id, product_name, price, stock_quantity, reorder_level, sale_status, created_at " +
            "FROM product ORDER BY product_id")
    List<Product> GetAllProducts();

    /**
     * 식별자(ID)로 상품 정보를 조회합니다.
     *
     * @param productId 조회할 상품 ID
     * @return 조회된 Product Optional 객체
     */
    @Select("SELECT product_id, product_code, category_id, product_name, price, stock_quantity, reorder_level, sale_status, created_at FROM product WHERE product_id = #{productId}")
    Optional<Product> FindById(@Param("productId") Long productId);

    /**
     * 전체 상품 목록을 조회합니다.
     *
     * @return 상품 목록 리스트
     */
    @Select("SELECT product_id, product_code, category_id, product_name, price, stock_quantity, reorder_level, sale_status, created_at FROM product ORDER BY product_id")
    List<Product> FindAll();

    /**
     * 상품 정보를 데이터베이스에 삽입합니다.
     *
     * @param product 저장할 Product 객체
     * @return 저장 성공 여부
     */
    @Insert("INSERT INTO product (product_code, category_id, product_name, price, stock_quantity, reorder_level, sale_status, requires_serial) " +
            "VALUES (#{productCode}, #{categoryId}, #{productName}, #{price}, #{stockQuantity}, #{reorderLevel}, #{saleStatus}, #{requiresSerial})")
    boolean Insert(Product product);

    /**
     * 상품 정보를 갱신합니다.
     *
     * @param product 갱신할 Product 객체
     * @return 갱신 성공 여부
     */
    @Update("UPDATE product SET product_name = #{productName}, price = #{price}, stock_quantity = #{stockQuantity}, sale_status = #{saleStatus} WHERE product_id = #{productId}")
    boolean Update(Product product);

    /**
     * 상품의 현재 재고 수량을 증감시킵니다.
     *
     * @param productId 상품 식별자
     * @param delta 변경할 수량 (양수: 입고, 음수: 출고)
     * @return 수량 갱신 성공 여부
     */
    @Update("UPDATE product SET stock_quantity = stock_quantity + #{delta} WHERE product_id = #{productId}")
    boolean UpdateStock(@Param("productId") Long productId, @Param("delta") int delta);

    /**
     * 식별자로 상품 정보를 삭제합니다.
     *
     * @param productId 삭제할 상품 ID
     * @return 삭제 성공 여부
     */
    @Delete("DELETE FROM product WHERE product_id = #{productId}")
    boolean DeleteById(@Param("productId") Long productId);
}
