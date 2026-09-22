package com.team.orderapp.app;

import com.team.orderapp.auth.LoginSession;
import com.team.orderapp.cart.CartMenu;
import com.team.orderapp.cart.CartService;
import com.team.orderapp.common.ConsoleUi;
import com.team.orderapp.customer.MyInfoMenu;
import com.team.orderapp.order.command.OrderCommandMenu;
import com.team.orderapp.order.query.OrderQueryMenu;
import com.team.orderapp.product.ProductMenu;

import java.util.Scanner;

/**
 * 로그인한 일반 회원 메뉴
 */
public class MemberMenu {

    private final Scanner scanner;

    // 로그인한 회원 이메일 (내정보조회/수정에서 이메일을 바꾸면 최신값으로 갱신됨)
    private String email;

    private final ProductMenu productMenu;

    private final OrderQueryMenu orderQueryMenu;

    private final CartMenu cartMenu;

    private final OrderCommandMenu orderCommandMenu;

    private final CartService cartService;
    public MemberMenu(
            Scanner scanner,
            String email
    ) {

        this.scanner = scanner;
        this.email = email;
        this.productMenu = new ProductMenu(scanner);
        this.orderQueryMenu = new OrderQueryMenu(scanner);
        this.cartMenu = new CartMenu(scanner);
        this.orderCommandMenu = new OrderCommandMenu(scanner);
        this.cartService = new CartService();

    }


    // ============================================================
    // 회원 메인 메뉴 실행
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
                    orderQueryMenu.ShowMyOrders(LoginSession.getCustomerId());

                    break;

                case "5":

                    orderCommandMenu.Run();

                    break;

                case "6":
                    MyInfoMenu myInfoMenu = new MyInfoMenu(scanner, email);
                    if (!myInfoMenu.Run()) {
                        // 회원 탈퇴로 계정이 사라짐 - GuestMenu 또는 로그인 흐름으로 돌아가도록
                        // 상위 호출부에서 처리
                        return;
                    }
                    // 이메일을 바꿨을 수도 있으니, 화면 상단 표시용 email도 최신값으로 갱신
                    email = myInfoMenu.GetEmail();
                    break;

                case "7":

                    // 로그아웃 시 LoginSession 정리
                    // 장바구니도 함께 초기화
                    LoginSession.Logout();
                    ConsoleUi.ClearScreen();
                    ConsoleUi.Success("로그아웃되었습니다.");

                    // GuestMenu 또는 로그인 흐름으로 돌아가도록
                    // 상위 호출부에서 처리
                    return;

                case "0":
                    ConsoleUi.ClearScreen();
                    ConsoleUi.Info("프로그램을 종료합니다.");

                    // 현재는 메뉴 종료
                    // 전체 프로그램 종료 정책은 Main과 통합하면서 결정
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

        int cartCount = cartService.GetCartCount();

        ConsoleUi.ClearScreen();

        ConsoleUi.MainHeader(
                ConsoleUi.InfoLine("회원", email),
                ConsoleUi.CartLine(cartCount)
        );

        ConsoleUi.Section("상품");
        ConsoleUi.MenuItem("01", "전체 상품 조회");
        ConsoleUi.MenuItem("02", "상품 조건 검색");
        ConsoleUi.MenuItem("03", "장바구니", ConsoleUi.CartBadge(cartCount));

        ConsoleUi.Section("마이페이지");
        ConsoleUi.MenuItem("04", "주문 목록 / 상세");
        ConsoleUi.MenuItem("05", "반품");
        ConsoleUi.MenuItem("06", "내 정보 조회 / 수정");

        ConsoleUi.Section("시스템");
        ConsoleUi.MenuItem("07", "로그아웃");
        ConsoleUi.MenuItem("00", "종료");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }

}