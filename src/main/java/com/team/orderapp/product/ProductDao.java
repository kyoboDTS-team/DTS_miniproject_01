package com.team.orderapp.product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 상품 정보 데이터베이스 접근 객체(DAO) 클래스입니다.
 */
public class ProductDao {

    /**
     * 상품 정보를 데이터베이스에 삽입합니다.
     *
     * @param InProduct 저장할 Product 객체
     * @return 저장 성공 여부
     */
    public boolean Insert(Product InProduct) {
        // TODO: INSERT INTO products ... 쿼리 구현
        return false;
    }

    /**
     * 식별자(ID)로 상품 정보를 조회합니다.
     *
     * @param InProductId 조회할 상품 ID
     * @return 조회된 Product Optional 객체
     */
    public Optional<Product> FindById(Long InProductId) {
        // TODO: SELECT FROM products WHERE product_id = ? 쿼리 구현
        return Optional.empty();
    }

    /**
     * 전체 상품 목록을 조회합니다.
     *
     * @return 상품 목록 리스트
     */
    public List<Product> FindAll() {
        // TODO: 전체 상품 SELECT 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * 상품 정보를 갱신합니다.
     *
     * @param InProduct 갱신할 Product 객체
     * @return 갱신 성공 여부
     */
    public boolean Update(Product InProduct) {
        // TODO: UPDATE products ... 쿼리 구현
        return false;
    }

    /**
     * 상품의 현재 재고 수량을 증감시킵니다.
     *
     * @param InProductId 상품 식별자
     * @param InQuantityDelta 변경할 수량 (양수: 입고, 음수: 출고)
     * @return 수량 갱신 성공 여부
     */
    public boolean UpdateStock(Long InProductId, int InQuantityDelta) {
        // TODO: UPDATE products SET current_stock = current_stock + ? WHERE product_id = ?
        return false;
    }

    /**
     * 식별자로 상품 정보를 삭제합니다.
     *
     * @param InProductId 삭제할 상품 ID
     * @return 삭제 성공 여부
     */
    public boolean DeleteById(Long InProductId) {
        // TODO: DELETE FROM products WHERE product_id = ? 쿼리 구현
        return false;
    }

    /**
     * ResultSet 레코드를 Product 도메인 객체로 변환하는 헬퍼 메서드입니다.
     *
     * @param InResultSet 조회 결과셋
     * @return 매핑된 Product 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private Product MapResultSetToProduct(ResultSet InResultSet) throws SQLException {
        Product product = new Product();
        product.SetProductId(InResultSet.getLong("product_id"));
        product.SetName(InResultSet.getString("name"));
        product.SetPrice(InResultSet.getDouble("price"));
        product.SetCurrentStock(InResultSet.getInt("current_stock"));
        product.SetDescription(InResultSet.getString("description"));
        product.SetCategory(InResultSet.getString("category"));
        return product;
    }
}
