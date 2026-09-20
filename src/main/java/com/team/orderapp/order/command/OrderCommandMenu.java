package com.team.orderapp.order.command;

import com.team.orderapp.auth.LoginSession;

import java.util.Scanner;

/**
 * 주문 반품을 처리하는 콘솔 화면입니다. (회원 메뉴 5번)
 *
 * 반품 권한은 LoginSession에서 꺼내 OrderCommandService에 넘깁니다.
 * 회원은 자기 주문만, 관리자는 모든 주문을, 비회원은 주문번호를 아는 주문을 반품할 수 있습니다.
 *
 * 주문 생성(구매)은 장바구니 화면에서 이어지므로 CartMenu에 있습니다.
 */
public class OrderCommandMenu {

    /*
     * 작업: 반품 요청 콘솔 화면
     *
     * 작업자: 김상진
     */

    private static final String CANCEL_INPUT = "0";

    // common/ConsoleInput이 아직 구현되지 않아 Scanner를 직접 사용. 완성되면 교체 필요.
    private final Scanner scanner;

    private final OrderCommandService orderCommandService = new OrderCommandService();

    /**
     * 상위 화면에서 쓰던 Scanner를 그대로 물려받습니다.
     *
     * @param scanner 상위 화면의 Scanner
     */
    public OrderCommandMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 반품 화면을 실행합니다. 주문번호를 입력받아 확인을 거친 뒤 반품을 처리합니다.
     * 반품은 주문 전체 단위로 이루어지며, 일부 품목만 반품할 수는 없습니다.
     */
    public void Run() {

        System.out.println();
        System.out.println("=== 주문 반품 (0을 입력하면 취소) ===");
        System.out.println("반품은 주문 전체 단위로 처리되며, 되돌릴 수 없습니다.");

        while (true) {

            System.out.print("주문번호 > ");
            String orderNo = scanner.nextLine().trim();

            if (CANCEL_INPUT.equals(orderNo)) {
                System.out.println("취소했습니다.");
                return;
            }

            if (orderNo.isEmpty()) {
                System.out.println("[오류] 주문번호를 입력해 주세요.");
                continue;
            }

            if (!ReadYesNo(orderNo + " 주문을 반품할까요? (Y/N) > ")) {
                System.out.println("취소했습니다.");
                return;
            }

            try {

                orderCommandService.ReturnOrder(
                        orderNo,
                        LoginSession.getCustomerId(),
                        LoginSession.IsAdmin()
                );

                System.out.println("반품이 완료되었습니다. 결제 금액은 환불 절차에 따라 처리됩니다.");
                return;

            } catch (IllegalArgumentException e) {

                // 주문번호를 잘못 입력했거나 이미 반품된 주문이면 다시 입력받는다.
                System.out.println("[오류] " + e.getMessage());

            } catch (RuntimeException e) {

                System.out.println("[오류] 반품 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
                return;
            }
        }
    }


    /**
     * Y 또는 N을 입력받는 헬퍼 메서드입니다.
     */
    private boolean ReadYesNo(String prompt) {

        while (true) {

            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Y")) {
                return true;
            }
            if (input.equalsIgnoreCase("N") || CANCEL_INPUT.equals(input)) {
                return false;
            }

            System.out.println("Y 또는 N을 입력해 주세요.");
        }
    }
}
