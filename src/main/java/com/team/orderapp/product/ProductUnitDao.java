package com.team.orderapp.product;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 시리얼 상품 개별 유닛 데이터베이스 접근 객체(DAO/Mapper) 인터페이스입니다.
 */
public interface ProductUnitDao {

    @Insert("INSERT INTO product_unit (product_id, serial_number, unit_status) VALUES (#{productId}, #{serialNumber}, #{unitStatus})")
    int Insert(ProductUnit productUnit);

    @Select("SELECT product_unit_id, product_id, serial_number, unit_status, created_at FROM product_unit WHERE product_unit_id = #{productUnitId}")
    Optional<ProductUnit> FindById(@Param("productUnitId") Long productUnitId);

    @Select("SELECT product_unit_id, product_id, serial_number, unit_status, created_at FROM product_unit WHERE serial_number = #{serialNumber}")
    Optional<ProductUnit> FindBySerialNumber(@Param("serialNumber") String serialNumber);

    @Select("SELECT product_unit_id, product_id, serial_number, unit_status, created_at FROM product_unit WHERE product_id = #{productId} AND unit_status = 'AVAILABLE'")
    List<ProductUnit> FindAvailableByProductId(@Param("productId") Long productId);

    @Select("SELECT product_unit_id, product_id, serial_number, unit_status, created_at FROM product_unit WHERE product_id = #{productId}")
    List<ProductUnit> FindByProductId(@Param("productId") Long productId);

    @Update("UPDATE product_unit SET unit_status = #{unitStatus} WHERE product_unit_id = #{productUnitId}")
    int UpdateStatus(@Param("productUnitId") Long productUnitId, @Param("unitStatus") String unitStatus);

    @Delete("DELETE FROM product_unit WHERE product_unit_id = #{productUnitId}")
    int DeleteById(@Param("productUnitId") Long productUnitId);
}
