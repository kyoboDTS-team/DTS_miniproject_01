package com.team.orderapp.app;

import com.team.orderapp.auth.*;
import com.team.orderapp.cart.CartMenu;
import com.team.orderapp.cart.CartService;
import com.team.orderapp.order.query.OrderQueryMenu;
import com.team.orderapp.product.ProductMenu;

import java.util.Optional;
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

    private final CartService cartService;

    private final LoginMenu loginMenu;

    private final CartMenu cartMenu;
    public GuestMenu(Scanner scanner) {
        this.scanner = scanner;
        this.productMenu = new ProductMenu(scanner);
        this.orderQueryMenu = new OrderQueryMenu(scanner);
        this.cartService = new CartService();
        this.cartMenu = new CartMenu(scanner);
        this.loginMenu = new LoginMenu(scanner);
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

                    cartMenu.Run();
                    break;

                case "4":
                    //작성자: 박형준
                    orderQueryMenu.ShowGuestOrder();

                    break;

                case "5":
                {
                    UserRole user = loginMenu.Run();

                    if (user == null) break;

                    //회원이면 회원메뉴로 이동
                    if (user == UserRole.CUSTOMER) {
                        EnterMemberMenu();
                    }
                    //관리자면 관리자로 이동
                    else if (user == UserRole.ADMIN) {
                        EnterAdminMenu();
                    }
                }
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

    private void EnterAdminMenu() {

        new AdminMenu(scanner, LoginSession.getUserId(), LoginSession.getEmail()).Run();

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
        System.out.println("장바구니(" + cartService.GetCartCount() + ")");

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


}