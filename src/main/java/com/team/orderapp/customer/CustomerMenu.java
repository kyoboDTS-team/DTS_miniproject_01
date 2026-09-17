package com.team.orderapp.customer;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 회원 정보 조회 등 고객 관련 콘솔 화면을 담당하는 클래스입니다.
 */
public class CustomerMenu {

    private static final String CANCEL_INPUT = "0";
    private static final String DB_ERROR_MESSAGE = "DB 연결에 실패했습니다. 잠시 후 다시 시도해주세요.";

    private final CustomerService customerService;
    // common/ConsoleInput이 아직 구현되지 않아 임시로 직접 사용. 완성되면 교체 필요.
    private final Scanner scanner;

    public CustomerMenu() {
        this.customerService = new CustomerService();
        this.scanner = new Scanner(System.in);
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
            System.out.println("3. 정보 수정");
            System.out.println("4. 회원 삭제");
            System.out.println("0. 뒤로가기");
            System.out.print("번호를 입력하세요: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> DisplayAll();
                case "2" -> DisplayById();
                case "3" -> UpdateInfo();
                case "4" -> Delete();
                case "0" -> {
                    return;
                }
                default -> System.out.println("잘못된 번호입니다.");
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
     * 결과를 확인할 시간을 주기 위해, "0"을 입력할 때까지 화면에 머무릅니다.
     */
    private void WaitForBack() {
        System.out.print("\n0을 입력하면 뒤로가기: ");
        while (!CANCEL_INPUT.equals(scanner.nextLine().trim())) {
            System.out.print("0을 입력하면 뒤로가기: ");
        }
    }

    /**
     * 전체 회원 목록을 조회하여 콘솔에 출력합니다.
     */
    public void DisplayAll() {
        List<Customer> customers;
        try {
            customers = customerService.FindAll();
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        }

        System.out.println("\n=== 전체 회원 목록 ===");
        if (customers.isEmpty()) {
            System.out.println("등록된 회원이 없습니다.");
        } else {
            for (Customer customer : customers) {
                System.out.println(customer.getCustomerId() + " | " + customer.getCustomerName() + " | " + customer.getPhone());
            }
        }

        WaitForBack();
    }

    /**
     * 고객 번호를 입력받아 상세 정보를 조회하여 콘솔에 출력합니다.
     */
    public void DisplayById() {
        System.out.print("조회할 고객 번호를 입력하세요 (0: 취소): ");
        String input = scanner.nextLine().trim();
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

        Optional<Customer> customer;
        try {
            customer = customerService.FindById(customerId);
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
            return;
        }
        if (customer.isEmpty()) {
            System.out.println("해당 번호의 회원이 없습니다.");
        } else {
            Customer found = customer.get();
            System.out.println(found.getCustomerId() + " | " + found.getCustomerName() + " | " + found.getPhone() + " | 가입일: " + found.getCreatedAt());
        }

        WaitForBack();
    }

    /**
     * 고객 번호를 입력받아 이름/전화번호를 수정합니다. 빈 값을 입력하면 기존 값을 유지합니다.
     */
    public void UpdateInfo() {
        System.out.print("수정할 고객 번호를 입력하세요 (0: 취소): ");
        String idInput = scanner.nextLine().trim();
        if (IsCancelled(idInput)) {
            return;
        }

        Long customerId;
        try {
            customerId = Long.parseLong(idInput);
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
        }
        if (existing.isEmpty()) {
            System.out.println("해당 번호의 회원이 없습니다.");
            WaitForBack();
            return;
        }

        Customer customer = existing.get();

        System.out.print("새 이름 (현재: " + customer.getCustomerName() + ", 그대로 두려면 엔터, 0: 취소): ");
        String newName = scanner.nextLine().trim();
        if (IsCancelled(newName)) {
            return;
        }
        if (!newName.isEmpty()) {
            customer.setCustomerName(newName);
        }

        System.out.print("새 전화번호 (현재: " + customer.getPhone() + ", 그대로 두려면 엔터, 0: 취소): ");
        String newPhone = scanner.nextLine().trim();
        if (IsCancelled(newPhone)) {
            return;
        }
        if (!newPhone.isEmpty()) {
            customer.setPhone(newPhone);
        }

        try {
            boolean updated = customerService.Update(customer);
            System.out.println(updated ? "회원 정보가 수정되었습니다." : "회원 정보 수정에 실패했습니다.");
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
        }

        WaitForBack();
    }

    /**
     * 고객 번호를 입력받아 고객 정보를 삭제합니다. 주문 이력이 있으면 삭제가 거절됩니다.
     */
    public void Delete() {
        System.out.print("삭제할 고객 번호를 입력하세요 (0: 취소): ");
        String input = scanner.nextLine().trim();
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
        }
        if (existing.isEmpty()) {
            System.out.println("해당 번호의 회원이 없습니다.");
            WaitForBack();
            return;
        }

        System.out.print("정말 삭제하시겠습니까? (y: 삭제, 0 또는 그 외 입력: 취소): ");
        String confirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("취소했습니다.");
            return;
        }

        try {
            boolean deleted = customerService.DeleteById(customerId);
            System.out.println(deleted ? "회원이 삭제되었습니다." : "회원 삭제에 실패했습니다.");
        } catch (IllegalStateException e) {
            System.out.println(DB_ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println("회원 삭제에 실패했습니다. 주문 이력이 있는 회원은 삭제할 수 없습니다.");
        }

        WaitForBack();
    }

}
