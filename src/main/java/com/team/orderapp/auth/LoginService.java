package com.team.orderapp.auth;

import com.team.orderapp.cart.CartService;
import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.customer.Customer;
import com.team.orderapp.customer.CustomerDao;
import org.apache.ibatis.session.SqlSession;

import java.util.Optional;

/**
 * 로그인·로그아웃 업무 규칙을 처리하는 서비스입니다.
 *
 * 회원가입은 이태은 담당 AuthService에 있고, 이 클래스는 로그인 검증만 맡습니다.
 * 콘솔 출력은 하지 않고, 규칙 위반은 예외로 던져 LoginMenu가 화면에 표시합니다.
 */
public class LoginService {

    /*
     * 작업: 로그인·로그아웃 처리(계정 검증, 장바구니 초기화, 세션 기록)
     *
     * 작업자: 김상진
     */

    /** 어떤 이유로 실패했는지 알려 주지 않는다. 가입된 이메일인지 떠보는 것을 막기 위함(AUTH-02). */
    private static final String LOGIN_FAILED_MESSAGE = "이메일 또는 비밀번호가 올바르지 않습니다.";

    /**
     * 로그인을 처리합니다. 검증에 모두 성공하면 비회원 장바구니를 버리고, 회원이면
     * 그 회원의 기존 장바구니를 불러온 뒤 세션에 사용자 정보를 기록합니다.
     *
     * 조회만 하므로 commit이 없고, 두 DAO를 한 SqlSession에서 꺼내 씁니다.
     * 장바구니 처리는 검증이 끝난 뒤에 합니다 — 비밀번호를 잘못 입력한 비회원의
     * 장바구니가 사라지면 안 되기 때문입니다(T03).
     *
     * @param email    입력한 이메일 (대소문자·앞뒤 공백은 여기서 정리)
     * @param password 입력한 평문 비밀번호
     * @return 로그인한 사용자의 권한
     * @throws IllegalArgumentException 입력이 비었거나 계정·비밀번호가 맞지 않는 경우
     * @throws IllegalStateException    DB 연결 실패, 권한 코드가 이상하거나 회원인데 고객 정보가 없는 경우
     */
    public UserRole Login(String email, String password) {

        if (email == null || email.isBlank() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("이메일과 비밀번호를 입력해 주세요.");
        }

        // 회원가입도 소문자로 저장하므로, 대문자로 입력해도 같은 계정을 찾는다.
        String normalizedEmail = email.trim().toLowerCase();

        Long userId;
        UserRole role;
        Long customerId = null;

        try (SqlSession session = OpenSession()) {

            AppUser user = session.getMapper(AppUserDao.class)
                    .FindByEmail(normalizedEmail)
                    .orElseThrow(() -> new IllegalArgumentException(LOGIN_FAILED_MESSAGE));

            if (!PasswordHasher.Verify(password, user.getPasswordHash())) {
                throw new IllegalArgumentException(LOGIN_FAILED_MESSAGE);
            }

            // isActive는 래퍼 타입이라 null일 수 있다. 관리자가 비활성화한 계정도 여기서 걸린다.
            if (!Boolean.TRUE.equals(user.getIsActive())) {
                throw new IllegalArgumentException(LOGIN_FAILED_MESSAGE);
            }

            userId = user.getUserId();
            role = UserRole.FromCode(user.getRoleCode());

            if (role == UserRole.CUSTOMER) {

                Optional<Customer> customer = session.getMapper(CustomerDao.class)
                        .FindByUserId(userId);

                if (customer.isEmpty()) {
                    throw new IllegalStateException(
                            "회원 정보를 찾을 수 없습니다. 관리자에게 문의해 주세요."
                    );
                }

                customerId = customer.get().getCustomerId();
            }
        }

        // 여기서부터는 검증이 모두 끝난 상태. 사용자가 바뀌었으므로 비회원 장바구니는 버린다.
        // (비회원 장바구니는 다음 로그인까지 유지하지 않기로 했다.)
        CartService cartService = new CartService();
        cartService.ResetCart();

        // 회원이면 그 회원 소유로 저장된 기존 장바구니를 불러온다.
        // (cart.customer_id로 남아 있으므로, 로그아웃 후 다시 로그인해도 담아 둔 게 유지된다.)
        if (role == UserRole.CUSTOMER) {
            cartService.LoadCustomerCart(customerId);
        }

        LoginSession.Login(userId, normalizedEmail, role, customerId);

        return role;
    }

    /**
     * 로그아웃을 처리합니다. 세션을 비우고 장바구니 번호를 잊습니다.
     * 로그인하지 않은 상태에서 불러도 아무 일도 일어나지 않습니다.
     *
     * 회원 장바구니는 DB에서 지우지 않습니다(ReleaseCart) — cart.customer_id로
     * 그 회원 소유임이 남아 있어야, 다음에 로그인했을 때 다시 불러올 수 있습니다.
     */
    public void Logout() {

        new CartService().ReleaseCart();

        LoginSession.Logout();
    }


    /**
     * MyBatis SqlSession을 여는 헬퍼 메서드입니다. 초기화에 실패했으면 상태 예외를 던집니다.
     */
    private SqlSession OpenSession() {

        SqlSession session = DbConnectionFactory.OpenSession();

        if (session == null) {
            throw new IllegalStateException("DB 연결에 실패했습니다.");
        }

        return session;
    }
}
