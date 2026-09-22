package com.team.orderapp.customer;

import com.team.orderapp.common.ConsoleUi;
import com.team.orderapp.order.query.OrderSummaryView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 로그인한 회원 본인의 정보를 조회/수정하는 화면입니다. 관리자용 CustomerMenu와 달리
 * 본인 정보만 다루고, 다른 회원 검색이나 삭제 기능은 없습니다.
 */
public class MyInfoMenu {

    private static final String CANCEL_INPUT = "0";
    private static final String DB_ERROR_MESSAGE = "DB 연결에 실패했습니다. 잠시 후 다시 시도해주세요.";
    private static final String COMMUNICATION_ERROR_MESSAGE = "통신 환경이 원활하지 않습니다. 잠시 후 다시 시도해주세요.";
    private static final String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_PATTERN = "^01[016789]-?\\d{3,4}-?\\d{4}$";
    private static final int EMAIL_MAX_LENGTH = 254;
    private static final int NAME_MAX_LENGTH = 50;

    // 연속 입력 화면에서 필드 이름 칸의 너비 (계획서 7장)
    private static final int PROMPT_WIDTH = 18;

    private final Scanner scanner;
    private String email;
    private final CustomerService customerService;

    /**
     * MemberMenu 등 상위 화면에서 이미 로그인한 회원의 이메일과, 공유 중인 Scanner를 그대로 받습니다.
     */
    public MyInfoMenu(Scanner scanner, String email) {
        this.scanner = scanner;
        this.email = email.toLowerCase();
        this.customerService = new CustomerService();
    }

    /**
     * 현재(최신) 이메일을 돌려줍니다. Run() 도중 이메일을 변경했다면 그 새 값이에요.
     * MemberMenu가 자기 화면에 표시할 이메일을 갱신할 때 사용합니다.
     */
    public String GetEmail() {
        return email;
    }

