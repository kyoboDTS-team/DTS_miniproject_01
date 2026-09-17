package com.team.orderapp.customer;

import com.team.orderapp.common.DbConnectionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Optional;

/**
 * 고객 관련 업무 로직을 처리하는 서비스 클래스입니다.
 */
public class CustomerService {

    /**
     * DB 세션을 열고, 연결에 실패하면 IllegalStateException을 던집니다.
     * DbConnectionFactory.OpenSession()은 연결 실패 시 null을 돌려주는데,
     * null을 그대로 쓰면 NullPointerException으로 이어져 원인을 알 수 없는 크래시가 나기 때문에
     * 여기서 "DB 연결 실패"임을 명확히 구분해 알립니다.
     *
     * @return 정상적으로 열린 SqlSession
     */
    private SqlSession OpenSessionOrThrow() {
        SqlSession session = DbConnectionFactory.OpenSession();
        if (session == null) {
            throw new IllegalStateException("DB 연결에 실패했습니다.");
        }
        return session;
    }

    /**
     * 전체 고객 목록을 조회합니다.
     *
     * @return 고객 목록 리스트
     */
    public List<Customer> FindAll() {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.FindAll();
        }
    }

    /**
     * 고객 번호로 고객 상세 정보를 조회합니다.
     *
     * @param customerId 조회할 고객 ID
     * @return 조회된 Customer Optional 객체
     */
    public Optional<Customer> FindById(Long customerId) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.FindById(customerId);
        }
    }

    /**
     * 고객 이름/전화번호를 수정합니다.
     *
     * @param customer customerId가 설정된 Customer 객체
     * @return 수정 성공 여부
     */
    public boolean Update(Customer customer) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            boolean updated = customerDao.Update(customer);
            session.commit();
            return updated;
        }
    }

    /**
     * 고객 정보를 삭제합니다. 이 고객의 주문 이력이 있으면 orders 테이블의 FK 제약(ON DELETE RESTRICT)에 의해
     * DB가 삭제를 거절하며 예외가 발생합니다.
     *
     * @param customerId 삭제할 고객 ID
     * @return 삭제 성공 여부
     */
    public boolean DeleteById(Long customerId) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            boolean deleted = customerDao.DeleteById(customerId);
            session.commit();
            return deleted;
        }
    }
}
