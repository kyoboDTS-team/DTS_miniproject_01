package com.team.orderapp.customer;

import com.team.orderapp.order.query.OrderSummaryView;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 회원 정보 조회 등 고객 관련 콘솔 화면을 담당하는 클래스입니다.
 */
public class CustomerMenu {

    private static final String CANCEL_INPUT = "0";
    private static final String MAIN_MENU_INPUT = "p";
    private static final String DB_ERROR_MESSAGE = "DB 연결에 실패했습니다. 잠시 후 다시 시도해주세요.";
    private static final String COMMUNICATION_ERROR_MESSAGE = "통신 환경이 원활하지 않습니다. 잠시 후 다시 시도해주세요.";
    private static final String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_PATTERN = "^01[016789]-?\\d{3,4}-?\\d{4}$";
    // app_user.email varchar(254), customer.customer_name varchar(50) — 실제 DB 컬럼 길이에 맞춤 (2026-09-18 DBeaver로 확인)
    private static final int EMAIL_MAX_LENGTH = 254;
    private static final int NAME_MAX_LENGTH = 50;
    private static final int PAGE_SIZE = 10;

    /**
     * 어느 화면 깊이에 있든 "p"를 입력하면 이 신호를 던져서, DisplayMenu()의 루프까지 곧장 빠져나갑니다.
     */
    private static class ReturnToMainMenu extends RuntimeException {
    }

    private final CustomerService customerService;
    // common/ConsoleInput이 아직 구현되지 않아 임시로 직접 사용. 완성되면 교체 필요.
    private final Scanner scanner;

    /**
     * AdminMenu 등 상위 화면에서 이미 만들어 쓰고 있는 Scanner를 그대로 물려받아 씁니다.
     * 앱 전체에서 Scanner(System.in)를 하나만 만들어 공유하는 규칙을 따르기 위함입니다.
     */
    public CustomerMenu(Scanner scanner) {
        this.customerService = new CustomerService();
        this.scanner = scanner;
    }

