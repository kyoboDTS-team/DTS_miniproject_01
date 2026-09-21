package com.team.orderapp.auth;

import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.customer.Customer;
import com.team.orderapp.customer.CustomerDao;
import org.apache.ibatis.session.SqlSession;

/**
 * 로그인 인증, 회원가입 등 계정 관련 업무 로직을 처리합니다.
 */
public class AuthService {

    private SqlSession OpenSessionOrThrow() {
        SqlSession session = DbConnectionFactory.OpenSession();
        if (session == null) {
            throw new IllegalStateException("DB 연결에 실패했습니다.");
        }
        return session;
    }

    /**
     * 이미 가입된 이메일인지 확인합니다. 회원가입 화면에서 이메일 입력 직후 바로 확인할 때 사용합니다.
     *
     * @param email 확인할 이메일
     * @return 이미 사용 중이면 true
     */
    public boolean IsEmailTaken(String email) {
        try (SqlSession session = OpenSessionOrThrow()) {
            AppUserDao appUserDao = session.getMapper(AppUserDao.class);
            return appUserDao.FindByEmail(email.toLowerCase()).isPresent();
        }
    }

    /**
     * 신규 회원가입을 처리합니다. 로그인 계정(app_user)과 고객 프로필(customer)을
     * 한 트랜잭션으로 같이 저장합니다 — 계정만 만들어지고 고객 정보 저장이 실패하는 일이 없도록 하기 위함입니다.
     *
     * @param email        로그인 이메일(아이디)
     * @param password     평문 비밀번호 (여기서 해싱해서 저장)
     * @param customerName 고객 이름
     * @param phone        전화번호
     * @return 회원가입 성공 여부
     */
    public boolean SignUp(String email, String password, String customerName, String phone) {
        try (SqlSession session = OpenSessionOrThrow()) {
            AppUserDao appUserDao = session.getMapper(AppUserDao.class);

            AppUser user = new AppUser();
            user.setEmail(email.toLowerCase());
            user.setPasswordHash(PasswordHasher.Hash(password));
            user.setRoleCode("CUSTOMER");
            user.setIsActive(true);

            boolean userSaved = appUserDao.Save(user);
            if (!userSaved || user.getUserId() == null) {
                return false;
            }

            CustomerDao customerDao = session.getMapper(CustomerDao.class);
            Customer customer = new Customer();
            customer.setUserId(user.getUserId());
            customer.setCustomerName(customerName);
            customer.setPhone(phone.replace("-", ""));

            boolean customerSaved = customerDao.Insert(customer);
            if (!customerSaved) {
                session.rollback();
                return false;
            }

            session.commit();
            return true;
        }
    }
}
