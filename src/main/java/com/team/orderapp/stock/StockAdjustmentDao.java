package com.team.orderapp.stock;

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
     * @param InAdjustment 저장할 StockAdjustment 객체
     * @return 저장 성공 여부
     */
    public boolean Insert(StockAdjustment InAdjustment) {
        // TODO: INSERT INTO stock_adjustments ... 쿼리 구현
        return false;
    }

    /**
     * 특정 상품의 재고 조정 기록 목록을 조회합니다.
     *
     * @param InProductId 상품 식별자
     * @return 재고 조정 기록 리스트
     */
    public List<StockAdjustment> FindByProductId(Long InProductId) {
        // TODO: SELECT FROM stock_adjustments WHERE product_id = ? 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * 전체 재고 조정 기록 목록을 조회합니다.
     *
     * @return 전체 재고 조정 기록 리스트
     */
    public List<StockAdjustment> FindAll() {
        // TODO: SELECT FROM stock_adjustments 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * ResultSet 레코드를 StockAdjustment 도메인 객체로 변환하는 헬퍼 메서드입니다.
     *
     * @param InResultSet 쿼리 결과셋
     * @return 매핑된 StockAdjustment 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private StockAdjustment MapResultSetToAdjustment(ResultSet InResultSet) throws SQLException {
        StockAdjustment adjustment = new StockAdjustment();
        adjustment.SetAdjustmentId(InResultSet.getLong("adjustment_id"));
        adjustment.SetProductId(InResultSet.getLong("product_id"));
        adjustment.SetQuantityDelta(InResultSet.getInt("quantity_delta"));
        adjustment.SetReason(InResultSet.getString("reason"));
        adjustment.SetAdjustedBy(InResultSet.getString("adjusted_by"));
        return adjustment;
    }
}
