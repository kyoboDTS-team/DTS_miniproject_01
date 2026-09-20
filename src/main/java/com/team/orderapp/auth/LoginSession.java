package com.team.orderapp.auth;

import lombok.Getter;

/**
 * 로그인한 사용자의 세션 상태를 프로그램 실행 중에 보관하는 클래스입니다.
 *
 * 값 검증(비밀번호 확인, 계정 활성 여부 등)은 LoginService가 하고,
 * 이 클래스는 검증이 끝난 값을 저장했다가 꺼내 주기만 합니다.
 *
 * 필드가 모두 static이라 프로그램 전체에서 하나의 로그인 상태를 공유합니다
 * (CartService.currentCartId와 같은 방식).
 */
public class LoginSession {

    /*
     * 작업: 로그인 사용자의 세션 상태(로그인 여부, 사용자 정보, 권한) 관리
     *
     * 작업자: 김상진
     */

    @Getter private static Long userId;
    @Getter private static String email;
    @Getter private static UserRole role;
    @Getter private static Long customerId;   // 관리자는 null

    /**
     * 객체를 만들 필요가 없는 클래스라 생성자를 막아 둡니다.
     */
    private LoginSession() {
    }

    /**
     * 로그인에 성공한 사용자 정보를 세션에 기록합니다.
     * 네 값을 한 번에 넣어, 일부만 채워진 상태가 생기지 않게 합니다.
     *
     * @param userId     app_user.user_id
     * @param email      로그인에 사용한 이메일(소문자)
     * @param role       권한
     * @param customerId customer.customer_id. 관리자는 null
     */
    public static void Login(Long userId, String email, UserRole role, Long customerId) {

        LoginSession.userId = userId;
        LoginSession.email = email;
        LoginSession.role = role;
        LoginSession.customerId = customerId;
    }

    /**
     * 세션을 비웁니다. 네 값을 모두 비워야 다음 사용자에게 이전 값이 남지 않습니다.
     */
    public static void Logout() {

        userId = null;
        email = null;
        role = null;
        customerId = null;
    }

    /**
     * 로그인 상태인지 확인합니다. 관리자는 customerId가 없으므로 userId로 판단합니다.
     *
     * @return 로그인 상태면 true
     */
    public static boolean IsLoggedIn() {

        return userId != null;
    }

    /**
     * 관리자로 로그인했는지 확인합니다. 로그아웃 상태면 false입니다.
     *
     * @return 관리자면 true
     */
    public static boolean IsAdmin() {

        return role == UserRole.ADMIN;
    }

    /**
     * 회원으로 로그인했는지 확인합니다. 로그아웃 상태면 false입니다.
     *
     * @return 회원이면 true
     */
    public static boolean IsCustomer() {

        return role == UserRole.CUSTOMER;
    }
}
