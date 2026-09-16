package com.team.orderapp.customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 고객 정보 데이터베이스 접근 객체(DAO) 클래스입니다.
 */
public class CustomerDao {

    /**
     * 고객 정보를 데이터베이스에 삽입합니다.
     *
     * @param InCustomer 저장할 Customer 객체
     * @return 저장 성공 여부
     */
    public boolean Insert(Customer InCustomer) {
        // TODO: INSERT INTO customers ... 쿼리 구현
        return false;
    }

    /**
     * 식별자(ID)를 기준으로 고객 정보를 조회합니다.
     *
     * @param InCustomerId 조회할 고객 ID
     * @return 조회된 Customer Optional 객체
     */
    public Optional<Customer> FindById(Long InCustomerId) {
        // TODO: SELECT FROM customers WHERE customer_id = ? 쿼리 구현
        return Optional.empty();
    }

    /**
     * 전체 고객 목록을 조회합니다.
     *
     * @return 고객 목록 리스트
     */
    public List<Customer> FindAll() {
        // TODO: 전체 고객 SELECT 쿼리 구현
        return new ArrayList<>();
    }

    /**
     * 고객 정보를 갱신합니다.
     *
     * @param InCustomer 갱신할 Customer 객체
     * @return 갱신 성공 여부
     */
    public boolean Update(Customer InCustomer) {
        // TODO: UPDATE customers ... 쿼리 구현
        return false;
    }

    /**
     * 고객 식별자를 기준으로 정보를 삭제합니다.
     *
     * @param InCustomerId 삭제할 고객 ID
     * @return 삭제 성공 여부
     */
    public boolean DeleteById(Long InCustomerId) {
        // TODO: DELETE FROM customers WHERE customer_id = ? 쿼리 구현
        return false;
    }

    /**
     * ResultSet 레코드를 Customer 도메인 객체로 변환하는 헬퍼 메서드입니다.
     *
     * @param InResultSet 조회 결과셋
     * @return 매핑된 Customer 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private Customer MapResultSetToCustomer(ResultSet InResultSet) throws SQLException {
        Customer customer = new Customer();
        customer.SetCustomerId(InResultSet.getLong("customer_id"));
        customer.SetName(InResultSet.getString("name"));
        customer.SetEmail(InResultSet.getString("email"));
        customer.SetPhone(InResultSet.getString("phone"));
        customer.SetAddress(InResultSet.getString("address"));
        return customer;
    }
}
