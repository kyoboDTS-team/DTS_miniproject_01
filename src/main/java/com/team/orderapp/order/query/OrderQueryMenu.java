package com.team.orderapp.order.query;

import java.util.Scanner;

/**
 * 관리자 주문 / 반품 관리 메뉴
 *
 * 주문 조회 기능이 완성되면 OrderQueryService를 연결하고,
 * 반품 기능이 완성되면 OrderCommandService를 연결하면 됨.
 */
public class OrderQueryMenu {

    private final Scanner scanner;


    public OrderQueryMenu(Scanner scanner) {

        this.scanner = scanner;
    }


    // ============================================================
    // 관리자 주문 / 반품 관리 메인
    // ============================================================

    public void RunAdminMenu() {

        while (true) {

            PrintAdminMenu();

            String input =
                    scanner.nextLine().trim();

            switch (input) {

                case "1":

                    // TODO:
                    // 전체 주문 조회
                    // OrderQueryService.FindAllOrders() 연결 예정
                    ShowAllOrders();

                    break;


                case "2":

                    // TODO:
                    // 주문 상태별 조회
                    // CONFIRMED / RETURNED
                    ShowOrdersByStatus();

                    break;


                case "3":

                    // TODO:
                    // 주문 기간별 조회
                    ShowOrdersByDate();

                    break;


                case "4":

                    // TODO:
                    // 회원 / 비회원 주문 조회
                    ShowOrdersByCustomerType();

                    break;


                case "5":

                    // TODO:
                    // 주문 번호 또는 ID로 상세 조회
                    ShowOrderDetail();

                    break;


                case "6":

                    // TODO:
                    // 다른 조원의 반품 기능 완성 후
                    // OrderCommandService.returnOrder(...) 연결
                    ReturnOrder();

                    break;


                case "0":

                    // 관리자 메인 메뉴로 복귀
                    return;


                default:

                    System.out.println(
                            "올바른 메뉴 번호를 입력해 주세요."
                    );
            }
        }
    }


    // ============================================================
    // 1. 전체 주문 조회
    // ============================================================

    private void ShowAllOrders() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("             전체 주문 조회");
        System.out.println("========================================");

        // TODO:
        // OrderQueryService 전체 주문 조회 기능 연결
        System.out.println(
                "[TODO] 전체 주문 조회 기능 연결 예정"
        );
    }


    // ============================================================
    // 2. 주문 상태별 조회
    // ============================================================

    private void ShowOrdersByStatus() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("           주문 상태별 조회");
        System.out.println("========================================");

        System.out.println("1. 주문 완료 (CONFIRMED)");
        System.out.println("2. 반품 완료 (RETURNED)");
        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");

        String input =
                scanner.nextLine().trim();


        String status;

        switch (input) {

            case "1":
                status = "CONFIRMED";
                break;

            case "2":
                status = "RETURNED";
                break;

            case "0":
                return;

            default:

                System.out.println(
                        "올바른 상태를 선택해 주세요."
                );

                return;
        }


        // TODO:
        // 나중에 OrderQueryService 연결
        System.out.println(
                "[TODO] 상태 조회: " + status
        );
    }


    // ============================================================
    // 3. 기간별 조회
    // ============================================================

    private void ShowOrdersByDate() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("             기간별 주문 조회");
        System.out.println("========================================");

        System.out.print(
                "시작 날짜 (YYYY-MM-DD) > "
        );

        String startDate =
                scanner.nextLine().trim();


        System.out.print(
                "종료 날짜 (YYYY-MM-DD) > "
        );

        String endDate =
                scanner.nextLine().trim();


        // TODO:
        // OrderQueryService 기간 조회 연결
        System.out.println(
                "[TODO] "
                        + startDate
                        + " ~ "
                        + endDate
                        + " 주문 조회"
        );
    }


    // ============================================================
    // 4. 회원 / 비회원 주문 조회
    // ============================================================

    private void ShowOrdersByCustomerType() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("          회원 / 비회원 주문 조회");
        System.out.println("========================================");

        System.out.println("1. 회원 주문");
        System.out.println("2. 비회원 주문");
        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");


        String input =
                scanner.nextLine().trim();


        switch (input) {

            case "1":

                // customer_id IS NOT NULL
                System.out.println(
                        "[TODO] 회원 주문 조회"
                );

                break;


            case "2":

                // customer_id IS NULL
                System.out.println(
                        "[TODO] 비회원 주문 조회"
                );

                break;


            case "0":

                return;


            default:

                System.out.println(
                        "올바른 메뉴 번호를 입력해 주세요."
                );
        }
    }


    // ============================================================
    // 5. 주문 상세 조회
    // ============================================================

    private void ShowOrderDetail() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("             주문 상세 조회");
        System.out.println("========================================");

        System.out.print(
                "주문 번호(order_no) > "
        );


        String orderNo =
                scanner.nextLine().trim();


        if (orderNo.isBlank()) {

            System.out.println(
                    "주문 번호를 입력해 주세요."
            );

            return;
        }


        // TODO:
        // OrderQueryService 상세 조회 연결
        System.out.println(
                "[TODO] 주문 상세 조회: "
                        + orderNo
        );
    }


    // ============================================================
    // 6. 관리자 반품 처리
    // ============================================================

    private void ReturnOrder() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("              주문 반품");
        System.out.println("========================================");

        System.out.print(
                "반품할 주문 번호(order_no) > "
        );


        String orderNo =
                scanner.nextLine().trim();


        if (orderNo.isBlank()) {

            System.out.println(
                    "주문 번호를 입력해 주세요."
            );

            return;
        }


        System.out.print(
                "정말 전체 반품하시겠습니까? (Y/N) > "
        );


        String confirm =
                scanner.nextLine()
                        .trim()
                        .toUpperCase();


        if (!confirm.equals("Y")) {

            System.out.println(
                    "반품 처리를 취소했습니다."
            );

            return;
        }


        /*
         * TODO:
         *
         * 형준님의 OrderCommandService가 완성되면
         * 여기만 실제 반품 Service 호출로 변경
         *
         * 예:
         *
         * boolean result =
         *      orderCommandService.ReturnOrder(orderNo, ...);
         *
         * 반품 Service 내부에서:
         *
         * 1. CONFIRMED 주문인지 확인
         * 2. orders.status → RETURNED
         * 3. returned_at 기록
         * 4. 일반 상품 재고 복구
         * 5. 시리얼 AVAILABLE 복구
         * 6. order_item_unit.returned_at 기록
         * 7. 모두 성공 → commit
         * 8. 하나라도 실패 → rollback
         */

        System.out.println(
                "[TODO] 반품 Service 연결 예정"
        );
    }


    // ============================================================
    // 화면 출력
    // ============================================================

    private void PrintAdminMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("           주문 / 반품 관리");
        System.out.println("========================================");

        System.out.println("1. 전체 주문 조회");
        System.out.println("2. 주문 상태별 조회");
        System.out.println("3. 기간별 주문 조회");
        System.out.println("4. 회원 / 비회원 주문 조회");
        System.out.println("5. 주문 상세 조회");
        System.out.println("6. 주문 반품");
        System.out.println("0. 이전");

        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }
}