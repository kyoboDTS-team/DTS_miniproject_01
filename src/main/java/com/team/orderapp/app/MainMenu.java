package com.team.orderapp.app;

import com.team.orderapp.auth.LoginMenu;
import com.team.orderapp.common.ConsoleInput;
import com.team.orderapp.customer.CustomerMenu;
import com.team.orderapp.order.command.OrderCommandMenu;
import com.team.orderapp.order.query.OrderQueryMenu;
import com.team.orderapp.product.ProductMenu;
import com.team.orderapp.report.ReportMenu;
import com.team.orderapp.stock.StockMenu;

/**
 * 시스템의 메인 메뉴 및 흐름 제어를 담당하는 클래스입니다.
 */
public class MainMenu {

    private final LoginMenu loginMenu;
    private final CustomerMenu customerMenu;
    private final ProductMenu productMenu;
    private final StockMenu stockMenu;
    private final OrderCommandMenu orderCommandMenu;
    private final OrderQueryMenu orderQueryMenu;
    private final ReportMenu reportMenu;

    public MainMenu() {
        this.loginMenu = new LoginMenu();
        this.customerMenu = new CustomerMenu();
        this.productMenu = new ProductMenu();
        this.stockMenu = new StockMenu();
        this.orderCommandMenu = new OrderCommandMenu();
        this.orderQueryMenu = new OrderQueryMenu();
        this.reportMenu = new ReportMenu();
    }

    /**
     * 메인 루프를 실행합니다.
     */
    public void Run() {
        boolean isRunning = true;
        while (isRunning) {
            DisplayMenuOptions();
            int choice = ConsoleInput.ReadInt("메뉴 번호를 선택하세요: ");
            isRunning = RouteMenuChoice(choice);
        }
        DisplayExitMessage();
    }

    /**
     * 메인 메뉴 목록을 화면에 출력하는 헬퍼 메서드입니다.
     */
    private void DisplayMenuOptions() {
        System.out.println("\n[메인 메뉴]");
        System.out.println("1. 로그인/사용자 관리");
        System.out.println("2. 고객 관리");
        System.out.println("3. 상품 관리");
        System.out.println("4. 재고 관리");
        System.out.println("5. 주문 접수 및 변경 (Command)");
        System.out.println("6. 주문 조회 및 상세 (Query)");
        System.out.println("7. 통계 및 보고서 (Report)");
        System.out.println("0. 프로그램 종료");
    }

    /**
     * 사용자의 메뉴 선택을 분기 처리하는 헬퍼 메서드입니다.
     *
     * @param InChoice 사용자가 선택한 메뉴 번호
     * @return 프로그램 지속 여부 (false이면 루프 종료)
     */
    private boolean RouteMenuChoice(int InChoice) {
        switch (InChoice) {
            case 1:
                loginMenu.DisplayMenu();
                return true;
            case 2:
                customerMenu.DisplayMenu();
                return true;
            case 3:
                productMenu.DisplayMenu();
                return true;
            case 4:
                stockMenu.DisplayMenu();
                return true;
            case 5:
                orderCommandMenu.DisplayMenu();
                return true;
            case 6:
                orderQueryMenu.DisplayMenu();
                return true;
            case 7:
                reportMenu.DisplayMenu();
                return true;
            case 0:
                return false;
            default:
                HandleInvalidInput(InChoice);
                return true;
        }
    }

    /**
     * 잘못된 입력 선택을 처리하는 헬퍼 메서드입니다.
     *
     * @param InChoice 잘못 입력된 메뉴 번호
     */
    private void HandleInvalidInput(int InChoice) {
        System.out.println("잘못된 입력입니다: " + InChoice + ". 다시 선택해 주세요.");
    }

    /**
     * 프로그램 종료 메시지를 출력하는 헬퍼 메서드입니다.
     */
    private void DisplayExitMessage() {
        System.out.println("프로그램을 종료합니다. 이용해 주셔서 감사합니다.");
    }
}