    /**
     * 내 정보를 조회하고, 메뉴에서 고른 항목을 수정합니다.
     *
     * @return 계속 로그인 상태를 유지해도 되면 true, 회원 탈퇴로 계정이 사라져서
     * 상위 화면(MemberMenu)이 로그아웃 처리를 해야 하면 false
     */
    public boolean Run() {
        Optional<Customer> found;
        try {
            found = customerService.FindByEmail(email);
        } catch (IllegalStateException e) {
            ConsoleUi.Error(DB_ERROR_MESSAGE);
            return true;
        } catch (Exception e) {
            ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
            return true;
        }

        if (found.isEmpty()) {
            ConsoleUi.Error("내 정보를 찾을 수 없습니다.");
            return true;
        }

        Customer customer = found.get();

        while (true) {
            ConsoleUi.ClearScreen();
            ConsoleUi.ScreenHeader("MY INFO", "내 정보 조회 / 수정");

            System.out.println();
            ConsoleUi.Field("이름", customer.getCustomerName(), 10);
            ConsoleUi.Field("이메일", customer.getEmail(), 10);
            ConsoleUi.Field("연락처", customer.getPhone(), 10);

            ConsoleUi.Section("내 정보 수정");
            ConsoleUi.MenuItem("01", "이름 변경");
            ConsoleUi.MenuItem("02", "연락처 변경");
            ConsoleUi.MenuItem("03", "이메일(ID) 변경");
            ConsoleUi.MenuItem("04", "비밀번호 변경");

            ConsoleUi.Section("계정");
            ConsoleUi.MenuItem("05", "회원 탈퇴");
            ConsoleUi.MenuItem("00", "뒤로가기");

            System.out.println();
            ConsoleUi.Prompt("선택");

            String choice = ConsoleUi.Choice(scanner.nextLine());
            switch (choice) {
                case "1" -> UpdateName(customer);
                case "2" -> UpdatePhone(customer);
                case "3" -> UpdateEmail(customer);
                case "4" -> UpdatePassword(customer);
                case "5" -> {
                    if (Withdraw(customer)) {
                        return false;
                    }
                }
                case "0" -> {
                    return true;
                }
                default -> PrintInvalidChoiceMessage(choice);
            }
        }
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
     * 메뉴 선택이 잘못됐을 때, 숫자가 아니면 "숫자를 입력해 주세요.", 숫자인데 없는 번호면
     * "올바른 메뉴 번호를 입력해 주세요."로 구분해서 안내합니다.
     */
    private void PrintInvalidChoiceMessage(String choice) {
        if (choice.matches("\\d+")) {
            ConsoleUi.InvalidMenu();
        } else {
            ConsoleUi.Error("숫자를 입력해 주세요.");
        }
        ConsoleUi.PressEnter(scanner);
    }

    /**
     * 결과를 확인할 시간을 주기 위해, "0"을 입력할 때까지 화면에 머무릅니다.
     */
    private void WaitForBack() {
        System.out.println();
        ConsoleUi.Prompt("0을 입력하면 뒤로가기");
        while (!CANCEL_INPUT.equals(scanner.nextLine().trim())) {
            ConsoleUi.Prompt("0을 입력하면 뒤로가기");
        }
    }

    /**
     * 내 이름만 수정합니다.
     */
    private void UpdateName(Customer customer) {
        while (true) {
            ConsoleUi.Info("현재 이름: " + customer.getCustomerName());
            ConsoleUi.Prompt("새 이름 (0: 취소)", PROMPT_WIDTH);
            String newName = scanner.nextLine().trim();
            if (IsCancelled(newName)) {
                return;
            }
            if (newName.isEmpty()) {
                ConsoleUi.Error("이름을 입력해주세요.");
                continue;
            }
            if (newName.length() > NAME_MAX_LENGTH) {
                ConsoleUi.Error("이름이 너무 깁니다. (" + NAME_MAX_LENGTH + "자 이하)");
                continue;
            }
            if (newName.equals(customer.getCustomerName())) {
                ConsoleUi.Error("동일한 이름은 사용할 수 없습니다. 다시 입력해주세요.");
                continue;
            }
            customer.setCustomerName(newName);
            break;
        }

        try {
            boolean updated = customerService.Update(customer);
            if (updated) {
                ConsoleUi.Success("이름이 \"" + customer.getCustomerName() + "\"(으)로 변경되었습니다.");
            } else {
                ConsoleUi.Error("이름 변경에 실패했습니다.");
            }
        } catch (IllegalStateException e) {
            ConsoleUi.Error(DB_ERROR_MESSAGE);
        } catch (Exception e) {
            ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
        }
        WaitForBack();
    }

    /**
     * 내 전화번호만 수정합니다.
     */
    private void UpdatePhone(Customer customer) {
        while (true) {
            ConsoleUi.Info("현재 연락처: " + customer.getPhone());
            ConsoleUi.Prompt("새 연락처 (0: 취소)", PROMPT_WIDTH);
            String newPhone = scanner.nextLine().trim();
            if (IsCancelled(newPhone)) {
                return;
            }
            if (!newPhone.matches(PHONE_PATTERN)) {
                ConsoleUi.Error("전화번호 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }

            String normalizedPhone = newPhone.replace("-", "");
            String currentNormalizedPhone = customer.getPhone().replace("-", "");
            if (normalizedPhone.equals(currentNormalizedPhone)) {
                ConsoleUi.Error("동일한 번호는 사용할 수 없습니다. 다시 입력해주세요.");
                continue;
            }
            try {
                if (customerService.IsPhoneTaken(normalizedPhone, customer.getCustomerId())) {
                    ConsoleUi.Error("이미 사용 중인 전화번호입니다. 다시 입력해주세요.");
                    continue;
                }
            } catch (IllegalStateException e) {
                ConsoleUi.Error(DB_ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
                return;
            }
            customer.setPhone(normalizedPhone);
            break;
        }

        try {
            boolean updated = customerService.Update(customer);
            if (updated) {
                ConsoleUi.Success("연락처가 \"" + customer.getPhone() + "\"(으)로 변경되었습니다.");
            } else {
                ConsoleUi.Error("연락처 변경에 실패했습니다.");
            }
        } catch (IllegalStateException e) {
            ConsoleUi.Error(DB_ERROR_MESSAGE);
        } catch (Exception e) {
            ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
        }
        WaitForBack();
    }

    /**
     * 내 로그인 이메일(ID)만 수정합니다. app_user 테이블의 email 컬럼을 바꾸는 것이라
     * customer가 아니라 auth 쪽 데이터를 수정합니다.
     */
    private void UpdateEmail(Customer customer) {
        while (true) {
            ConsoleUi.Info("현재 이메일: " + customer.getEmail());
            ConsoleUi.Prompt("새 이메일 (0: 취소)", PROMPT_WIDTH);
            String newEmail = scanner.nextLine().trim().toLowerCase();
            if (IsCancelled(newEmail)) {
                return;
            }
            if (!newEmail.matches(EMAIL_PATTERN)) {
                ConsoleUi.Error("이메일 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }
            if (newEmail.length() > EMAIL_MAX_LENGTH) {
                ConsoleUi.Error("이메일이 너무 깁니다. (" + EMAIL_MAX_LENGTH + "자 이하) 다시 입력해주세요.");
                continue;
            }
            if (newEmail.equals(customer.getEmail().toLowerCase())) {
                ConsoleUi.Error("동일한 이메일은 사용할 수 없습니다. 다시 입력해주세요.");
                continue;
            }

            try {
                boolean updated = customerService.UpdateEmail(customer, newEmail);
                if (updated) {
                    customer.setEmail(newEmail);
                    this.email = newEmail;
                    ConsoleUi.Success("이메일(ID)이 \"" + newEmail + "\"(으)로 수정되었습니다.");
                    ConsoleUi.Warn("다음 로그인부터는 새 이메일로 로그인해 주세요.");
                } else {
                    ConsoleUi.Error("이메일 수정에 실패했습니다.");
                }
            } catch (IllegalStateException e) {
                ConsoleUi.Error(DB_ERROR_MESSAGE);
            } catch (Exception e) {
                if (IsSqlState(e, "23505")) {
                    ConsoleUi.Error("이미 사용 중인 이메일이라 변경할 수 없습니다. 다시 입력해주세요.");
                    continue;
                }
                ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
            }
            break;
        }
        WaitForBack();
    }

    /**
     * 내 비밀번호를 변경합니다. 현재 비밀번호를 먼저 확인한 뒤에만 바꿀 수 있습니다.
     */
    private void UpdatePassword(Customer customer) {
        String currentPassword;
        while (true) {
            ConsoleUi.Prompt("현재 비밀번호 (0: 취소)", PROMPT_WIDTH);
            currentPassword = scanner.nextLine();
            if (IsCancelled(currentPassword.trim())) {
                return;
            }

            boolean verified;
            try {
                verified = customerService.VerifyPassword(customer, currentPassword);
            } catch (IllegalStateException e) {
                ConsoleUi.Error(DB_ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
                return;
            }
            if (!verified) {
                ConsoleUi.Error("현재 비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
                continue;
            }
            break;
        }

        String newPassword;
        while (true) {
            ConsoleUi.Warn("비밀번호는 8~20자이며 대문자/소문자/숫자/특수문자를 각각 1개 이상 포함해야 합니다.");
            ConsoleUi.Prompt("새 비밀번호 (0: 취소)", PROMPT_WIDTH);
            newPassword = scanner.nextLine();
            if (IsCancelled(newPassword.trim())) {
                return;
            }
            List<String> problems = ValidatePassword(newPassword);
            if (!problems.isEmpty()) {
                for (String problem : problems) {
                    ConsoleUi.Error(problem);
                }
                continue;
            }
            if (newPassword.equals(currentPassword)) {
                ConsoleUi.Error("동일한 비밀번호는 사용할 수 없습니다. 다시 입력해주세요.");
                continue;
            }
            break;
        }

        String newPasswordConfirm;
        while (true) {
            ConsoleUi.Prompt("새 비밀번호 확인", PROMPT_WIDTH);
            newPasswordConfirm = scanner.nextLine();
            if (IsCancelled(newPasswordConfirm.trim())) {
                return;
            }
            if (newPassword.equals(newPasswordConfirm)) {
                break;
            }
            ConsoleUi.Error("비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
        }

        try {
            boolean updated = customerService.UpdatePassword(customer, newPassword);
            if (updated) {
                ConsoleUi.Success("비밀번호가 변경되었습니다.");
            } else {
                ConsoleUi.Error("비밀번호 변경에 실패했습니다.");
            }
        } catch (IllegalStateException e) {
            ConsoleUi.Error(DB_ERROR_MESSAGE);
        } catch (Exception e) {
            ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
        }
        WaitForBack();
    }

    /**
     * 회원 탈퇴를 처리합니다. 주문 이력이 있으면 탈퇴할 수 없고, 본인 확인을 위해 현재 비밀번호를 먼저 확인합니다.
     *
     * @return 실제로 탈퇴되었으면 true
     */
    private boolean Withdraw(Customer customer) {
        List<OrderSummaryView> orders;
        try {
            orders = customerService.FindOrderHistory(customer.getCustomerId());
        } catch (IllegalStateException e) {
            ConsoleUi.Error(DB_ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
            return false;
        }

        if (!orders.isEmpty()) {
            System.out.println();
            ConsoleUi.Error("주문 이력이 " + orders.size() + "건 있어서 탈퇴할 수 없습니다.");
            return false;
        }

        String password;
        while (true) {
            System.out.println();
            ConsoleUi.Prompt("본인 확인 - 현재 비밀번호 (0: 취소)");
            password = scanner.nextLine();
            if (IsCancelled(password.trim())) {
                return false;
            }
            if (password.isEmpty()) {
                ConsoleUi.Error("비밀번호를 입력해주세요.");
                continue;
            }
            break;
        }

        boolean verified;
        try {
            verified = customerService.VerifyPassword(customer, password);
        } catch (IllegalStateException e) {
            ConsoleUi.Error(DB_ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
            return false;
        }
        if (!verified) {
            ConsoleUi.Error("비밀번호가 일치하지 않습니다.");
            return false;
        }

        System.out.println();
        ConsoleUi.Warn("탈퇴하면 되돌릴 수 없습니다.");
        ConsoleUi.YesNoOptions("탈퇴", "취소");
        System.out.println();
        ConsoleUi.Prompt("정말 탈퇴하시겠습니까? (Y/N)");
        String confirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            ConsoleUi.Cancelled();
            return false;
        }

        try {
            boolean deleted = customerService.DeleteById(customer);
            if (!deleted) {
                ConsoleUi.Error("탈퇴에 실패했습니다.");
                return false;
            }
        } catch (IllegalStateException e) {
            ConsoleUi.Error(DB_ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            ConsoleUi.Error(COMMUNICATION_ERROR_MESSAGE);
            return false;
        }

        ConsoleUi.ClearScreen();
        ConsoleUi.CompleteBox("WITHDRAW", "회원 탈퇴 완료");

        System.out.println();
        ConsoleUi.Success("회원 탈퇴가 완료되었습니다.");
        System.out.println();
        ConsoleUi.Prompt("0을 입력하면 비회원 메뉴로 이동");
        while (!CANCEL_INPUT.equals(scanner.nextLine().trim())) {
            ConsoleUi.Prompt("0을 입력하면 비회원 메뉴로 이동");
        }
        return true;
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
     * 예외의 원인을 타고 올라가며 PostgreSQL SQL 상태 코드가 일치하는지 확인합니다.
     */
    private boolean IsSqlState(Throwable error, String sqlState) {
        Throwable cause = error;
        while (cause != null) {
            if (cause instanceof SQLException sqlException) {
                return sqlState.equals(sqlException.getSQLState());
            }
            cause = cause.getCause();
        }
        return false;
    }
}
