package com.team.orderapp.app;

import com.team.orderapp.product.ProductCommandMenu;
import com.team.orderapp.stock.StockMenu;
import com.team.orderapp.order.query.OrderQueryMenu;
import com.team.orderapp.order.query.OrderAdminMenu;

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

            String input = scanner.nextLine().trim();

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
                    System.out.println("[TODO] 회원 관리");
                    break;

                case "4":
                    // 관리자 주문 / 반품 관리
                    OrderAdminMenu orderAdminMenu = new OrderAdminMenu(scanner);

                    orderAdminMenu.Run();
                    break;

                case "5":
                    // TODO: 관리자 통계 메뉴 연결
                    System.out.println("[TODO] 통계");
                    break;

                case "6":
                    // TODO: CSV 저장 / 불러오기 메뉴 연결
                    System.out.println("[TODO] CSV 저장 / 불러오기");
                    break;

                case "7":
                    // TODO: 관리자 LoginSession 초기화
                    System.out.println("로그아웃합니다.");

                    // 상위 GuestMenu / 로그인 흐름으로 복귀
                    return;

                case "0":
                    System.out.println("프로그램을 종료합니다.");
                    return;

                default:
                    System.out.println(
                            "올바른 메뉴 번호를 입력해 주세요."
                    );
            }
        }
    }


    // ============================================================
    // 화면 출력
    // ============================================================

    private void PrintMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("             TERMINAL MARKET");

        // 로그인한 관리자 표시
        System.out.println("[관리자] " + email);

        System.out.println("========================================");
        System.out.println("1. 상품 / 카테고리 관리");
        System.out.println("2. 재고 / 시리얼 관리");
        System.out.println("3. 회원 관리");
        System.out.println("4. 주문 / 반품 관리");
        System.out.println("5. 통계");
        System.out.println("6. CSV 저장 / 불러오기");
        System.out.println("7. 로그아웃");
        System.out.println("0. 종료");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }
}