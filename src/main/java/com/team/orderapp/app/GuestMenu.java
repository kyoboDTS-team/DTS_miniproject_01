package com.team.orderapp.app;

import com.team.orderapp.auth.LoginService;
import com.team.orderapp.auth.LoginSession;
import com.team.orderapp.auth.SignupMenu;
import com.team.orderapp.order.query.OrderQueryMenu;
import com.team.orderapp.product.ProductMenu;

import java.util.Scanner;

/**
 * 비회원 / 최초 접속 사용자 메뉴
 *
 * 담당 기능들이 완성되면 각 TODO 위치에
 * 해당 Menu 또는 Service를 연결하면 됩니다.
 */
public class GuestMenu {

    private final Scanner scanner;

    private final ProductMenu productMenu;

    private final OrderQueryMenu orderQueryMenu;

    public GuestMenu(Scanner scanner) {
        this.scanner = scanner;
        this.productMenu = new ProductMenu(scanner);
        this.orderQueryMenu = new OrderQueryMenu(scanner);
    }


    // ============================================================
    // 비회원 메인 메뉴 실행
    // ============================================================

    public void Run() {

        while (true) {

            PrintMenu();

            String input = scanner.nextLine().trim();

            switch (input) {

                case "1":
                    //작성자: 박형준
                    productMenu.ShowAllProducts();

                    break;

                case "2":
                    //작성자: 박형준
                    productMenu.ShowProductsByCondition();

                    break;

                case "3":
                    // TODO: 장바구니 메뉴 연결
                    System.out.println("[TODO] 장바구니 보기");
                    break;

                case "4":
                    //작성자: 박형준
                    orderQueryMenu.ShowGuestOrder();

                    break;

                case "5":
                    // TODO: 로그인 메뉴 연결
                    //
                    // 로그인 성공 후 role에 따라서
                    // CUSTOMER -> MemberMenu
                    // ADMIN    -> AdminMenu
                    System.out.println("[TODO] 로그인");
                    break;

                case "6":
                    new SignupMenu(scanner).SignUp();
                    // 가입 후 자동 로그인에 성공했으면 회원 메뉴로 이동
                    if (LoginSession.IsLoggedIn()) {
                        EnterMemberMenu();
                    }
                    break;

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
    // 회원 메뉴 진입
    // ============================================================

    /**
     * 로그인한 회원의 이메일을 넘겨 회원 메뉴를 실행하고, 메뉴에서 돌아오면 로그아웃 처리합니다.
     */
    private void EnterMemberMenu() {

        new MemberMenu(scanner, LoginSession.getEmail()).Run();

        new LoginService().Logout();
    }


    // ============================================================
    // 화면 출력
    // ============================================================

    private void PrintMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("             TERMINAL MARKET");
        System.out.println("----------------------------------------");

        // TODO: Cart 구현 완료 후 실제 장바구니 총 수량으로 변경
        System.out.println("장바구니(" + GetCartCount() + ")");

        System.out.println("========================================");
        System.out.println("1. 상품 전체 조회");
        System.out.println("2. 상품 조건 조회 (카테고리 / 가격)");
        System.out.println("3. 장바구니 보기");
        System.out.println("4. 비회원 주문 조회");
        System.out.println("5. 로그인");
        System.out.println("6. 회원가입");
        System.out.println("0. 종료");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }


    /**
     * 현재 장바구니 총 상품 수량
     *
     * Cart 구현 전까지는 0 반환
     */
    private int GetCartCount() {

        // TODO: 나중에 CartService 또는 Cart에서 총 수량 가져오기
        return 0;
    }
}