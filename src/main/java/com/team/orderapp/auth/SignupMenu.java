package com.team.orderapp.auth;

import com.team.orderapp.customer.CustomerService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 신규 회원가입 정보(이메일, 비밀번호, 고객 프로필 정보)를 입력받는 콘솔 화면입니다.
 */
public class SignupMenu {

    private static final String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_PATTERN = "^01[016789]-?\\d{3,4}-?\\d{4}$";
    private static final String CANCEL_INPUT = "0";
    // app_user.email varchar(254), customer.customer_name varchar(50) — 실제 DB 컬럼 길이에 맞춤 (2026-09-18 DBeaver로 확인)
    private static final int EMAIL_MAX_LENGTH = 254;
    private static final int NAME_MAX_LENGTH = 50;

    // common/ConsoleInput이 아직 구현되지 않아 임시로 직접 사용. 완성되면 교체 필요.
    private final Scanner scanner;

    public SignupMenu() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * GuestMenu 등 상위 화면에서 이미 만들어 쓰고 있는 Scanner를 그대로 물려받아 씁니다.
     */
    public SignupMenu(Scanner scanner) {
        this.scanner = scanner;
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
     * 회원가입 정보를 입력받아 형식을 검증한 뒤, AuthService.SignUp()으로 계정+고객 정보를 저장합니다.
     */
    public void SignUp() {
        System.out.println("\n=== 회원가입 (입력 중 언제든 0을 입력하면 취소) ===");

        String email;
        while (true) {
            System.out.print("이메일(로그인 ID): ");
            email = scanner.nextLine().trim().toLowerCase();
            if (IsCancelled(email)) {
                return;
            }
            if (!email.matches(EMAIL_PATTERN)) {
                System.out.println("이메일 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }
            if (email.length() > EMAIL_MAX_LENGTH) {
                System.out.println("이메일이 너무 깁니다. (" + EMAIL_MAX_LENGTH + "자 이하)");
                continue;
            }

            boolean emailTaken;
            try {
                emailTaken = new AuthService().IsEmailTaken(email);
            } catch (IllegalStateException e) {
                System.out.println("DB 연결에 실패했습니다. 잠시 후 다시 시도해주세요.");
                return;
            } catch (Exception e) {
                System.out.println("통신 환경이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                return;
            }
            if (emailTaken) {
                System.out.println("이미 가입된 이메일입니다. 다시 입력해주세요.");
                continue;
            }

            System.out.print("입력하신 이메일이 \"" + email + "\" 맞습니까? (y: 확인, 0: 취소, 그 외: 다시 입력): ");
            String confirm = scanner.nextLine().trim();
            if (IsCancelled(confirm)) {
                return;
            }
            if ("y".equalsIgnoreCase(confirm)) {
                break;
            }
        }

        String password;
        String passwordConfirm;
        passwordStep:
        while (true) {
            while (true) {
                System.out.print("비밀번호 (8~20자, 대문자/소문자/숫자/특수문자 각 1개 이상, 공백 불가): ");
                password = scanner.nextLine();
                if (IsCancelled(password.trim())) {
                    return;
                }

                List<String> problems = ValidatePassword(password);
                if (problems.isEmpty()) {
                    break;
                }
                for (String problem : problems) {
                    System.out.println(problem);
                }
            }

            while (true) {
                System.out.print("비밀번호 확인 (0: 취소, b: 비밀번호 다시 입력): ");
                passwordConfirm = scanner.nextLine();
                if (IsCancelled(passwordConfirm.trim())) {
                    return;
                }
                if ("b".equalsIgnoreCase(passwordConfirm.trim())) {
                    continue passwordStep;
                }
                if (password.equals(passwordConfirm)) {
                    break passwordStep;
                }
                System.out.println("비밀번호가 일치하지 않습니다. 다시 입력해주세요. (b: 비밀번호부터 다시 입력)");
            }
        }

        String customerName;
        while (true) {
            System.out.print("이름: ");
            customerName = scanner.nextLine().trim();
            if (IsCancelled(customerName)) {
                return;
            }
            if (customerName.isEmpty()) {
                System.out.println("이름은 필수 입력 항목입니다. 다시 입력해주세요.");
                continue;
            }
            if (customerName.length() > NAME_MAX_LENGTH) {
                System.out.println("이름이 너무 깁니다. (" + NAME_MAX_LENGTH + "자 이하)");
                continue;
            }
            break;
        }

        String phone;
        while (true) {
            System.out.print("전화번호 (예: 01012345678): ");
            phone = scanner.nextLine().trim();
            if (IsCancelled(phone)) {
                return;
            }
            if (!phone.matches(PHONE_PATTERN)) {
                System.out.println("전화번호 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }

            boolean phoneTaken;
            try {
                phoneTaken = new CustomerService().IsPhoneTaken(phone);
            } catch (IllegalStateException e) {
                System.out.println("DB 연결에 실패했습니다. 잠시 후 다시 시도해주세요.");
                return;
            } catch (Exception e) {
                System.out.println("통신 환경이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                return;
            }
            if (phoneTaken) {
                System.out.println("이미 가입된 전화번호입니다. 다시 입력해주세요.");
                continue;
            }

            break;
        }

        boolean signedUp;
        try {
            signedUp = new AuthService().SignUp(email, password, customerName, phone);
        } catch (IllegalStateException e) {
            System.out.println("DB 연결에 실패했습니다. 잠시 후 다시 시도해주세요.");
            return;
        } catch (Exception e) {
            if (IsDuplicateEmail(e)) {
                System.out.println("이미 가입된 이메일입니다.");
            } else {
                System.out.println("통신 환경이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
            }
            return;
        }

        if (!signedUp) {
            System.out.println("회원가입에 실패했습니다.");
            return;
        }

        System.out.println("\n=== 회원가입 완료 ===");
        System.out.println("환영합니다 " + customerName + "님!");
        System.out.println();
        System.out.println("이메일   : " + email);
        System.out.println("이름     : " + customerName);
        System.out.println("전화번호 : " + phone);
        System.out.println("====================");

        System.out.print("\n0을 입력하면 뒤로 돌아가기: ");
        while (!CANCEL_INPUT.equals(scanner.nextLine().trim())) {
            System.out.print("0을 입력하면 뒤로 돌아가기: ");
        }
    }

    /**
     * 비밀번호 규칙(8~20자, 대문자/소문자/숫자/특수문자 각 1개 이상, 공백 불가)을 하나씩 확인해서,
     * 빠진 조건마다 각각 다른 안내 문구를 돌려줍니다. 전부 통과하면 빈 목록을 돌려줍니다.
     */
    private List<String> ValidatePassword(String password) {
        List<String> problems = new ArrayList<>();

        if (password.length() < 8) {
            problems.add("비밀번호가 너무 짧습니다. (8자 이상)");
        }
        if (password.length() > 20) {
            problems.add("비밀번호가 너무 깁니다. (20자 이하)");
        }
        if (password.matches(".*\\s.*")) {
            problems.add("비밀번호에 공백을 포함할 수 없습니다.");
        }
        if (!password.matches(".*[A-Z].*")) {
            problems.add("대문자가 없습니다.");
        }
        if (!password.matches(".*[a-z].*")) {
            problems.add("소문자가 없습니다.");
        }
        if (!password.matches(".*\\d.*")) {
            problems.add("숫자가 없습니다.");
        }
        if (!password.matches(".*[^A-Za-z0-9\\s].*")) {
            problems.add("특수문자가 없습니다.");
        }

        return problems;
    }

    /**
     * 예외의 원인을 타고 올라가며 이메일 유니크 제약 위반(PostgreSQL SQL 상태 23505)인지 확인합니다.
     */
    private boolean IsDuplicateEmail(Throwable error) {
        Throwable cause = error;
        while (cause != null) {
            if (cause instanceof SQLException sqlException) {
                return "23505".equals(sqlException.getSQLState());
            }
            cause = cause.getCause();
        }
        return false;
    }
}
