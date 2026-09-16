package com.team.orderapp.mapper;

import com.team.orderapp.model.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;

public interface ProductMapper {

    // 이미 등록된 카테고리가 있는지 첫 번째 ID 조회
    @Select("SELECT category_id FROM category ORDER BY category_id ASC LIMIT 1")
    Long GetFirstCategoryId();

    // 카테고리가 없을 때 기본 카테고리 등록
    @Insert("INSERT INTO category (category_code, category_name) VALUES (#{code}, #{name})")
    void InsertCategory(@Param("code") String code, @Param("name") String name);

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
}