    /**
     * 관리자용 "회원 관리" 메뉴를 출력하고, 0을 선택할 때까지 반복합니다.
     * AdminMenu에서 이 메서드 하나만 호출하면 됩니다.
     */
    public void DisplayMenu() {
        while (true) {
            System.out.println("\n=== 회원 관리 ===");
            System.out.println("1. 전체 회원 조회");
            System.out.println("2. 상세 조회");
            System.out.println("3. 회원 정보 수정");
            System.out.println("4. 회원 삭제");
            System.out.println("5. 회원 비활성화");
            System.out.println("0. 뒤로가기");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> DisplayAll();
                    case "2" -> DisplayByIdMenu();
                    case "3" -> UpdateInfo();
                    case "4" -> Delete();
                    case "5" -> Deactivate();
                    case "0" -> {
                        return;
                    }
                    default -> PrintInvalidChoiceMessage(choice);
                }
            } catch (ReturnToMainMenu e) {
                // 어느 화면에서 "p"를 눌렀든, 여기로 와서 회원 관리 메뉴를 다시 보여줍니다.
            }
        }
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
     * 입력값이 "p"면 회원 관리 메뉴로 바로 돌아가기 위한 신호를 던집니다.
     */
    private void CheckMainMenuShortcut(String input) {
        if (MAIN_MENU_INPUT.equalsIgnoreCase(input)) {
            throw new ReturnToMainMenu();
        }
    }

    /**
     * 메뉴 선택이 잘못됐을 때, 숫자가 아니면 "숫자를 입력해 주세요.", 숫자인데 없는 번호면
     * "올바른 메뉴 번호를 입력해 주세요."로 구분해서 안내합니다.
     */
    private void PrintInvalidChoiceMessage(String choice) {
        if (choice.matches("\\d+")) {
            System.out.println("올바른 메뉴 번호를 입력해 주세요.");
        } else {
            System.out.println("숫자를 입력해 주세요.");
        }
    }

    /**
     * 결과를 확인할 시간을 주기 위해, "0"을 입력할 때까지 화면에 머무릅니다.
     * "p"를 입력하면 회원 관리 메뉴로 바로 돌아갑니다.
     */
    private void WaitForBack() {
        System.out.print("\n0을 입력하면 뒤로가기 (p: 회원 관리 메뉴로 이동): ");
        String input = scanner.nextLine().trim();
        while (!CANCEL_INPUT.equals(input)) {
            CheckMainMenuShortcut(input);
            System.out.print("0을 입력하면 뒤로가기 (p: 회원 관리 메뉴로 이동): ");
            input = scanner.nextLine().trim();
        }
    }

    /**
     * "전체 회원 조회" 메뉴를 출력합니다. 목록으로 보거나, 검색해서 볼 수 있습니다.
     */
    public void DisplayAll() {
        while (true) {
            System.out.println("\n=== 전체 회원 조회 ===");
            System.out.println("1. 리스트로 보기");
            System.out.println("2. 회원 검색하기");
            System.out.println("0. 뒤로가기");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> DisplayAllPaged();
                case "2" -> SearchCustomers();
                case "0" -> {
                    return;
                }
                default -> PrintInvalidChoiceMessage(choice);
            }
        }
    }

    /**
     * 전체 회원 목록을 {@value #PAGE_SIZE}명씩 페이지로 나누어 보여줍니다.
     */
    private void DisplayAllPaged() {
        long totalCount;
        try {
            totalCount = customerService.CountAll();
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return;
        }

        if (totalCount == 0) {
            System.out.println("등록된 회원이 없습니다.");
            WaitForBack();
            return;
        }

        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        int page = 1;

        while (true) {
            List<Customer> customers;
            try {
                customers = customerService.FindPage(page, PAGE_SIZE);
            } catch (IllegalStateException e) {
                System.out.println(DB_ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                System.out.println(COMMUNICATION_ERROR_MESSAGE);
                return;
            }

            System.out.println("\n=== 전체 회원 목록 (" + page + "/" + totalPages + " 페이지) ===");
            for (Customer customer : customers) {
                System.out.println(FormatListLine(customer));
            }

            System.out.print("\nz: 이전 페이지   x: 다음 페이지   회원번호 입력: 상세조회   0: 뒤로가기   p: 회원 관리 메뉴로 이동: ");
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);

            if ("x".equalsIgnoreCase(choice)) {
                if (page < totalPages) {
                    page++;
                } else {
                    System.out.println("마지막 페이지입니다.");
                }
            } else if ("z".equalsIgnoreCase(choice)) {
                if (page > 1) {
                    page--;
                } else {
                    System.out.println("첫 페이지입니다.");
                }
            } else if (IsCancelled(choice)) {
                return;
            } else {
                try {
                    DisplayById(Long.parseLong(choice));
                    return;
                } catch (NumberFormatException e) {
                    System.out.println("잘못된 입력입니다.");
                }
            }
        }
    }

    /**
     * "상세 조회" 메뉴를 출력합니다. 이름으로 바로 찾거나, 이름/전화번호로 검색할 수 있습니다.
     */
    private void DisplayByIdMenu() {
        while (true) {
            System.out.println("\n=== 상세 조회 ===");
            System.out.println("1. 회원 이름 입력");
            System.out.println("2. 회원 검색");
            System.out.println("0. 뒤로가기");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> SearchByName();
                case "2" -> SearchCustomers();
                case "0" -> {
                    return;
                }
                default -> PrintInvalidChoiceMessage(choice);
            }
        }
    }

    /**
     * 이름을 입력받아 바로 조회합니다. 일치하는 회원이 한 명이면 바로 상세 조회로 이동하고,
     * 동명이인이 여러 명이면 휴대폰 뒷자리와 함께 목록을 보여주고 고르게 합니다.
     */
    private void SearchByName() {
        System.out.print("조회할 회원 이름을 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
        String name = scanner.nextLine().trim();
        CheckMainMenuShortcut(name);
        if (IsCancelled(name)) {
            return;
        }
        if (name.isEmpty()) {
            System.out.println("이름을 입력해주세요.");
            return;
        }

        List<Customer> results;
        try {
            results = customerService.SearchByName(name);
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return;
        }

        if (results.isEmpty()) {
            System.out.println("일치하는 회원이 없습니다.");
            WaitForBack();
            return;
        }

        if (results.size() == 1) {
            DisplayById(results.get(0).getCustomerId());
            return;
        }

        Customer selected = SelectFromResults(results);
        if (selected != null) {
            DisplayById(selected.getCustomerId());
        }
    }

    /**
     * 이름 또는 이메일로 회원을 검색하여 결과를 보여줍니다. 일치하는 회원이 없으면 같은 화면에서 다시 검색할 수 있습니다.
     */
    private void SearchCustomers() {
        List<Customer> results;
        while (true) {
            System.out.print("검색할 이름 또는 이메일을 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
            String keyword = scanner.nextLine().trim();
            CheckMainMenuShortcut(keyword);
            if (IsCancelled(keyword)) {
                return;
            }
            if (keyword.isEmpty()) {
                System.out.println("검색어를 입력해주세요.");
                continue;
            }

            try {
                results = customerService.SearchByNameOrEmail(keyword);
            } catch (IllegalStateException e) {
                System.out.println(DB_ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                System.out.println(COMMUNICATION_ERROR_MESSAGE);
                return;
            }

            if (results.isEmpty()) {
                System.out.println("\n일치하는 회원이 없습니다. 다시 검색해주세요.");
                continue;
            }
            break;
        }

        if (results.size() == 1) {
            System.out.println("\n=== 검색 결과 1명 ===");
            System.out.println(FormatSearchLine(results.get(0), false));
            while (true) {
                System.out.print("\n1: 상세조회   0: 뒤로가기   p: 회원 관리 메뉴로 이동: ");
                String choice = scanner.nextLine().trim();
                CheckMainMenuShortcut(choice);
                if (IsCancelled(choice)) {
                    return;
                }
                if ("1".equals(choice)) {
                    DisplayById(results.get(0).getCustomerId());
                    return;
                }
                System.out.println("잘못된 입력입니다.");
            }
        }

        Customer selected = SelectFromResults(results);
        if (selected != null) {
            DisplayById(selected.getCustomerId());
        }
    }

    /**
     * 예외의 원인을 타고 올라가며 PostgreSQL SQL 상태 코드가 일치하는지 확인합니다.
     * 제약 위반(예: 유니크/외래키)과, 통신이 끊긴 것 같은 순수 연결 문제를 구분할 때 사용합니다.
     * (23505 = 유니크 제약 위반, 23503 = 외래키 제약 위반)
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

    /**
     * 전화번호에서 하이픈 등을 제거하고 뒷자리 4자리만 뽑아냅니다. 동명이인을 구분할 때 사용합니다.
     */
    private String LastFourDigits(String phone) {
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        if (digitsOnly.length() <= 4) {
            return digitsOnly;
        }
        return digitsOnly.substring(digitsOnly.length() - 4);
    }

    /**
     * 회원 목록에 쓰는 기본 한 줄 형식입니다 ("아이디 | 이름 | 이메일 | 전화번호 | 상태").
     */
    private String FormatBasicLine(Customer customer) {
        return customer.getCustomerId() + " | " + customer.getCustomerName() + " | " + customer.getEmail()
                + " | " + customer.getPhone() + " | " + FormatStatus(customer);
    }

    /**
     * "전체 회원 조회 - 리스트로 보기"에 쓰는 형식입니다. 전화번호는 빼고 보여줍니다.
     */
    private String FormatListLine(Customer customer) {
        return customer.getCustomerId() + " | " + customer.getCustomerName() + " | " + customer.getEmail()
                + " | " + FormatStatus(customer);
    }

    /**
     * 검색 결과 목록에 쓰는 형식입니다. 상세조회와 구분하기 위해 아이디/이름/이메일만 보여주고,
     * 목록 안에 동명이인이 있을 때만 휴대폰 뒷자리를 이름 옆에 같이 보여줘서 구분할 수 있게 합니다.
     */
    private String FormatSearchLine(Customer customer, boolean showPhoneSuffix) {
        String nameSuffix = showPhoneSuffix ? " (뒷자리 " + LastFourDigits(customer.getPhone()) + ")" : "";
        return customer.getCustomerId() + " | " + customer.getCustomerName() + nameSuffix + " | " + customer.getEmail();
    }

    /**
     * 목록 안에서 이 회원과 이름이 같은 다른 회원이 있는지 확인합니다. 동명이인 여부를 판단할 때 사용합니다.
     */
    private boolean HasDuplicateName(Customer customer, List<Customer> results) {
        int count = 0;
        for (Customer other : results) {
            if (other.getCustomerName().equals(customer.getCustomerName())) {
                count++;
                if (count > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 상세 조회/삭제 확인 화면에 쓰는 형식입니다. 가입일까지 같이 보여줍니다.
     */
    private String FormatDetailLine(Customer customer) {
        return customer.getCustomerId() + " | " + customer.getCustomerName() + " | " + customer.getEmail()
                + " | " + customer.getPhone() + " | 가입일: " + customer.getCreatedAt().toLocalDate()
                + " | " + FormatStatus(customer);
    }

    /**
     * 계정 활성 상태를 "상태: 활성"/"상태: 비활성" 문자열로 바꿉니다.
     */
    private String FormatStatus(Customer customer) {
        return "상태: " + (Boolean.TRUE.equals(customer.getIsActive()) ? "활성" : "비활성");
    }

    /**
     * 여러 명(동명이인 등)의 검색 결과를 {@value #PAGE_SIZE}명씩 페이지로 나눠 보여주고,
     * 회원번호를 입력받아 그중 한 명을 고르게 합니다.
     *
     * @param results 고를 대상 목록 (2명 이상)
     * @return 고른 Customer, 취소했으면 null
     */
    private Customer SelectFromResults(List<Customer> results) {
        int totalPages = (int) Math.ceil((double) results.size() / PAGE_SIZE);
        int page = 1;

        while (true) {
            int fromIndex = (page - 1) * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, results.size());

            System.out.println("\n=== 검색 결과 " + results.size() + "명 (" + page + "/" + totalPages + " 페이지) ===");
            for (Customer customer : results.subList(fromIndex, toIndex)) {
                System.out.println(FormatSearchLine(customer, HasDuplicateName(customer, results)));
            }

            if (totalPages > 1) {
                System.out.print("\nz: 이전 페이지   x: 다음 페이지   회원번호 입력: 선택   0: 취소   p: 회원 관리 메뉴로 이동: ");
            } else {
                System.out.print("\n회원번호 입력: 선택   0: 취소   p: 회원 관리 메뉴로 이동: ");
            }
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);

            if (totalPages > 1 && "x".equalsIgnoreCase(choice)) {
                if (page < totalPages) {
                    page++;
                } else {
                    System.out.println("마지막 페이지입니다.");
                }
                continue;
            }
            if (totalPages > 1 && "z".equalsIgnoreCase(choice)) {
                if (page > 1) {
                    page--;
                } else {
                    System.out.println("첫 페이지입니다.");
                }
                continue;
            }
            if (IsCancelled(choice)) {
                return null;
            }

            try {
                Long customerId = Long.parseLong(choice);
                for (Customer customer : results) {
                    if (customer.getCustomerId().equals(customerId)) {
                        return customer;
                    }
                }
                System.out.println("목록에 없는 번호입니다.");
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    /**
     * 이미 알고 있는 고객 번호로 바로 상세 정보를 조회합니다.
     * 목록/검색 화면에서 번호를 다시 물어보지 않고 바로 넘어올 때 사용합니다.
     */
    private void DisplayById(Long customerId) {
        Optional<Customer> customer;
        try {
            customer = customerService.FindById(customerId);
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return;
        }
        if (customer.isEmpty()) {
            System.out.println("해당 번호의 회원이 없습니다.");
            WaitForBack();
            return;
        }

        Customer found = customer.get();
        System.out.println(FormatDetailLine(found));

        while (true) {
            String toggleLabel = Boolean.TRUE.equals(found.getIsActive()) ? "5. 회원 비활성화" : "5. 회원 활성화";
            System.out.print("\n3. 회원 정보 수정으로 이동   4. 회원 삭제   " + toggleLabel + "   0. 뒤로가기   p: 회원 관리 메뉴로 이동: ");
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            if ("3".equals(choice)) {
                UpdateInfo(found);
                return;
            }
            if ("4".equals(choice)) {
                if (!ConfirmAndDelete(found)) {
                    Delete();
                }
                return;
            }
            if ("5".equals(choice)) {
                if (!ConfirmAndToggleActive(found)) {
                    Deactivate();
                }
                return;
            }
            if (IsCancelled(choice)) {
                return;
            }
            PrintInvalidChoiceMessage(choice);
        }
    }

    /**
     * 회원 이름 또는 아이디(이메일)를 입력받아 찾은 뒤, 정보 수정 메뉴로 이동합니다.
     * 일치하는 회원이 한 명이면 바로 이동하고, 동명이인이 여러 명이면 목록에서 고르게 합니다.
     */
    public void UpdateInfo() {
        System.out.print("수정할 회원 이름 또는 아이디(이메일)를 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
        String keyword = scanner.nextLine().trim();
        CheckMainMenuShortcut(keyword);
        if (IsCancelled(keyword)) {
            return;
        }
        if (keyword.isEmpty()) {
            System.out.println("이름 또는 아이디를 입력해주세요.");
            return;
        }

        List<Customer> results;
        try {
            results = customerService.SearchByNameOrEmail(keyword);
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return;
        }

        if (results.isEmpty()) {
            System.out.println("일치하는 회원이 없습니다.");
            WaitForBack();
            return;
        }

        if (results.size() == 1) {
            UpdateInfo(results.get(0));
            return;
        }

        Customer selected = SelectFromResults(results);
        if (selected != null) {
            UpdateInfo(selected);
        }
    }

    /**
     * 이미 조회된 Customer 객체를 받아, 무엇을 수정/삭제할지 고르는 메뉴를 보여줍니다.
     * 상세 조회 화면에서 바로 넘어올 때처럼, 고객 번호를 다시 물어볼 필요가 없는 경우에 사용합니다.
     */
    private void UpdateInfo(Customer customer) {
        while (true) {
            System.out.println("\n=== 회원 정보 수정 ===");
            System.out.println(FormatBasicLine(customer));
            System.out.println("1. 이름 변경");
            System.out.println("2. 전화번호 변경");
            System.out.println("3. 이메일(ID) 변경");
            System.out.println("4. 회원 삭제");
            System.out.println(Boolean.TRUE.equals(customer.getIsActive()) ? "5. 회원 비활성화" : "5. 회원 활성화");
            System.out.println("6. 비밀번호 초기화");
            System.out.println("0. 뒤로가기 (p: 회원 관리 메뉴로 이동)");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            switch (choice) {
                case "1" -> UpdateName(customer);
                case "2" -> UpdatePhone(customer);
                case "3" -> UpdateEmail(customer);
                case "4" -> {
                    if (!ConfirmAndDelete(customer)) {
                        Delete();
                    }
                    return;
                }
                case "5" -> {
                    if (Boolean.TRUE.equals(customer.getIsActive())) {
                        if (!HasOrderHistory(customer)) {
                            SetActiveStatus(customer, false);
                        }
                    } else {
                        SetActiveStatus(customer, true);
                    }
                }
                case "6" -> ResetPassword(customer);
                case "0" -> {
                    return;
                }
                default -> PrintInvalidChoiceMessage(choice);
            }
        }
    }

    /**
     * 확인을 받은 뒤 회원의 비밀번호를 기본 비밀번호로 초기화합니다.
     */
    private void ResetPassword(Customer customer) {
        System.out.print("\"" + customer.getCustomerName() + "\" 회원의 비밀번호를 초기화하시겠습니까? (y: 초기화, 0 또는 그 외 입력: 취소): ");
        String confirm = scanner.nextLine().trim();
        CheckMainMenuShortcut(confirm);
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("취소했습니다.");
            return;
        }

        try {
            if (!customerService.ResetPassword(customer)) {
                System.out.println("비밀번호 초기화에 실패했습니다.");
                return;
            }
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return;
        }

        System.out.println("\n비밀번호가 \"" + CustomerService.DEFAULT_PASSWORD + "\"(으)로 초기화되었습니다. 회원에게 로그인 후 비밀번호를 변경하도록 안내해주세요.");
        System.out.print("\n0을 입력하면 회원 관리 메뉴로 이동: ");
        while (!CANCEL_INPUT.equals(scanner.nextLine().trim())) {
            System.out.print("0을 입력하면 회원 관리 메뉴로 이동: ");
        }
        throw new ReturnToMainMenu();
    }

    /**
     * 고객 이름만 수정합니다.
     */
    private void UpdateName(Customer customer) {
        while (true) {
            System.out.print("새 이름 (현재: " + customer.getCustomerName() + ", 0: 취소, p: 회원 관리 메뉴로 이동): ");
            String newName = scanner.nextLine().trim();
            CheckMainMenuShortcut(newName);
            if (IsCancelled(newName)) {
                return;
            }
            if (newName.isEmpty()) {
                System.out.println("이름을 입력해주세요.");
                continue;
            }
            if (newName.length() > NAME_MAX_LENGTH) {
                System.out.println("이름이 너무 깁니다. (" + NAME_MAX_LENGTH + "자 이하)");
                continue;
            }
            if (newName.equals(customer.getCustomerName())) {
                System.out.println("동일한 이름은 사용할 수 없습니다. 다시 입력해주세요.");
                continue;
            }
            customer.setCustomerName(newName);
            break;
        }

        try {
            boolean updated = customerService.Update(customer);
            System.out.println(updated ? "이름이 \"" + customer.getCustomerName() + "\"(으)로 변경되었습니다." : "이름 변경에 실패했습니다.");
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
        }
    }

    /**
     * 고객 전화번호만 수정합니다.
     */
    private void UpdatePhone(Customer customer) {
        while (true) {
            System.out.print("새 전화번호 (현재: " + customer.getPhone() + ", 0: 취소, p: 회원 관리 메뉴로 이동): ");
            String newPhone = scanner.nextLine().trim();
            CheckMainMenuShortcut(newPhone);
            if (IsCancelled(newPhone)) {
                return;
            }
            if (!newPhone.matches(PHONE_PATTERN)) {
                System.out.println("전화번호 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }

            String normalizedPhone = newPhone.replace("-", "");
            try {
                if (customerService.IsPhoneTaken(normalizedPhone, customer.getCustomerId())) {
                    System.out.println("이미 사용 중인 전화번호입니다. 다시 입력해주세요.");
                    continue;
                }
            } catch (IllegalStateException e) {
                System.out.println(DB_ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                System.out.println(COMMUNICATION_ERROR_MESSAGE);
                return;
            }
            customer.setPhone(normalizedPhone);
            break;
        }

        try {
            boolean updated = customerService.Update(customer);
            System.out.println(updated ? "전화번호가 \"" + customer.getPhone() + "\"(으)로 변경되었습니다." : "전화번호 변경에 실패했습니다.");
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
        }
    }

    /**
     * 고객의 로그인 이메일(ID)만 수정합니다. app_user 테이블의 email 컬럼을 바꾸는 것이라
     * customer가 아니라 auth 쪽 데이터를 수정합니다.
     */
    private void UpdateEmail(Customer customer) {
        while (true) {
            System.out.print("새 이메일/ID (현재: " + customer.getEmail() + ", 0: 취소, p: 회원 관리 메뉴로 이동): ");
            String newEmail = scanner.nextLine().trim().toLowerCase();
            CheckMainMenuShortcut(newEmail);
            if (IsCancelled(newEmail)) {
                return;
            }
            if (!newEmail.matches(EMAIL_PATTERN)) {
                System.out.println("이메일 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }
            if (newEmail.length() > EMAIL_MAX_LENGTH) {
                System.out.println("이메일이 너무 깁니다. (" + EMAIL_MAX_LENGTH + "자 이하) 다시 입력해주세요.");
                continue;
            }

            try {
                boolean updated = customerService.UpdateEmail(customer, newEmail);
                if (updated) {
                    customer.setEmail(newEmail);
                    System.out.println("이메일(ID)이 \"" + newEmail + "\"(으)로 수정되었습니다.");
                } else {
                    System.out.println("이메일 수정에 실패했습니다.");
                }
            } catch (IllegalStateException e) {
                System.out.println(DB_ERROR_MESSAGE);
            } catch (Exception e) {
                if (IsSqlState(e, "23505")) {
                    System.out.println("이미 사용 중인 이메일이라 변경할 수 없습니다. 다시 입력해주세요.");
                    continue;
                }
                System.out.println(COMMUNICATION_ERROR_MESSAGE);
            }
            break;
        }
    }

    /**
     * "회원 삭제" 메뉴를 출력합니다. 이름으로 바로 찾거나, 이름/전화번호로 검색할 수 있습니다.
     */
    public void Delete() {
        while (true) {
            System.out.println("\n=== 회원 삭제 ===");
            System.out.println("1. 회원 이름 입력");
            System.out.println("2. 회원 검색");
            System.out.println("0. 뒤로가기");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    if (FindAndDeleteByName()) {
                        return;
                    }
                }
                case "2" -> {
                    if (FindAndDeleteBySearch()) {
                        return;
                    }
                }
                case "0" -> {
                    return;
                }
                default -> PrintInvalidChoiceMessage(choice);
            }
        }
    }

    /**
     * 이름을 입력받아 삭제 대상을 찾습니다. 동명이인이 여러 명이면 휴대폰 뒷자리와 함께 목록을 보여주고 고르게 합니다.
     *
     * @return "뒤로가기"를 선택해서 Delete() 전체를 끝내야 하면 true, 삭제 메뉴로 돌아가야 하면 false
     */
    private boolean FindAndDeleteByName() {
        System.out.print("삭제할 회원 이름을 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
        String name = scanner.nextLine().trim();
        CheckMainMenuShortcut(name);
        if (IsCancelled(name)) {
            return false;
        }
        if (name.isEmpty()) {
            System.out.println("이름을 입력해주세요.");
            return false;
        }

        List<Customer> results;
        try {
            results = customerService.SearchByName(name);
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return false;
        }

        return ResolveDeleteTarget(results);
    }

    /**
     * 이름 또는 전화번호로 검색해서 삭제 대상을 찾습니다.
     *
     * @return "뒤로가기"를 선택해서 Delete() 전체를 끝내야 하면 true, 삭제 메뉴로 돌아가야 하면 false
     */
    private boolean FindAndDeleteBySearch() {
        List<Customer> results;
        while (true) {
            System.out.print("검색할 이름 또는 이메일을 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
            String keyword = scanner.nextLine().trim();
            CheckMainMenuShortcut(keyword);
            if (IsCancelled(keyword)) {
                return false;
            }
            if (keyword.isEmpty()) {
                System.out.println("검색어를 입력해주세요.");
                continue;
            }

            try {
                results = customerService.SearchByNameOrEmail(keyword);
            } catch (IllegalStateException e) {
                System.out.println(DB_ERROR_MESSAGE);
                return false;
            } catch (Exception e) {
                System.out.println(COMMUNICATION_ERROR_MESSAGE);
                return false;
            }

            if (results.isEmpty()) {
                System.out.println("일치하는 회원이 없습니다. 다시 검색해주세요.");
                continue;
            }
            break;
        }

        return ResolveDeleteTarget(results);
    }

    /**
     * 검색 결과가 0명/1명/동명이인 여러 명인 경우를 처리해서 삭제 대상 한 명을 정한 뒤 ConfirmAndDelete()로 넘깁니다.
     *
     * @return "뒤로가기"를 선택해서 Delete() 전체를 끝내야 하면 true, 삭제 메뉴로 돌아가야 하면 false
     */
    private boolean ResolveDeleteTarget(List<Customer> results) {
        if (results.isEmpty()) {
            System.out.println("일치하는 회원이 없습니다.");
            return false;
        }

        if (results.size() == 1) {
            return ConfirmAndDelete(results.get(0));
        }

        Customer selected = SelectFromResults(results);
        if (selected == null) {
            return false;
        }
        return ConfirmAndDelete(selected);
    }

    /**
     * "회원 비활성화" 메뉴를 출력합니다. 이름으로 바로 찾거나, 이름/전화번호로 검색할 수 있습니다.
     * 이미 비활성 상태인 회원을 고르면 다시 활성화할 수도 있습니다.
     */
    public void Deactivate() {
        while (true) {
            System.out.println("\n=== 회원 비활성화 ===");
            System.out.println("1. 회원 이름 입력");
            System.out.println("2. 회원 검색");
            System.out.println("0. 뒤로가기");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    if (FindAndToggleActiveByName()) {
                        return;
                    }
                }
                case "2" -> {
                    if (FindAndToggleActiveBySearch()) {
                        return;
                    }
                }
                case "0" -> {
                    return;
                }
                default -> PrintInvalidChoiceMessage(choice);
            }
        }
    }

    /**
     * 이름을 입력받아 비활성화(또는 활성화) 대상을 찾습니다. 동명이인이 여러 명이면 목록에서 고르게 합니다.
     *
     * @return "뒤로가기"를 선택해서 Deactivate() 전체를 끝내야 하면 true, 비활성화 메뉴로 돌아가야 하면 false
     */
    private boolean FindAndToggleActiveByName() {
        System.out.print("비활성화(또는 활성화)할 회원 이름을 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
        String name = scanner.nextLine().trim();
        CheckMainMenuShortcut(name);
        if (IsCancelled(name)) {
            return false;
        }
        if (name.isEmpty()) {
            System.out.println("이름을 입력해주세요.");
            return false;
        }

        List<Customer> results;
        try {
            results = customerService.SearchByName(name);
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return false;
        }

        return ResolveToggleActiveTarget(results);
    }

    /**
     * 이름 또는 전화번호로 검색해서 비활성화(또는 활성화) 대상을 찾습니다.
     *
     * @return "뒤로가기"를 선택해서 Deactivate() 전체를 끝내야 하면 true, 비활성화 메뉴로 돌아가야 하면 false
     */
    private boolean FindAndToggleActiveBySearch() {
        List<Customer> results;
        while (true) {
            System.out.print("검색할 이름 또는 이메일을 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
            String keyword = scanner.nextLine().trim();
            CheckMainMenuShortcut(keyword);
            if (IsCancelled(keyword)) {
                return false;
            }
            if (keyword.isEmpty()) {
                System.out.println("검색어를 입력해주세요.");
                continue;
            }

            try {
                results = customerService.SearchByNameOrEmail(keyword);
            } catch (IllegalStateException e) {
                System.out.println(DB_ERROR_MESSAGE);
                return false;
            } catch (Exception e) {
                System.out.println(COMMUNICATION_ERROR_MESSAGE);
                return false;
            }

            if (results.isEmpty()) {
                System.out.println("일치하는 회원이 없습니다. 다시 검색해주세요.");
                continue;
            }
            break;
        }

        return ResolveToggleActiveTarget(results);
    }

    /**
     * 검색 결과가 0명/1명/동명이인 여러 명인 경우를 처리해서 대상 한 명을 정한 뒤 ConfirmAndToggleActive()로 넘깁니다.
     *
     * @return "뒤로가기"를 선택해서 Deactivate() 전체를 끝내야 하면 true, 비활성화 메뉴로 돌아가야 하면 false
     */
    private boolean ResolveToggleActiveTarget(List<Customer> results) {
        if (results.isEmpty()) {
            System.out.println("일치하는 회원이 없습니다.");
            return false;
        }

        if (results.size() == 1) {
            return ConfirmAndToggleActive(results.get(0));
        }

        Customer selected = SelectFromResults(results);
        if (selected == null) {
            return false;
        }
        return ConfirmAndToggleActive(selected);
    }

    /**
     * 대상 회원 정보를 다시 보여주고 한 번 더 확인시킨 뒤 활성 상태를 바꿉니다. 주문 이력이 있으면 비활성화할 수 없습니다.
     * 처리(또는 취소) 후에는 계속 다른 회원을 처리할지, 회원 관리 메뉴로 돌아갈지 선택하게 합니다.
     *
     * @return "뒤로가기"를 선택해서 Deactivate() 전체를 끝내야 하면 true, 비활성화 메뉴로 돌아가야 하면 false
     */
    private boolean ConfirmAndToggleActive(Customer customer) {
        System.out.println("\n=== 대상 회원 정보 ===");
        System.out.println(FormatDetailLine(customer));

        if (Boolean.TRUE.equals(customer.getIsActive())) {
            if (!HasOrderHistory(customer)) {
                System.out.print("정말 비활성화하시겠습니까? (y: 비활성화, 0 또는 그 외: 취소): ");
                String confirm = scanner.nextLine().trim();
                if ("y".equalsIgnoreCase(confirm)) {
                    SetActiveStatus(customer, false);
                } else {
                    System.out.println("취소했습니다.");
                }
            }
        } else {
            System.out.print("이미 비활성 상태입니다. 다시 활성화하시겠습니까? (y: 활성화, 0 또는 그 외: 취소): ");
            String confirm = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(confirm)) {
                SetActiveStatus(customer, true);
            } else {
                System.out.println("취소했습니다.");
            }
        }

        while (true) {
            System.out.println("\n1. 이어서 처리하기");
            System.out.println("0. 뒤로가기 (회원 관리 메뉴로 이동)");
            System.out.print("번호를 입력하세요: ");
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            switch (choice) {
                case "1":
                    return false;
                case "0":
                    return true;
                default:
                    PrintInvalidChoiceMessage(choice);
            }
        }
    }

    /**
     * 삭제하기 전에 회원 정보를 다시 보여주고 한 번 더 확인시킨 뒤 삭제합니다.
     * 삭제(또는 취소) 후에는 계속 다른 회원을 삭제할지, 회원 관리 메뉴로 돌아갈지 선택하게 합니다.
     *
     * @return "뒤로가기"를 선택해서 Delete() 전체를 끝내야 하면 true, 삭제 메뉴로 돌아가야 하면 false
     */
    private boolean ConfirmAndDelete(Customer customer) {
        System.out.println("\n=== 삭제할 회원 정보 ===");
        System.out.println(FormatDetailLine(customer));

        if (!HasOrderHistory(customer)) {
            System.out.println("\n이 회원은 주문 내역이 없습니다.");
            DeleteCustomer(customer);
        }

        while (true) {
            System.out.println("\n1. 이어서 삭제하기");
            System.out.println("0. 뒤로가기 (회원 관리 메뉴로 이동)");
            System.out.print("번호를 입력하세요: ");
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            switch (choice) {
                case "1":
                    return false;
                case "0":
                    return true;
                default:
                    PrintInvalidChoiceMessage(choice);
            }
        }
    }

    /**
     * 이 회원에게 주문 이력이 있는지 확인합니다. 있으면 안내 메시지를 출력합니다.
     * 주문 이력이 있는 회원은 삭제와 비활성화 둘 다 할 수 없다는 규칙을, 삭제/비활성화 두 흐름에서 공통으로 씁니다.
     *
     * @return 주문 이력이 있어서(또는 조회 자체가 실패해서) 삭제/비활성화를 막아야 하면 true
     */
    private boolean HasOrderHistory(Customer customer) {
        List<OrderSummaryView> orders;
        try {
            orders = customerService.FindOrderHistory(customer.getCustomerId());
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return true;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return true;
        }

        if (orders.isEmpty()) {
            return false;
        }
        System.out.println("\n이 회원은 주문 이력이 " + orders.size() + "건 있어서 삭제와 비활성화 모두 할 수 없습니다.");
        return true;
    }

    /**
     * 회원 계정의 활성 상태를 바꿉니다. "회원 정보 수정" 화면의 활성화/비활성화 메뉴에서 사용합니다.
     */
    private void SetActiveStatus(Customer customer, boolean active) {
        try {
            boolean updated = customerService.UpdateActiveStatus(customer, active);
            if (updated) {
                customer.setIsActive(active);
                System.out.println("계정이 " + (active ? "활성화" : "비활성화") + "되었습니다.");
            } else {
                System.out.println("상태 변경에 실패했습니다.");
            }
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
        }
    }

    /**
     * 확인을 받은 뒤 고객 정보를 삭제합니다. 주문 이력이 있으면 삭제가 거절됩니다.
     *
     * @return 실제로 삭제되었으면 true
     */
    private boolean DeleteCustomer(Customer customer) {
        System.out.print("정말 삭제하시겠습니까? (y: 삭제, 0 또는 그 외 입력: 취소): ");
        String confirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("취소했습니다.");
            return false;
        }

        try {
            boolean deleted = customerService.DeleteById(customer);
            System.out.println(deleted ? "회원이 삭제되었습니다." : "회원 삭제에 실패했습니다.");
            return deleted;
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            if (IsSqlState(e, "23503")) {
                System.out.println("회원 삭제에 실패했습니다. 주문 이력이 있는 회원은 삭제할 수 없습니다.");
            } else {
                System.out.println(COMMUNICATION_ERROR_MESSAGE);
            }
            return false;
        }
    }

}
