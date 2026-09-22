package com.team.orderapp.auth;

import com.team.orderapp.common.ConsoleUi;
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

    // 연속 입력 화면에서 필드 이름 칸의 너비 (계획서 7장)
    private static final int PROMPT_WIDTH = 16;
    // app_user.email varchar(254), customer.customer_name varchar(50) — 실제 DB 컬럼 길이에 맞춤 (2026-09-18 DBeaver로 확인)
    private static final int EMAIL_MAX_LENGTH = 254;
    private static final int NAME_MAX_LENGTH = 50;

    // common/ConsoleInput이 아직 구현되지 않아 임시로 직접 사용. 완성되면 교체 필요.
    private final Scanner scanner;

    /**
     * GuestMenu 등 상위 화면에서 이미 만들어 쓰고 있는 Scanner를 그대로 물려받아 씁니다.
     * 앱 전체에서 Scanner(System.in)를 하나만 만들어 공유하는 규칙을 따르기 위함입니다.
     */
    public SignupMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 입력값이 취소 신호("0")인지 확인하고, 맞으면 취소 안내를 출력합니다.
     */
    private boolean IsCancelled(String input) {
        if (CANCEL_INPUT.equals(input)) {
            ConsoleUi.Cancelled();
            return true;
        }
        return false;
    }

    /**
     * 회원가입 정보를 입력받아 형식을 검증한 뒤, AuthService.SignUp()으로 계정+고객 정보를 저장합니다.
     */
    public void SignUp() {
        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("SIGN UP", "회원가입");

        System.out.println();
        ConsoleUi.Info("입력 중 언제든 0을 입력하면 취소합니다.");
        System.out.println();

        String email;
        while (true) {
            ConsoleUi.Prompt("이메일", PROMPT_WIDTH);
            email = scanner.nextLine().trim().toLowerCase();
            if (IsCancelled(email)) {
                return;
            }
            if (!email.matches(EMAIL_PATTERN)) {
                ConsoleUi.Error("이메일 형식이 올바르지 않습니다. 다시 입력해 주세요.");
                continue;
            }
            if (email.length() > EMAIL_MAX_LENGTH) {
                ConsoleUi.Error("이메일이 너무 깁니다. (" + EMAIL_MAX_LENGTH + "자 이하)");
                continue;
            }

            boolean emailTaken;
            try {
                emailTaken = new AuthService().IsEmailTaken(email);
            } catch (IllegalStateException e) {
                ConsoleUi.Error("DB 연결에 실패했습니다. 잠시 후 다시 시도해 주세요.");
                return;
            } catch (Exception e) {
                ConsoleUi.Error("통신 환경이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
                return;
            }
            if (emailTaken) {
                ConsoleUi.Error("이미 사용 중인 이메일입니다. 다시 입력해 주세요.");
                continue;
            }

            ConsoleUi.Success("사용 가능한 이메일입니다.");
            ConsoleUi.Prompt("\"" + email + "\" 맞습니까? (y: 확인, 0: 취소, 그 외: 다시 입력)");
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
                ConsoleUi.Warn("비밀번호는 8~20자이며 대문자/소문자/숫자/특수문자를 각각 1개 이상 포함해야 합니다.");
                ConsoleUi.Prompt("비밀번호", PROMPT_WIDTH);
                password = scanner.nextLine();
                if (IsCancelled(password.trim())) {
                    return;
                }

                List<String> problems = ValidatePassword(password);
                if (problems.isEmpty()) {
                    break;
                }
                for (String problem : problems) {
                    ConsoleUi.Error(problem);
                }
            }

            while (true) {
                ConsoleUi.Prompt("비밀번호 확인", PROMPT_WIDTH);
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
                ConsoleUi.Error("비밀번호가 일치하지 않습니다. 다시 입력해 주세요. (b: 비밀번호부터 다시 입력)");
            }
        }

        String customerName;
        while (true) {
            ConsoleUi.Prompt("이름", PROMPT_WIDTH);
            customerName = scanner.nextLine().trim();
            if (IsCancelled(customerName)) {
                return;
            }
            if (customerName.isEmpty()) {
                ConsoleUi.Error("이름은 필수 입력 항목입니다. 다시 입력해 주세요.");
                continue;
            }
            if (customerName.length() > NAME_MAX_LENGTH) {
                ConsoleUi.Error("이름이 너무 깁니다. (" + NAME_MAX_LENGTH + "자 이하)");
                continue;
            }
            break;
        }

        String phone;
        while (true) {
            ConsoleUi.Prompt("연락처", PROMPT_WIDTH);
            phone = scanner.nextLine().trim();
            if (IsCancelled(phone)) {
                return;
            }
            if (!phone.matches(PHONE_PATTERN)) {
                ConsoleUi.Error("전화번호 형식이 올바르지 않습니다. 다시 입력해 주세요.");
                continue;
            }

            boolean phoneTaken;
            try {
                phoneTaken = new CustomerService().IsPhoneTaken(phone);
            } catch (IllegalStateException e) {
                ConsoleUi.Error("DB 연결에 실패했습니다. 잠시 후 다시 시도해 주세요.");
                return;
            } catch (Exception e) {
                ConsoleUi.Error("통신 환경이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
                return;
            }
            if (phoneTaken) {
                ConsoleUi.Error("이미 사용 중인 전화번호입니다. 다시 입력해 주세요.");
                continue;
            }

            break;
        }

        System.out.println();
        ConsoleUi.Divider();
        ConsoleUi.Field("이메일", email, 10);
        ConsoleUi.Field("이름", customerName, 10);
        ConsoleUi.Field("연락처", phone, 10);
        ConsoleUi.Divider();
        System.out.println();
        ConsoleUi.YesNoOptions("가입", "취소");
        System.out.println();
        ConsoleUi.Prompt("입력한 정보로 가입하시겠습니까? (Y/N)");
        String finalConfirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(finalConfirm)) {
            ConsoleUi.Cancelled();
            return;
        }

        boolean signedUp;
        try {
            signedUp = new AuthService().SignUp(email, password, customerName, phone);
        } catch (IllegalStateException e) {
            ConsoleUi.Error("DB 연결에 실패했습니다. 잠시 후 다시 시도해 주세요.");
            return;
        } catch (Exception e) {
            if (IsDuplicateEmail(e)) {
                ConsoleUi.Error("이미 사용 중인 이메일입니다.");
            } else {
                ConsoleUi.Error("통신 환경이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
            }
            return;
        }

        if (!signedUp) {
            ConsoleUi.Error("회원가입에 실패했습니다.");
            return;
        }

        ConsoleUi.ClearScreen();
        ConsoleUi.CompleteBox(
                "SIGN UP COMPLETE",
                "환영합니다 " + customerName + "님!",
                ConsoleUi.InfoLine("이메일", email),
                ConsoleUi.InfoLine("연락처", phone)
        );

        System.out.println();
        ConsoleUi.Success("회원가입이 완료되었습니다.");

        boolean loggedIn = AutoLogin(email, password);

        String backPrompt = loggedIn ? "0을 입력하면 회원 메뉴로 이동" : "0을 입력하면 뒤로 돌아가기";
        System.out.println();
        ConsoleUi.Prompt(backPrompt);
        while (!CANCEL_INPUT.equals(scanner.nextLine().trim())) {
            ConsoleUi.Prompt(backPrompt);
        }
    }

    /**
     * 가입한 계정으로 바로 로그인합니다. 로그인에 실패해도 가입은 이미 끝났으므로 안내만 하고 넘어갑니다.
     *
     * @return 로그인에 성공했으면 true
     */
    private boolean AutoLogin(String email, String password) {
        try {
            new LoginService().Login(email, password);
            ConsoleUi.Success("자동으로 로그인되었습니다.");
            return true;
        } catch (Exception e) {
            ConsoleUi.Warn("가입은 완료되었습니다. 로그인 메뉴에서 로그인해 주세요.");
            return false;
        }
    }

    /**
     * 비밀번호 규칙(8~20자, 대문자/소문자/숫자/특수문자 각 1개 이상, 공백 불가)을 하나씩 확인해서,
     * 빠진 조건마다 각각 다른 안내 문구를 돌려줍니다. 전부 통과하면 빈 목록을 돌려줍니다.
     */
    private List<String> ValidatePassword(String password) {
        List<String> problems = new ArrayList<>();

        if (!password.matches("[\\x21-\\x7E]*")) {
            problems.add("한글이나 이모지 등은 사용할 수 없습니다. 영문/숫자/특수문자(ASCII)만 입력해주세요.");
        }
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
