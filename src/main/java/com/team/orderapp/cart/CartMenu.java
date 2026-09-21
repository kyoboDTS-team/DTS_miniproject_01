package com.team.orderapp.cart;

import com.team.orderapp.auth.LoginSession;
import com.team.orderapp.common.ConsoleUi;
import com.team.orderapp.order.command.OrderCommandService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * 장바구니 화면입니다. 목록·합계를 보여 주고 수량 변경, 품목 삭제, 전체 비우기, 구매를 처리합니다.
 *
 * 사용 예: new CartMenu(scanner).Run();
 */
public class CartMenu {

    // 표 열 너비 (한글은 2칸으로 계산)
    private static final int COL_NO = 6;
    private static final int COL_NAME = 20;
    private static final int COL_PRICE = 10;
    private static final int COL_QUANTITY = 6;
    private static final int COL_AMOUNT = 12;
    private static final int TABLE_WIDTH =
            COL_NO + COL_NAME + COL_PRICE + COL_QUANTITY + COL_AMOUNT;

    private final Scanner scanner;
    private final CartService cartService;

    /**
     * 장바구니 화면을 생성합니다.
     *
     * @param scanner 프로그램 전체에서 공유하는 Scanner
     */
    public CartMenu(Scanner scanner) {
        this.scanner = scanner;
        this.cartService = new CartService();
    }


    // ============================================================
    // 장바구니 메인
    // ============================================================

    /**
     * 장바구니 화면을 실행합니다. 0을 입력하면 이전 메뉴로 돌아갑니다.
     */
    public void Run() {

        while (true) {

            List<CartItem> items;

            try {
                items = cartService.GetItems();
            } catch (RuntimeException e) {
                PrintError(e);
                return;
            }

            PrintCart(items);

            if (items.isEmpty()) {
                PrintEmptyMenu();

                if (ConsoleUi.Choice(scanner.nextLine()).equals("0")) {
                    return;
                }

                ConsoleUi.InvalidMenu();
                ConsoleUi.PressEnter(scanner);
                continue;
            }

            PrintMenu();

            String input = ConsoleUi.Choice(scanner.nextLine());

            try {

                switch (input) {

                    case "1":
                        ChangeQuantity(items);
                        break;

                    case "2":
                        RemoveItem(items);
                        break;

                    case "3":
                        ClearCart();
                        break;

                    case "4":
                        Purchase();
                        break;

                    case "0":
                        return;

                    default:
                        ConsoleUi.InvalidMenu();
                        ConsoleUi.PressEnter(scanner);
                }

            } catch (RuntimeException e) {
                PrintError(e);
                ConsoleUi.PressEnter(scanner);
            }
        }
    }


    // ============================================================
    // 1. 수량 변경
    // ============================================================

