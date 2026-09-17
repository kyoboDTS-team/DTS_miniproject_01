package com.team.orderapp.product;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 정보 데이터베이스 접근 객체(DAO/Mapper) 인터페이스입니다.
 */
public interface CategoryDao {

    // 이미 등록된 카테고리가 있는지 첫 번째 ID 조회
    @Select("SELECT category_id FROM category ORDER BY category_id ASC LIMIT 1")
    Long GetFirstCategoryId();

    // 카테고리가 없을 때 기본 카테고리 등록
    @Insert("INSERT INTO category (category_code, category_name) VALUES (#{code}, #{name})")
    void InsertCategory(@Param("code") String code, @Param("name") String name);

    @Select("SELECT category_id, parent_category_id, category_code, category_name FROM category WHERE category_id = #{categoryId}")
    Optional<Category> FindById(@Param("categoryId") Long categoryId);

    @Select("SELECT category_id, parent_category_id, category_code, category_name FROM category ORDER BY category_id")
    List<Category> FindAll();

    @Insert("INSERT INTO category (parent_category_id, category_code, category_name) VALUES (#{parentCategoryId}, #{categoryCode}, #{categoryName})")
    int Insert(Category category);

    @Update("UPDATE category SET parent_category_id = #{parentCategoryId}, category_code = #{categoryCode}, category_name = #{categoryName} WHERE category_id = #{categoryId}")
    int Update(Category category);

    @Delete("DELETE FROM category WHERE category_id = #{categoryId}")
    int DeleteById(@Param("categoryId") Long categoryId);
}
