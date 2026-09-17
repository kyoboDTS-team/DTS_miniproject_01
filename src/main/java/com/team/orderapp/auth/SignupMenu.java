package com.team.orderapp.auth;

import java.util.Scanner;

/**
 * 신규 회원가입 정보(이메일, 비밀번호, 고객 프로필 정보)를 입력받는 콘솔 화면입니다.
 */
public class SignupMenu {

    private static final String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S{8,20}$";
    private static final String CANCEL_INPUT = "0";

    // common/ConsoleInput이 아직 구현되지 않아 임시로 직접 사용. 완성되면 교체 필요.
    private final Scanner scanner;

    public SignupMenu() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * 입력값이 취소 신호("0")인지 확인하고, 맞으면 취소 안내를 출력합니다.
     */
    private boolean IsCancelled(String input) {
        if (CANCEL_INPUT.equals(input)) {
            System.out.println("취소했습니다.");
            return true;
        }
        return false;
    }

    /**
     * 회원가입 정보를 입력받아 형식을 검증합니다.
     * 계정+고객 저장(AuthService 호출)은 AppUserDao/PasswordHasher/CustomerDao를 한 트랜잭션으로
     * 묶어야 하는데, AuthService에 해당 메서드가 아직 없어 이 메서드에서는 검증까지만 수행합니다.
     */
    public void SignUp() {
        System.out.println("\n=== 회원가입 (입력 중 언제든 0을 입력하면 취소) ===");

        System.out.print("이메일(로그인 ID): ");
        String email = scanner.nextLine().trim();
        if (IsCancelled(email)) {
            return;
        }
        if (!email.matches(EMAIL_PATTERN)) {
            System.out.println("이메일 형식이 올바르지 않습니다.");
            return;
        }

        System.out.print("비밀번호 (8~20자, 대문자/소문자/숫자/특수문자 각 1개 이상, 공백 불가): ");
        String password = scanner.nextLine();
        if (IsCancelled(password.trim())) {
            return;
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            System.out.println("비밀번호 형식이 올바르지 않습니다.");
            return;
        }

        System.out.print("비밀번호 확인: ");
        String passwordConfirm = scanner.nextLine();
        if (IsCancelled(passwordConfirm.trim())) {
            return;
        }
        if (!password.equals(passwordConfirm)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return;
        }

        System.out.print("이름: ");
        String customerName = scanner.nextLine().trim();
        if (IsCancelled(customerName)) {
            return;
        }
        if (customerName.isEmpty()) {
            System.out.println("이름은 필수 입력 항목입니다.");
            return;
        }

        System.out.print("전화번호: ");
        String phone = scanner.nextLine().trim();
        if (IsCancelled(phone)) {
            return;
        }
        if (phone.isEmpty()) {
            System.out.println("전화번호는 필수 입력 항목입니다.");
            return;
        }

        // TODO(상진님): AuthService에 회원가입 저장 메서드가 준비되면 여기서 호출.
        // 예상 형태: AuthService.SignUp(email, password, customerName, phone)
        // -> 내부에서 PasswordHasher.HashPassword, AppUserDao.Insert, CustomerDao.Insert를
        //    한 트랜잭션(같은 SqlSession)으로 묶어서 저장해야 함 (계정만 남고 고객 저장 실패하면 안 됨).

        System.out.println("\n=== 가입 정보 확인 ===");
        System.out.println("이메일   : " + email);
        System.out.println("이름     : " + customerName);
        System.out.println("전화번호 : " + phone);
        System.out.println("(계정 생성 기능은 AuthService 준비가 끝나면 이어서 연결됩니다.)");

        System.out.print("\n0을 입력하면 종료: ");
        while (!CANCEL_INPUT.equals(scanner.nextLine().trim())) {
            System.out.print("0을 입력하면 종료: ");
        }
    }
}