    /**
     * 선택한 품목의 수량을 변경합니다. 0을 입력하면 삭제 확인 후 삭제합니다.
     */
    private void ChangeQuantity(List<CartItem> items) {

        CartItem target = ReadItem(items, "수량을 변경할 품목 번호 (0: 취소)");

        if (target == null) {
            return;
        }

        int quantity = ReadNonNegativeInt("새 수량 (0: 삭제)");

        if (quantity == 0) {

            if (!ReadYesNo(target.getProductName() + "을(를) 장바구니에서 삭제하시겠습니까?")) {
                ConsoleUi.Cancelled();
                ConsoleUi.PressEnter(scanner);
                return;
            }

            cartService.RemoveItem(target.getCartItemId());
            ConsoleUi.Success("품목을 삭제했습니다.");
            ConsoleUi.PressEnter(scanner);
            return;
        }

        cartService.ChangeQuantity(target.getCartItemId(), quantity);
        ConsoleUi.Success("수량을 " + quantity + "개로 변경했습니다.");
        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 2. 품목 삭제
    // ============================================================

    /**
     * 선택한 품목을 확인 후 삭제합니다.
     */
    private void RemoveItem(List<CartItem> items) {

        CartItem target = ReadItem(items, "삭제할 품목 번호 (0: 취소)");

        if (target == null) {
            return;
        }

        if (!ReadYesNo(target.getProductName() + "을(를) 장바구니에서 삭제하시겠습니까?")) {
            ConsoleUi.Cancelled();
            ConsoleUi.PressEnter(scanner);
            return;
        }

        cartService.RemoveItem(target.getCartItemId());
        ConsoleUi.Success("품목을 삭제했습니다.");
        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 3. 전체 비우기
    // ============================================================

    /**
     * 확인 후 장바구니를 모두 비웁니다.
     */
    private void ClearCart() {

        ConsoleUi.Warn("장바구니를 모두 비우면 되돌릴 수 없습니다.");

        if (!ReadYesNo("장바구니를 모두 비우시겠습니까?")) {
            ConsoleUi.Cancelled();
            ConsoleUi.PressEnter(scanner);
            return;
        }

        cartService.ClearCart();
        ConsoleUi.Success("장바구니를 비웠습니다.");
        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 4. 구매
    // ============================================================

    /**
     * 장바구니에 담긴 상품을 주문합니다.
     *
     * 실제 결제 금액은 주문 시점에 상품을 다시 조회해 계산하므로, 화면에 보여 준 합계와
     * 다를 수 있습니다(담은 뒤 가격이 바뀐 경우). 재고 부족·판매 중지 같은 실패는
     * 예외로 올라와 Run()의 PrintError가 처리하며, 이때 장바구니는 그대로 남습니다.
     *
     * 주문에 성공하면 OrderCommandService가 장바구니를 비웁니다.
     */
    private void Purchase() {

        List<CartItem> items = cartService.GetItems();

        if (items.isEmpty()) {
            ConsoleUi.Warn("장바구니가 비어있습니다.");
            ConsoleUi.PressEnter(scanner);
            return;
        }

        BigDecimal total = cartService.CalculateTotalAmount(items);

        PrintCheckout(items, total);

        if (!ReadYesNo("주문하시겠습니까?")) {
            ConsoleUi.Cancelled();
            ConsoleUi.PressEnter(scanner);
            return;
        }

        // 회원이면 customerId가, 비회원이면 null이 넘어가 주문 종류가 갈린다.
        String orderNo = new OrderCommandService()
                .Checkout(LoginSession.getCustomerId());

        PrintOrderComplete(orderNo, total);
    }


    /**
     * 주문 확인 화면을 출력하는 헬퍼 메서드입니다.
     */
    private void PrintCheckout(List<CartItem> items, BigDecimal total) {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("CHECKOUT", "주문 확인");

        System.out.println();

        int totalQuantity = 0;

        for (CartItem item : items) {

            totalQuantity += item.getQuantity();

            String line = ConsoleUi.Truncate(item.getProductName(), COL_NAME - 1)
                    + "  " + ConsoleUi.Money(item.getPrice()) + "원"
                    + " × " + item.getQuantity();

            System.out.println(
                    ConsoleUi.INDENT
                            + ConsoleUi.PadRight(line, TABLE_WIDTH - 14)
                            + ConsoleUi.PadLeft(ConsoleUi.Money(item.getSubTotal()) + "원", 12)
            );
        }

        ConsoleUi.Divider(TABLE_WIDTH);
        System.out.println(
                ConsoleUi.INDENT
                        + ConsoleUi.PadRight("총 수량", TABLE_WIDTH - 14)
                        + ConsoleUi.PadLeft(ConsoleUi.Yellow(String.valueOf(totalQuantity)), 12)
        );
        System.out.println(
                ConsoleUi.INDENT
                        + ConsoleUi.PadRight("주문 금액", TABLE_WIDTH - 14)
                        + ConsoleUi.PadLeft(ConsoleUi.Amount(total), 12)
        );
        ConsoleUi.Divider(TABLE_WIDTH);

        System.out.println();
        ConsoleUi.Warn("구매 시점의 최신 가격·재고로 다시 계산되므로 금액이 달라질 수 있습니다.");
        System.out.println();
        ConsoleUi.YesNoOptions("주문 확정", "취소");
        System.out.println();
    }


    /**
     * 주문 완료 화면을 출력하는 헬퍼 메서드입니다.
     */
    private void PrintOrderComplete(String orderNo, BigDecimal total) {

        ConsoleUi.ClearScreen();
        ConsoleUi.CompleteBox(
                "ORDER COMPLETE",
                "주문 완료",
                ConsoleUi.InfoLine("주문번호", ConsoleUi.Cyan(orderNo)),
                ConsoleUi.InfoLine("주문금액", ConsoleUi.Amount(total))
        );

        System.out.println();
        ConsoleUi.Success("주문이 정상적으로 완료되었습니다.");

        if (!LoginSession.IsLoggedIn()) {
            ConsoleUi.Warn("비회원은 주문 조회·반품에 주문번호가 필요하니 반드시 보관해 주세요.");
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 화면 출력
    // ============================================================

    /**
     * 장바구니 헤더, 품목 표, 합계를 출력하는 헬퍼 메서드입니다.
     */
    private void PrintCart(List<CartItem> items) {

        int totalQuantity = 0;
        for (CartItem item : items) {
            totalQuantity += item.getQuantity();
        }

        BigDecimal total = items.isEmpty()
                ? BigDecimal.ZERO
                : cartService.CalculateTotalAmount(items);

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader(
                "CART",
                "총 수량 " + ConsoleUi.Yellow(String.valueOf(totalQuantity)),
                ConsoleUi.Amount(total)
        );

        if (items.isEmpty()) {
            System.out.println();
            ConsoleUi.Warn("장바구니가 비어있습니다.");
            return;
        }

        System.out.println();
        System.out.println(
                ConsoleUi.Cyan(
                        ConsoleUi.PadRight("번호", COL_NO)
                                + ConsoleUi.PadRight("상품명", COL_NAME)
                                + ConsoleUi.PadLeft("단가", COL_PRICE)
                                + ConsoleUi.PadLeft("수량", COL_QUANTITY)
                                + ConsoleUi.PadLeft("금액", COL_AMOUNT)
                )
        );
        ConsoleUi.Divider(TABLE_WIDTH);

        for (int i = 0; i < items.size(); i++) {

            CartItem item = items.get(i);

            System.out.println(
                    ConsoleUi.PadRight(String.format("%02d", i + 1), COL_NO)
                            + ConsoleUi.PadRight(
                                    ConsoleUi.Truncate(item.getProductName(), COL_NAME - 1),
                                    COL_NAME)
                            + ConsoleUi.PadLeft(ConsoleUi.Money(item.getPrice()), COL_PRICE)
                            + ConsoleUi.PadLeft(String.valueOf(item.getQuantity()), COL_QUANTITY)
                            + ConsoleUi.PadLeft(ConsoleUi.Money(item.getSubTotal()), COL_AMOUNT)
            );
        }

        ConsoleUi.Divider(TABLE_WIDTH);

        System.out.println(
                ConsoleUi.PadLeft("합계 " + ConsoleUi.Amount(total), TABLE_WIDTH)
        );
    }


    /**
     * 품목이 있을 때의 메뉴를 출력하는 헬퍼 메서드입니다.
     */
    private void PrintMenu() {

        System.out.println();
        ConsoleUi.Option("1", "수량 변경");
        ConsoleUi.Option("2", "품목 삭제");
        ConsoleUi.Option("3", "전체 비우기");
        System.out.println();
        ConsoleUi.Option("4", "구매하기");
        ConsoleUi.Option("0", "이전");
        System.out.println();
        ConsoleUi.Prompt("선택");
    }


    /**
     * 장바구니가 비어 있을 때의 메뉴를 출력하는 헬퍼 메서드입니다.
     */
    private void PrintEmptyMenu() {

        System.out.println();
        ConsoleUi.Option("0", "이전");
        System.out.println();
        ConsoleUi.Prompt("선택");
    }


    /**
     * 예외를 사용자용 오류 메시지로 출력하는 헬퍼 메서드입니다.
     *
     * 업무 규칙 위반(IllegalArgumentException)은 메시지를 그대로, 그 외(DB 오류 등)는 실패 사실을 알립니다.
     */
    private void PrintError(RuntimeException e) {

        if (e instanceof IllegalArgumentException) {
            ConsoleUi.Error(e.getMessage());
            return;
        }

        ConsoleUi.Error("장바구니 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
    }


    // ============================================================
    // 입력
    // ============================================================

    /**
     * 화면의 품목 번호(1부터)를 입력받아 해당 CartItem을 반환하는 헬퍼 메서드입니다.
     *
     * @return 선택한 품목, 0을 입력하면 null
     */
    private CartItem ReadItem(List<CartItem> items, String message) {

        while (true) {

            int number = ReadNonNegativeInt(message);

            if (number == 0) {
                return null;
            }

            if (number <= items.size()) {
                return items.get(number - 1);
            }

            ConsoleUi.Error("1 ~ " + items.size() + " 사이의 번호를 입력해 주세요.");
        }
    }


    /**
     * 0 이상의 정수를 입력받는 헬퍼 메서드입니다.
     */
    private int ReadNonNegativeInt(String message) {

        while (true) {

            ConsoleUi.Prompt(message);

            try {
                int value = Integer.parseInt(scanner.nextLine().trim());

                if (value >= 0) {
                    return value;
                }

            } catch (NumberFormatException e) {
                // 아래 안내 문구로 재입력
            }

            ConsoleUi.Error("0 이상의 숫자를 입력해 주세요.");
        }
    }


    /**
     * Y/N을 입력받는 헬퍼 메서드입니다.
     *
     * @return Y면 true, N이면 false
     */
    private boolean ReadYesNo(String message) {

        while (true) {

            ConsoleUi.Prompt(message + " (Y/N)");

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("Y")) {
                return true;
            }

            if (input.equals("N")) {
                return false;
            }

            ConsoleUi.Error("Y 또는 N을 입력해 주세요.");
        }
    }
}
