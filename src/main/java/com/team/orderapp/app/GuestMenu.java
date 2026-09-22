package com.team.orderapp.app;

import com.team.orderapp.auth.*;
import com.team.orderapp.cart.CartMenu;
import com.team.orderapp.cart.CartService;
import com.team.orderapp.common.ConsoleUi;
import com.team.orderapp.order.command.OrderCommandMenu;
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

            String input = ConsoleUi.Choice(scanner.nextLine());

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

                case "7":
                    new OrderCommandMenu(scanner).Run();
                    break;

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

        int cartCount = cartService.GetCartCount();

        ConsoleUi.ClearScreen();

        ConsoleUi.MainHeader(
                ConsoleUi.InfoLine("비회원", null),
                ConsoleUi.CartLine(cartCount)
        );

        ConsoleUi.Section("상품");
        ConsoleUi.MenuItem("01", "전체 상품 조회");
        ConsoleUi.MenuItem("02", "상품 조건 검색");
        ConsoleUi.MenuItem("03", "장바구니", ConsoleUi.CartBadge(cartCount));

        ConsoleUi.Section("주문");
        ConsoleUi.MenuItem("04", "비회원 주문 조회");
        ConsoleUi.MenuItem("07", "반품");

        ConsoleUi.Section("계정");
        ConsoleUi.MenuItem("05", "로그인");
        ConsoleUi.MenuItem("06", "회원가입");

        ConsoleUi.Section("시스템");
        ConsoleUi.MenuItem("00", "종료");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }


}