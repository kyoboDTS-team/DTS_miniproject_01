package com.team.orderapp.customer;

import com.team.orderapp.common.BusinessException;
import com.team.orderapp.common.ConsoleInput;

import java.util.List;

/**
 * 고객 관리 콘솔 메뉴 및 사용자 인터랙션을 담당하는 클래스입니다.
 */
public class CustomerMenu {

    private final CustomerService customerService;

    public CustomerMenu() {
        this.customerService = new CustomerService();
    }

    public CustomerMenu(CustomerService InCustomerService) {
        this.customerService = InCustomerService;
    }

    /**
     * 고객 관리 서브 메뉴를 화면에 표시하고 입력을 처리합니다.
     */
    public void DisplayMenu() {
        boolean inMenu = true;
        while (inMenu) {
            PrintCustomerMenuOptions();
            int choice = ConsoleInput.ReadInt("메뉴 번호를 선택하세요: ");
            inMenu = RouteCustomerChoice(choice);
        }
    }

    /**
     * 신규 고객을 등록하는 사용자 흐름을 처리합니다.
     */
    public void RegisterCustomer() {
        System.out.println("\n--- 신규 고객 등록 ---");
        Customer customer = PromptCustomerInput();
        try {
            Customer created = customerService.CreateCustomer(customer);
            System.out.println("고객 등록 완료: " + created.GetName());
        } catch (BusinessException InException) {
            System.out.println("[오류] " + InException.getMessage());
        }
    }

    /**
     * 고객 단건 조회를 처리합니다.
     */
    public void FindCustomer() {
        System.out.println("\n--- 고객 단건 조회 ---");
        long customerId = ConsoleInput.ReadLong("조회할 고객 ID: ");
        try {
            Customer customer = customerService.GetCustomerById(customerId);
            PrintCustomerDetail(customer);
        } catch (BusinessException InException) {
            System.out.println("[오류] " + InException.getMessage());
        }
    }

    /**
     * 전체 고객 목록 조회를 처리합니다.
     */
    public void ListCustomers() {
        System.out.println("\n--- 고객 전체 목록 ---");
        List<Customer> customers = customerService.GetAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("등록된 고객이 없습니다.");
            return;
        }
        for (Customer c : customers) {
            PrintCustomerDetail(c);
        }
    }

    /**
     * 고객 정보 수정을 처리합니다.
     */
    public void UpdateCustomer() {
        System.out.println("\n--- 고객 정보 수정 ---");
        long customerId = ConsoleInput.ReadLong("수정할 고객 ID: ");
        try {
            Customer existing = customerService.GetCustomerById(customerId);
            PrintCustomerDetail(existing);

            System.out.println("새로운 정보를 입력하세요:");
            Customer updated = PromptCustomerInput();
            updated.SetCustomerId(customerId);

            customerService.UpdateCustomer(updated);
            System.out.println("고객 정보가 성공적으로 수정되었습니다.");
        } catch (BusinessException InException) {
            System.out.println("[오류] " + InException.getMessage());
        }
    }

    /**
     * 고객 메뉴 항목을 콘솔에 출력하는 헬퍼 메서드입니다.
     */
    private void PrintCustomerMenuOptions() {
        System.out.println("\n[고객 관리 메뉴]");
        System.out.println("1. 신규 고객 등록");
        System.out.println("2. 고객 단건 조회");
        System.out.println("3. 고객 목록 조회");
        System.out.println("4. 고객 정보 수정");
        System.out.println("0. 메인 메뉴로 돌아가기");
    }

    /**
     * 고객 메뉴 번호 선택 분기를 담당하는 헬퍼 메서드입니다.
     *
     * @param InChoice 선택된 메뉴 번호
     * @return 메뉴 지속 여부
     */
    private boolean RouteCustomerChoice(int InChoice) {
        switch (InChoice) {
            case 1:
                RegisterCustomer();
                return true;
            case 2:
                FindCustomer();
                return true;
            case 3:
                ListCustomers();
                return true;
            case 4:
                UpdateCustomer();
                return true;
            case 0:
                return false;
            default:
                System.out.println("잘못된 번호입니다. 다시 선택해 주세요.");
                return true;
        }
    }

    /**
     * 고객 기본 정보를 콘솔로부터 입력받아 객체로 조합하는 헬퍼 메서드입니다.
     *
     * @return 입력 생성된 Customer 객체
     */
    private Customer PromptCustomerInput() {
        String name = ConsoleInput.ReadString("고객 이름: ");
        String email = ConsoleInput.ReadString("이메일: ");
        String phone = ConsoleInput.ReadString("연락처: ");
        String address = ConsoleInput.ReadString("주소: ");

        Customer customer = new Customer();
        customer.SetName(name);
        customer.SetEmail(email);
        customer.SetPhone(phone);
        customer.SetAddress(address);
        return customer;
    }

    /**
     * 고객 정보를 한 줄 또는 상세하게 콘솔에 출력하는 헬퍼 메서드입니다.
     *
     * @param InCustomer 출력할 Customer 객체
     */
    private void PrintCustomerDetail(Customer InCustomer) {
        if (InCustomer == null) {
            return;
        }
        System.out.println(String.format("ID: %d | 이름: %s | 연락처: %s | 이메일: %s | 주소: %s",
                InCustomer.GetCustomerId(),
                InCustomer.GetName(),
                InCustomer.GetPhone(),
                InCustomer.GetEmail(),
                InCustomer.GetAddress()));
    }
}
