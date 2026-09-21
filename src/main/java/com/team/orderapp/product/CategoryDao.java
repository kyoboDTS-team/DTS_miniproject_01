package com.team.orderapp.product;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 DAO
 */
public interface CategoryDao {

    // =========================================================
    // 전체 카테고리 조회
    // =========================================================
    @Select("""
        SELECT
            category_id,
            parent_category_id,
            category_code,
            category_name
        FROM category
        ORDER BY category_code
        """)
    List<Category> FindAll();


    // =========================================================
    // ID 조회
    // =========================================================
    @Select("""
        SELECT
            category_id,
            parent_category_id,
            category_code,
            category_name
        FROM category
        WHERE category_id = #{categoryId}
        """)
    Optional<Category> FindById(
            @Param("categoryId") Long categoryId
    );


    // =========================================================
    // 코드 중복 확인
    // =========================================================
    @Select("""
        SELECT EXISTS (
            SELECT 1
            FROM category
            WHERE category_code = #{categoryCode}
        )
        """)
    boolean ExistsByCode(
            @Param("categoryCode") String categoryCode
    );


    // =========================================================
    // 이름 중복 확인
    // =========================================================
    @Select("""
        SELECT EXISTS (
            SELECT 1
            FROM category
            WHERE category_name = #{categoryName}
        )
        """)
    boolean ExistsByName(
            @Param("categoryName") String categoryName
    );


    // =========================================================
    // 수정 시 자기 자신을 제외한 코드 중복 검사
    // =========================================================
    @Select("""
        SELECT EXISTS (
            SELECT 1
            FROM category
            WHERE category_code = #{categoryCode}
              AND category_id <> #{categoryId}
        )
        """)
    boolean ExistsByCodeExceptId(
            @Param("categoryCode") String categoryCode,
            @Param("categoryId") Long categoryId
    );


    // =========================================================
    // 수정 시 자기 자신 제외 이름 중복 검사
    // =========================================================
    @Select("""
        SELECT EXISTS (
            SELECT 1
            FROM category
            WHERE category_name = #{categoryName}
              AND category_id <> #{categoryId}
        )
        """)
    boolean ExistsByNameExceptId(
            @Param("categoryName") String categoryName,
            @Param("categoryId") Long categoryId
    );


    // =========================================================
    // 카테고리 등록
    // =========================================================
    @Insert("""
        INSERT INTO category (
            parent_category_id,
            category_code,
            category_name
        )
        VALUES (
            #{parentCategoryId},
            #{categoryCode},
            #{categoryName}
        )
        """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "categoryId",
            keyColumn = "category_id"
    )
    boolean Insert(Category category);


    // =========================================================
    // 카테고리 수정
    // =========================================================
    @Update("""
        UPDATE category
        SET
            parent_category_id = #{parentCategoryId},
            category_code = #{categoryCode},
            category_name = #{categoryName}
        WHERE category_id = #{categoryId}
        """)
    boolean Update(Category category);


    // =========================================================
    // 하위 카테고리 존재 여부
    // =========================================================
    @Select("""
        SELECT EXISTS (
            SELECT 1
            FROM category
            WHERE parent_category_id = #{categoryId}
        )
        """)
    boolean HasChildren(
            @Param("categoryId") Long categoryId
    );


    // =========================================================
    // 해당 카테고리를 사용하는 상품 존재 여부
    // =========================================================
    @Select("""
        SELECT EXISTS (
            SELECT 1
            FROM product
            WHERE category_id = #{categoryId}
        )
        """)
    boolean HasProducts(
            @Param("categoryId") Long categoryId
    );


    // =========================================================
    // 카테고리 삭제
    // =========================================================
    @Delete("""
        DELETE FROM category
        WHERE category_id = #{categoryId}
        """)
    boolean DeleteById(
            @Param("categoryId") Long categoryId
    );
}