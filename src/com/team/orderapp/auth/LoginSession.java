package com.team.orderapp.auth;

/**
 * 현재 애플리케이션의 로그인 세션 상태를 유지하고 관리하는 클래스입니다.
 */
public class LoginSession {

    private static AppUser currentUser = null;

    /**
     * 현재 로그인 사용자를 설정합니다.
     *
     * @param InUser 로그인한 사용자 객체
     */
    public static void SetCurrentUser(AppUser InUser) {
        currentUser = InUser;
    }

    /**
     * 현재 로그인된 사용자 객체를 반환합니다.
     *
     * @return 로그인된 AppUser 객체 (미로그인 시 null)
     */
    public static AppUser GetCurrentUser() {
        return currentUser;
    }

    /**
     * 현재 로그인 상태인지 여부를 반환합니다.
     *
     * @return 로그인 여부
     */
    public static boolean IsLoggedIn() {
        return currentUser != null;
    }

    /**
     * 현재 로그인 세션을 초기화(로그아웃)합니다.
     */
    public static void ClearSession() {
        currentUser = null;
    }

    /**
     * 현재 사용자가 특정 권한을 보유하고 있는지 확인하는 헬퍼 메서드입니다.
     *
     * @param InRole 확인할 권한
     * @return 권한 일치 여부
     */
    public static boolean HasRole(UserRole InRole) {
        if (!IsLoggedIn()) {
            return false;
        }
        return currentUser.GetRole() == InRole;
    }
}
