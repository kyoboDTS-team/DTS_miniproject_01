package com.team.orderapp.customer;

import com.team.orderapp.auth.AppUser;
import com.team.orderapp.auth.AppUserDao;
import com.team.orderapp.auth.PasswordHasher;
import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.order.query.OrderQueryDao;
import com.team.orderapp.order.query.OrderSummaryView;
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
     * 전체 고객 수를 조회합니다.
     *
     * @return 전체 고객 수
     */
    public long CountAll() {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.CountAll();
        }
    }

    /**
     * 고객 목록을 페이지 단위로 조회합니다.
     *
     * @param pageNumber 1부터 시작하는 페이지 번호
     * @param pageSize   한 페이지에 보여줄 개수
     * @return 해당 페이지에 속하는 고객 목록
     */
    public List<Customer> FindPage(int pageNumber, int pageSize) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            int offset = (pageNumber - 1) * pageSize;
            return customerDao.FindPage(pageSize, offset);
        }
    }

    /**
     * 이름으로 고객을 검색합니다. 동명이인이 있으면 여러 명이 반환될 수 있습니다.
     *
     * @param customerName 검색할 이름
     * @return 검색된 고객 목록
     */
    public List<Customer> SearchByName(String customerName) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.FindByName(customerName);
        }
    }

    /**
     * 이름 또는 전화번호로 고객을 검색합니다.
     *
     * @param keyword 검색어
     * @return 검색된 고객 목록
     */
    public List<Customer> SearchByNameOrPhone(String keyword) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.SearchByNameOrPhone(keyword);
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
     * 이메일로 고객을 정확히 한 명 조회합니다. 로그인한 회원 본인 정보를 찾을 때 사용합니다.
     *
     * @param email 조회할 이메일
     * @return 조회된 Customer Optional 객체
     */
    public Optional<Customer> FindByEmail(String email) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.FindByEmail(email);
        }
    }

    /**
     * 이름 또는 이메일(아이디)로 고객을 검색합니다. 정보 수정 대상을 찾을 때 사용합니다.
     *
     * @param keyword 검색어
     * @return 검색된 고객 목록
     */
    public List<Customer> SearchByNameOrEmail(String keyword) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.SearchByNameOrEmail(keyword);
        }
    }

    /**
     * 전화번호로 이미 등록된 고객이 있는지 확인합니다. 회원가입 시 중복 체크에 사용합니다.
     *
     * @param phone 확인할 전화번호
     * @return 이미 등록되어 있으면 true
     */
    public boolean IsPhoneTaken(String phone) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.CountByPhone(phone) > 0;
        }
    }

    /**
     * 전화번호로 이미 등록된 다른 고객이 있는지 확인합니다(본인은 제외). 정보 수정 시 중복 체크에 사용합니다.
     *
     * @param phone             확인할 전화번호
     * @param excludeCustomerId 제외할 고객 ID (본인)
     * @return 이미 등록되어 있으면 true
     */
    public boolean IsPhoneTaken(String phone, Long excludeCustomerId) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            return customerDao.CountByPhoneExcluding(phone, excludeCustomerId) > 0;
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
     * 입력한 비밀번호가 현재 저장된 비밀번호와 일치하는지 확인합니다. 비밀번호 변경 전 본인 확인용입니다.
     *
     * @param customer 확인 대상 고객 (userId가 설정되어 있어야 함)
     * @param password 확인할 평문 비밀번호
     * @return 일치하면 true
     */
    public boolean VerifyPassword(Customer customer, String password) {
        try (SqlSession session = OpenSessionOrThrow()) {
            AppUserDao appUserDao = session.getMapper(AppUserDao.class);
            Optional<AppUser> user = appUserDao.FindById(customer.getUserId());
            return user.isPresent() && PasswordHasher.Verify(password, user.get().getPasswordHash());
        }
    }

    /**
     * 비밀번호를 변경합니다. 새 비밀번호는 여기서 해싱해서 저장합니다.
     * 호출 전에 반드시 VerifyPassword()로 현재 비밀번호를 확인해야 합니다.
     *
     * @param customer    대상 고객 (userId가 설정되어 있어야 함)
     * @param newPassword 새 평문 비밀번호
     * @return 변경 성공 여부
     */
    public boolean UpdatePassword(Customer customer, String newPassword) {
        try (SqlSession session = OpenSessionOrThrow()) {
            AppUserDao appUserDao = session.getMapper(AppUserDao.class);
            boolean updated = appUserDao.UpdatePassword(customer.getUserId(), PasswordHasher.Hash(newPassword));
            session.commit();
            return updated;
        }
    }

    /**
     * 회원가입 때 함께 만들어진 로그인 계정(app_user)의 이메일(ID)을 수정합니다.
     * 이미 다른 회원이 쓰는 이메일이면 uq_app_user_email 제약 위반 예외가 발생합니다.
     *
     * @param customer 수정 대상 고객 (userId가 설정되어 있어야 함)
     * @param newEmail 새 이메일
     * @return 수정 성공 여부
     */
    public boolean UpdateEmail(Customer customer, String newEmail) {
        try (SqlSession session = OpenSessionOrThrow()) {
            AppUserDao appUserDao = session.getMapper(AppUserDao.class);
            boolean updated = appUserDao.UpdateEmail(customer.getUserId(), newEmail);
            session.commit();
            return updated;
        }
    }

    /**
     * 회원 계정의 활성 상태를 바꿉니다. 관리자가 로그인 계정을 잠그거나(비활성화) 풀 때(활성화) 사용합니다.
     *
     * @param customer 대상 고객 (userId가 설정되어 있어야 함)
     * @param isActive 새 활성 상태
     * @return 수정 성공 여부
     */
    public boolean UpdateActiveStatus(Customer customer, boolean isActive) {
        try (SqlSession session = OpenSessionOrThrow()) {
            AppUserDao appUserDao = session.getMapper(AppUserDao.class);
            boolean updated = appUserDao.UpdateActiveStatus(customer.getUserId(), isActive);
            session.commit();
            return updated;
        }
    }

    /**
     * 이 고객의 주문 이력을 조회합니다. 삭제 전에 주문 이력이 있는지 미리 확인할 때 사용합니다.
     *
     * @param customerId 조회할 고객 ID
     * @return 주문 요약 목록 (주문 이력이 없으면 빈 목록)
     */
    public List<OrderSummaryView> FindOrderHistory(Long customerId) {
        try (SqlSession session = OpenSessionOrThrow()) {
            OrderQueryDao orderQueryDao = session.getMapper(OrderQueryDao.class);
            return orderQueryDao.FindSummariesByCustomerId(customerId);
        }
    }

    /**
     * 고객 정보와, 회원가입 때 함께 만들어진 로그인 계정(app_user)을 한 트랜잭션으로 같이 삭제합니다.
     * 이 고객의 주문 이력이 있으면 orders 테이블의 FK 제약(ON DELETE RESTRICT)에 의해
     * DB가 삭제를 거절하며 예외가 발생합니다. customer가 app_user를 참조하는 구조라서,
     * app_user보다 customer를 먼저 지워야 FK 제약에 걸리지 않습니다.
     *
     * @param customer 삭제할 고객 (customerId, userId가 설정되어 있어야 함)
     * @return 삭제 성공 여부
     */
    public boolean DeleteById(Customer customer) {
        try (SqlSession session = OpenSessionOrThrow()) {
            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            AppUserDao appUserDao = session.getMapper(AppUserDao.class);

            boolean customerDeleted = customerDao.DeleteById(customer.getCustomerId());
            if (!customerDeleted) {
                return false;
            }

            boolean userDeleted = appUserDao.DeleteById(customer.getUserId());
            if (!userDeleted) {
                return false;
            }

            session.commit();
            return true;
        }
    }
}
