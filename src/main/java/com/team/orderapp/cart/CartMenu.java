package com.team.orderapp.cart;

import com.team.orderapp.auth.LoginSession;
import com.team.orderapp.order.command.OrderCommandService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * 장바구니 화면입니다. 목록·합계를 보여 주고 수량 변경, 품목 삭제, 전체 비우기, 구매를 처리합니다.
 *
 * 사용 예: new CartMenu(scanner).Run();
 */
public class CartMenu {

    // 표 열 너비 (한글은 2칸으로 계산)
    private static final int COL_NO = 6;
    private static final int COL_NAME = 18;
    private static final int COL_PRICE = 10;
    private static final int COL_QUANTITY = 6;
    private static final int COL_AMOUNT = 10;
    private static final int TABLE_WIDTH =
            COL_NO + COL_NAME + COL_PRICE + COL_QUANTITY + COL_AMOUNT;

    private final Scanner scanner;
    private final CartService cartService;

    private final NumberFormat moneyFormat =
            NumberFormat.getNumberInstance(Locale.KOREA);

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

                if (scanner.nextLine().trim().equals("0")) {
                    return;
                }

                System.out.println("올바른 메뉴 번호를 입력해 주세요.");
                continue;
            }

            PrintMenu();

            String input = scanner.nextLine().trim();

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
                        System.out.println("올바른 메뉴 번호를 입력해 주세요.");
                }

            } catch (RuntimeException e) {
                PrintError(e);
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

        CartItem target = ReadItem(items, "수량을 변경할 품목 번호 (0: 취소) > ");

        if (target == null) {
            return;
        }

        int quantity = ReadNonNegativeInt("새 수량 (0: 삭제) > ");

        if (quantity == 0) {

            if (!ReadYesNo(target.getProductName() + "을(를) 장바구니에서 삭제하시겠습니까? (Y/N) > ")) {
                System.out.println("취소했습니다.");
                return;
            }

            cartService.RemoveItem(target.getCartItemId());
            System.out.println("품목을 삭제했습니다.");
            return;
        }

        cartService.ChangeQuantity(target.getCartItemId(), quantity);
        System.out.println("수량을 " + quantity + "개로 변경했습니다.");
    }


    // ============================================================
    // 2. 품목 삭제
    // ============================================================

    /**
     * 선택한 품목을 확인 후 삭제합니다.
     */
    private void RemoveItem(List<CartItem> items) {

        CartItem target = ReadItem(items, "삭제할 품목 번호 (0: 취소) > ");

        if (target == null) {
            return;
        }

        if (!ReadYesNo(target.getProductName() + "을(를) 장바구니에서 삭제하시겠습니까? (Y/N) > ")) {
            System.out.println("취소했습니다.");
            return;
        }

        cartService.RemoveItem(target.getCartItemId());
        System.out.println("품목을 삭제했습니다.");
    }


    // ============================================================
    // 3. 전체 비우기
    // ============================================================

    /**
     * 확인 후 장바구니를 모두 비웁니다.
     */
    private void ClearCart() {

        if (!ReadYesNo("장바구니를 모두 비우시겠습니까? (Y/N) > ")) {
            System.out.println("취소했습니다.");
            return;
        }

        cartService.ClearCart();
        System.out.println("장바구니를 비웠습니다.");
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

        String total = FormatMoney(cartService.CalculateTotalAmount(items)) + "원";

        if (!ReadYesNo("총 " + total + "을 결제합니다. 주문하시겠습니까? (Y/N) > ")) {
            System.out.println("취소했습니다.");
            return;
        }

        // 회원이면 customerId가, 비회원이면 null이 넘어가 주문 종류가 갈린다.
        String orderNo = new OrderCommandService()
                .Checkout(LoginSession.getCustomerId());

        System.out.println();
        System.out.println("주문이 완료되었습니다.");
        System.out.println("주문번호: " + orderNo);

        if (!LoginSession.IsLoggedIn()) {
            System.out.println("비회원 주문은 주문번호로만 조회·반품할 수 있으니 꼭 기록해 주세요.");
        }
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

        System.out.println();
        System.out.println("========================================");
        System.out.println("               장바구니(" + totalQuantity + ")");
        System.out.println("========================================");

        if (items.isEmpty()) {
            System.out.println("장바구니가 비어 있습니다.");
            return;
        }

        System.out.println(
                PadRight("번호", COL_NO)
                        + PadRight("상품명", COL_NAME)
                        + PadLeft("단가", COL_PRICE)
                        + PadLeft("수량", COL_QUANTITY)
                        + PadLeft("금액", COL_AMOUNT)
        );
        System.out.println("-".repeat(TABLE_WIDTH));

        for (int i = 0; i < items.size(); i++) {

            CartItem item = items.get(i);

            System.out.println(
                    PadRight(String.valueOf(i + 1), COL_NO)
                            + PadRight(Truncate(item.getProductName(), COL_NAME - 1), COL_NAME)
                            + PadLeft(FormatMoney(item.getPrice()), COL_PRICE)
                            + PadLeft(String.valueOf(item.getQuantity()), COL_QUANTITY)
                            + PadLeft(FormatMoney(item.getSubTotal()), COL_AMOUNT)
            );
        }

        System.out.println("-".repeat(TABLE_WIDTH));

        String total = FormatMoney(cartService.CalculateTotalAmount(items)) + "원";
        System.out.println("합계" + PadLeft(total, TABLE_WIDTH - DisplayWidth("합계")));
    }


    /**
     * 품목이 있을 때의 메뉴를 출력하는 헬퍼 메서드입니다.
     */
    private void PrintMenu() {

        System.out.println();
        System.out.println("1. 수량 변경");
        System.out.println("2. 품목 삭제");
        System.out.println("3. 전체 비우기");
        System.out.println("4. 구매");
        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }


    /**
     * 장바구니가 비어 있을 때의 메뉴를 출력하는 헬퍼 메서드입니다.
     */
    private void PrintEmptyMenu() {

        System.out.println();
        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }


    /**
     * 예외를 사용자용 오류 메시지로 출력하는 헬퍼 메서드입니다.
     *
     * 업무 규칙 위반(IllegalArgumentException)은 메시지를 그대로, 그 외(DB 오류 등)는 실패 사실을 알립니다.
     */
    private void PrintError(RuntimeException e) {

        if (e instanceof IllegalArgumentException) {
            System.out.println("[오류] " + e.getMessage());
            return;
        }

        System.out.println("[오류] 장바구니 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
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

            System.out.println("1 ~ " + items.size() + " 사이의 번호를 입력해 주세요.");
        }
    }


    /**
     * 0 이상의 정수를 입력받는 헬퍼 메서드입니다.
     */
    private int ReadNonNegativeInt(String message) {

        while (true) {

            System.out.print(message);

            try {
                int value = Integer.parseInt(scanner.nextLine().trim());

                if (value >= 0) {
                    return value;
                }

            } catch (NumberFormatException e) {
                // 아래 안내 문구로 재입력
            }

            System.out.println("0 이상의 숫자를 입력해 주세요.");
        }
    }


    /**
     * Y/N을 입력받는 헬퍼 메서드입니다.
     *
     * @return Y면 true, N이면 false
     */
    private boolean ReadYesNo(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("Y")) {
                return true;
            }

            if (input.equals("N")) {
                return false;
            }

            System.out.println("Y 또는 N을 입력해 주세요.");
        }
    }


    // ============================================================
    // 표 정렬 Helper
    // ============================================================

    /**
     * 금액을 천 단위 콤마 형식(예: 14,000)으로 바꾸는 헬퍼 메서드입니다.
     */
    private String FormatMoney(BigDecimal amount) {
        return moneyFormat.format(amount);
    }


    /**
     * 터미널에서 차지하는 칸 수를 계산하는 헬퍼 메서드입니다. 한글은 2칸, 나머지는 1칸입니다.
     */
    private int DisplayWidth(String text) {

        int width = 0;

        for (char c : text.toCharArray()) {
            width += IsWide(c) ? 2 : 1;
        }

        return width;
    }


    /**
     * 한글처럼 터미널에서 2칸을 차지하는 문자인지 확인하는 헬퍼 메서드입니다.
     */
    private boolean IsWide(char c) {
        return (c >= '가' && c <= '힣')   // 한글 음절
                || (c >= 'ᄀ' && c <= 'ᇿ')  // 한글 자모
                || (c >= '㄰' && c <= '㆏')  // 한글 호환 자모
                || (c >= '一' && c <= '鿿')  // 한자
                || (c >= '！' && c <= '｠'); // 전각 기호
    }


    /**
     * 표시 너비가 width가 되도록 오른쪽을 공백으로 채우는 헬퍼 메서드입니다.
     */
    private String PadRight(String text, int width) {
        return text + " ".repeat(Math.max(0, width - DisplayWidth(text)));
    }


    /**
     * 표시 너비가 width가 되도록 왼쪽을 공백으로 채우는 헬퍼 메서드입니다.
     */
    private String PadLeft(String text, int width) {
        return " ".repeat(Math.max(0, width - DisplayWidth(text))) + text;
    }


    /**
     * 표시 너비가 maxWidth를 넘으면 잘라서 ".."을 붙이는 헬퍼 메서드입니다.
     */
    private String Truncate(String text, int maxWidth) {

        if (DisplayWidth(text) <= maxWidth) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int width = 0;

        for (char c : text.toCharArray()) {

            int charWidth = IsWide(c) ? 2 : 1;

            if (width + charWidth > maxWidth - 2) {
                break;
            }

            result.append(c);
            width += charWidth;
        }

        return result + "..";
    }
}
