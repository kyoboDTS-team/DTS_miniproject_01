package com.team.orderapp.app;

import com.team.orderapp.auth.LoginSession;
import com.team.orderapp.common.ConsoleUi;
import com.team.orderapp.customer.CustomerMenu;
import com.team.orderapp.product.ProductCommandMenu;
import com.team.orderapp.stock.StockMenu;
import com.team.orderapp.order.query.OrderQueryMenu;
import com.team.orderapp.order.query.OrderAdminMenu;
import com.team.orderapp.report.ReportMenu;
import com.team.orderapp.export.ProductCsvMenu;


import java.util.Scanner;

/**
 * 관리자 전용 메인 메뉴
 *
 * 각 관리 기능이 완성되면
 * switch문의 TODO 위치에 연결하면 됩니다.
 */
public class AdminMenu {

    private final Scanner scanner;

    // 로그인한 관리자 PK
    private final Long adminUserId;

    // 로그인한 관리자 이메일
    private final String email;


    public AdminMenu(
            Scanner scanner,
            Long adminUserId,
            String email
    ) {
        this.scanner = scanner;
        this.adminUserId = adminUserId;
        this.email = email;
    }


    // ============================================================
    // 관리자 메인 메뉴 실행
    // ============================================================

    public void Run() {

        while (true) {

            PrintMenu();

            String input = ConsoleUi.Choice(scanner.nextLine());

            switch (input) {

                case "1":

                    ProductCommandMenu productMenu = new ProductCommandMenu(scanner);

                    productMenu.Run();
                    break;

                case "2":
                    StockMenu stockMenu = new StockMenu(scanner, adminUserId);
                    stockMenu.Run();
                    break;

                case "3":
                    // TODO: 회원 관리 메뉴 연결
                    CustomerMenu customerMenu = new CustomerMenu(scanner);

                    customerMenu.DisplayMenu();
                    break;

                case "4":
                    // 관리자 주문 / 반품 관리
                    OrderAdminMenu orderAdminMenu = new OrderAdminMenu(scanner);

                    orderAdminMenu.Run();
                    break;

                case "5":
                    ReportMenu reportMenu = new ReportMenu(scanner);

                    reportMenu.Run();
                    break;

                case "6":
                    ProductCsvMenu csvMenu = new ProductCsvMenu(scanner);

                    csvMenu.Run();
                    break;

                case "7":

                    LoginSession.Logout();
                    ConsoleUi.ClearScreen();
                    ConsoleUi.Success("로그아웃되었습니다.");

                    // 상위 GuestMenu / 로그인 흐름으로 복귀
                    return;

                case "0":
                    ConsoleUi.ClearScreen();
                    ConsoleUi.Info("프로그램을 종료합니다.");
                    return;

                default:
                    ConsoleUi.InvalidMenu();
                    ConsoleUi.PressEnter(scanner);
            }
        }
    }


    // ============================================================
    // 화면 출력
    // ============================================================

    private void PrintMenu() {

        ConsoleUi.ClearScreen();

        ConsoleUi.AdminHeader(
                ConsoleUi.InfoLine("관리자", email)
        );

        ConsoleUi.Section("관리");
        ConsoleUi.MenuItem("01", "상품 / 카테고리 관리");
        ConsoleUi.MenuItem("02", "재고 / 시리얼 관리");
        ConsoleUi.MenuItem("03", "회원 관리");
        ConsoleUi.MenuItem("04", "주문 / 반품 관리");

        ConsoleUi.Section("리포트");
        ConsoleUi.MenuItem("05", "통계");
        ConsoleUi.MenuItem("06", "CSV 저장 / 불러오기");

        ConsoleUi.Section("시스템");
        ConsoleUi.MenuItem("07", "로그아웃");
        ConsoleUi.MenuItem("00", "종료");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }
}