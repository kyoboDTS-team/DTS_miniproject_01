package com.team.orderapp.auth;

import com.team.orderapp.common.BusinessException;
import com.team.orderapp.common.ConsoleInput;

/**
 * 로그인, 회원가입, 세션 상태 관리 콘솔 화면을 담당하는 클래스입니다.
 */
public class LoginMenu {

    private final AuthService authService;

    public LoginMenu() {
        this.authService = new AuthService();
    }

    public LoginMenu(AuthService InAuthService) {
        this.authService = InAuthService;
    }

    /**
     * 인증 관련 서브 메뉴를 표시하고 사용자 입력을 처리합니다.
     */
    public void DisplayMenu() {
        boolean inMenu = true;
        while (inMenu) {
            PrintAuthMenuOptions();
            int choice = ConsoleInput.ReadInt("메뉴 번호를 선택하세요: ");
            inMenu = RouteAuthChoice(choice);
        }
    }

    /**
     * 로그인 요청을 처리합니다.
     */
    public void ProcessLogin() {
        System.out.println("\n--- 로그인 ---");
        String username = ConsoleInput.ReadString("아이디: ");
        String password = ConsoleInput.ReadString("비밀번호: ");

        try {
            AppUser user = authService.Login(username, password);
            System.out.println("로그인 성공! 환영합니다, " + user.GetUsername() + "님 (" + user.GetRole() + ")");
        } catch (BusinessException InException) {
            System.out.println("[로그인 실패] " + InException.getMessage());
        }
    }

    /**
     * 로그아웃 요청을 처리합니다.
     */
    public void ProcessLogout() {
        if (!LoginSession.IsLoggedIn()) {
            System.out.println("현재 로그인 상태가 아닙니다.");
            return;
        }
        String username = LoginSession.GetCurrentUser().GetUsername();
        LoginSession.ClearSession();
        System.out.println(username + "님이 성공적으로 로그아웃되었습니다.");
    }

    /**
     * 신규 사용자 등록 요청을 처리합니다.
     */
    public void ProcessSignUp() {
        System.out.println("\n--- 회원가입 ---");
        String username = ConsoleInput.ReadString("아이디: ");
        String password = ConsoleInput.ReadString("비밀번호: ");
        String roleStr = ConsoleInput.ReadString("권한 (ADMIN / STAFF / CUSTOMER) [기본 CUSTOMER]: ");

        UserRole role = UserRole.FromString(roleStr);
        AppUser newUser = new AppUser(null, username, null, role);

        try {
            authService.RegisterUser(newUser, password);
            System.out.println("회원가입이 완료되었습니다. 로그인 후 이용해 주세요.");
        } catch (BusinessException InException) {
            System.out.println("[회원가입 실패] " + InException.getMessage());
        }
    }

    /**
     * 인증 메뉴 옵션 목록을 출력하는 헬퍼 메서드입니다.
     */
    private void PrintAuthMenuOptions() {
        System.out.println("\n[로그인 / 계정 관리]");
        PrintSessionStatus();
        System.out.println("1. 로그인");
        System.out.println("2. 로그아웃");
        System.out.println("3. 회원가입");
        System.out.println("0. 이전 메뉴로 돌아가기");
    }

    /**
     * 현재 로그인 세션 상태를 출력하는 헬퍼 메서드입니다.
     */
    private void PrintSessionStatus() {
        if (LoginSession.IsLoggedIn()) {
            System.out.println("상태: " + LoginSession.GetCurrentUser().GetUsername() + " 로그인 중 (" + LoginSession.GetCurrentUser().GetRole() + ")");
        } else {
            System.out.println("상태: 미로그인");
        }
    }

    /**
     * 인증 메뉴 번호 분기를 처리하는 헬퍼 메서드입니다.
     *
     * @param InChoice 선택 번호
     * @return 서브메뉴 지속 여부
     */
    private boolean RouteAuthChoice(int InChoice) {
        switch (InChoice) {
            case 1:
                ProcessLogin();
                return true;
            case 2:
                ProcessLogout();
                return true;
            case 3:
                ProcessSignUp();
                return true;
            case 0:
                return false;
            default:
                System.out.println("잘못된 번호입니다. 다시 선택해 주세요.");
                return true;
        }
    }
}
