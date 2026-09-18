package com.team.orderapp.customer;

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
            System.out.println("0. 뒤로가기");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> DisplayAll();
                    case "2" -> DisplayByIdMenu();
                    case "3" -> UpdateInfo();
                    case "4" -> Delete();
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("잘못된 번호입니다.");
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
            System.out.println("0. 뒤로가기 (p: 회원 관리 메뉴로 이동)");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            switch (choice) {
                case "1" -> DisplayAllPaged();
                case "2" -> SearchCustomers();
                case "0" -> {
                    return;
                }
                default -> System.out.println("잘못된 번호입니다.");
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
                System.out.println(customer.getCustomerId() + " | " + customer.getCustomerName() + " | " + customer.getEmail() + " | " + customer.getPhone());
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
            System.out.println("0. 뒤로가기 (p: 회원 관리 메뉴로 이동)");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            switch (choice) {
                case "1" -> SearchByName();
                case "2" -> SearchCustomers();
                case "0" -> {
                    return;
                }
                default -> System.out.println("잘못된 번호입니다.");
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

        System.out.println("\n=== 동명이인 " + results.size() + "명이 있습니다 ===");
        for (Customer customer : results) {
            System.out.println(customer.getCustomerId() + " | " + customer.getCustomerName()
                    + " (뒷자리 " + LastFourDigits(customer.getPhone()) + ") | " + customer.getEmail() + " | " + customer.getPhone());
        }

        while (true) {
            System.out.print("\n회원번호 입력: 상세조회   0: 뒤로가기   p: 회원 관리 메뉴로 이동: ");
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            if (IsCancelled(choice)) {
                return;
            }
            try {
                DisplayById(Long.parseLong(choice));
                return;
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    /**
     * 이름 또는 전화번호로 회원을 검색하여 결과를 보여줍니다.
     */
    private void SearchCustomers() {
        System.out.print("검색할 이름 또는 전화번호를 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
        String keyword = scanner.nextLine().trim();
        CheckMainMenuShortcut(keyword);
        if (IsCancelled(keyword)) {
            return;
        }
        if (keyword.isEmpty()) {
            System.out.println("검색어를 입력해주세요.");
            return;
        }

        List<Customer> results;
        try {
            results = customerService.SearchByNameOrPhone(keyword);
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return;
        }

        System.out.println("\n=== 검색 결과 (" + results.size() + "건) ===");
        if (results.isEmpty()) {
            System.out.println("일치하는 회원이 없습니다.");
            WaitForBack();
            return;
        }
        for (Customer customer : results) {
            System.out.println(customer.getCustomerId() + " | " + customer.getCustomerName()
                    + " (뒷자리 " + LastFourDigits(customer.getPhone()) + ") | " + customer.getEmail() + " | " + customer.getPhone());
        }

        while (true) {
            System.out.print("\n회원번호 입력: 상세조회   0: 뒤로가기   p: 회원 관리 메뉴로 이동: ");
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            if (IsCancelled(choice)) {
                return;
            }
            try {
                DisplayById(Long.parseLong(choice));
                return;
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다.");
            }
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
        System.out.println(found.getCustomerId() + " | " + found.getCustomerName() + " | " + found.getEmail() + " | " + found.getPhone() + " | 가입일: " + found.getCreatedAt().toLocalDate());

        while (true) {
            System.out.print("\n3. 회원 정보 수정으로 이동   4. 회원 삭제   0. 뒤로가기   p: 회원 관리 메뉴로 이동: ");
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            if ("3".equals(choice)) {
                UpdateInfo(found);
                return;
            }
            if ("4".equals(choice)) {
                if (DeleteCustomer(found)) {
                    return;
                }
                continue;
            }
            if (IsCancelled(choice)) {
                return;
            }
            System.out.println("잘못된 번호입니다.");
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

        System.out.println("\n=== 동명이인 " + results.size() + "명이 있습니다 ===");
        for (Customer customer : results) {
            System.out.println(customer.getCustomerId() + " | " + customer.getCustomerName()
                    + " (뒷자리 " + LastFourDigits(customer.getPhone()) + ") | " + customer.getEmail() + " | " + customer.getPhone());
        }

        while (true) {
            System.out.print("\n회원번호 입력: 정보 수정   0: 뒤로가기   p: 회원 관리 메뉴로 이동: ");
            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            if (IsCancelled(choice)) {
                return;
            }

            Long customerId;
            try {
                customerId = Long.parseLong(choice);
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다.");
                continue;
            }

            Customer selected = null;
            for (Customer customer : results) {
                if (customer.getCustomerId().equals(customerId)) {
                    selected = customer;
                    break;
                }
            }
            if (selected == null) {
                System.out.println("목록에 없는 번호입니다.");
                continue;
            }

            UpdateInfo(selected);
            return;
        }
    }

    /**
     * 이미 조회된 Customer 객체를 받아, 무엇을 수정/삭제할지 고르는 메뉴를 보여줍니다.
     * 상세 조회 화면에서 바로 넘어올 때처럼, 고객 번호를 다시 물어볼 필요가 없는 경우에 사용합니다.
     */
    private void UpdateInfo(Customer customer) {
        while (true) {
            System.out.println("\n=== 회원 정보 수정 ===");
            System.out.println(customer.getCustomerId() + " | " + customer.getCustomerName() + " | " + customer.getEmail() + " | " + customer.getPhone());
            System.out.println("1. 이름 변경");
            System.out.println("2. 전화번호 변경");
            System.out.println("3. 이메일(ID) 변경");
            System.out.println("4. 회원 삭제");
            System.out.println("0. 뒤로가기 (p: 회원 관리 메뉴로 이동)");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            CheckMainMenuShortcut(choice);
            switch (choice) {
                case "1" -> UpdateName(customer);
                case "2" -> UpdatePhone(customer);
                case "3" -> UpdateEmail(customer);
                case "4" -> {
                    if (DeleteCustomer(customer)) {
                        return;
                    }
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("잘못된 번호입니다.");
            }
        }
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
            System.out.println(updated ? "이름이 변경되었습니다." : "이름 변경에 실패했습니다.");
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
            System.out.println(updated ? "전화번호가 변경되었습니다." : "전화번호 변경에 실패했습니다.");
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
                    System.out.println("이메일(ID)이 수정되었습니다.");
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
     * 고객 번호를 입력받아 고객 정보를 삭제합니다. 주문 이력이 있으면 삭제가 거절됩니다.
     */
    public void Delete() {
        System.out.print("삭제할 고객 번호를 입력하세요 (0: 취소, p: 회원 관리 메뉴로 이동): ");
        String input = scanner.nextLine().trim();
        CheckMainMenuShortcut(input);
        if (IsCancelled(input)) {
            return;
        }

        Long customerId;
        try {
            customerId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해주세요.");
            return;
        }

        Optional<Customer> existing;
        try {
            existing = customerService.FindById(customerId);
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        } catch (Exception e) {
            System.out.println(COMMUNICATION_ERROR_MESSAGE);
            return;
        }
        if (existing.isEmpty()) {
            System.out.println("해당 번호의 회원이 없습니다.");
            WaitForBack();
            return;
        }

        DeleteCustomer(existing.get());
        WaitForBack();
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
