package com.team.orderapp.stock;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 재고 조정 내역 데이터베이스 접근 객체(DAO) 클래스입니다.
 */
public class StockAdjustmentDao {

    /**
     * 재고 조정 기록을 데이터베이스에 등록합니다.
     *
     * @param adjustment 저장할 StockAdjustment 객체
     * @return 저장 성공 여부
     */
    @Insert("INSERT INTO stock_adjustment (product_id, quantity_delta, reason, adjusted_by_user_id) " +
            "VALUES (#{productId}, #{quantityDelta}, #{reason}, #{adjustedByUserId})")
    public boolean Insert(StockAdjustment adjustment) {
        // TODO: INSERT INTO stock_adjustment ... 쿼리 구현
        return false;
    }

    /**
     * 특정 상품의 재고 조정 기록 목록을 조회합니다.
     *
     * @param productId 상품 식별자
     * @return 재고 조정 기록 리스트
     */
    @Select("SELECT adjustment_id, product_id, quantity_delta, reason, adjusted_by_user_id, adjusted_at " +
            "FROM stock_adjustment WHERE product_id = #{productId} ORDER BY adjusted_at DESC")
    public List<StockAdjustment> FindByProductId(@Param("productId") Long productId) {
        // TODO: SELECT FROM stock_adjustment WHERE product_id = ? 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * 전체 재고 조정 기록 목록을 조회합니다.
     *
     * @return 전체 재고 조정 기록 리스트
     */
    @Select("SELECT adjustment_id, product_id, quantity_delta, reason, adjusted_by_user_id, adjusted_at " +
            "FROM stock_adjustment ORDER BY adjusted_at DESC")
    public List<StockAdjustment> FindAll() {
        // TODO: SELECT FROM stock_adjustment 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * ResultSet 레코드를 StockAdjustment 도메인 객체로 변환하는 헬퍼 메서드입니다.
     *
     * @param resultSet 쿼리 결과셋
     * @return 매핑된 StockAdjustment 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private StockAdjustment MapResultSetToAdjustment(ResultSet resultSet) throws SQLException {
        StockAdjustment adjustment = new StockAdjustment();
        return adjustment;
    }
}
