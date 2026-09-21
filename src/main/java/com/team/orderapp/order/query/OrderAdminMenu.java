package com.team.orderapp.order.query;

import com.team.orderapp.order.model.OrderStatus;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

/**
 * 관리자 주문 / 반품 관리 메뉴
 *
 * 주문 조회 : 박형준 기능 연결
 * 반품 처리 : 김상진 반품 기능에서 완료
 *
 * 관리자는 주문 및 반품 완료 내역을 확인하는 역할
 */
public class    OrderAdminMenu {

    private final Scanner scanner;

    private final OrderQueryService orderQueryService;

    // 주문일시 출력 형식
    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm"
            );

    public OrderAdminMenu(Scanner scanner) {
        this.scanner = scanner;
        this.orderQueryService =
                new OrderQueryService();
    }


    public void Run() {

        while (true) {

            PrintMenu();

            String input =
                    scanner.nextLine().trim();

            switch (input) {

                case "1":
                    // TODO: 박형준 - 전체 주문 조회 연결 (완료)
                    ShowAllOrders();
                    break;

                case "2":
                    // TODO: 박형준 - 주문 상세 조회 연결 (완료)
                    ShowOrderDetail();
                    break;

                case "3":
                    // TODO: 박형준 - 조건별 주문 조회 연결 (완료)
                    RunConditionMenu();
                    break;

                case "4":
                    // TODO:
                    // 박형준 조회 기능에서
                    // status = RETURNED 조건으로 연결 (완료)
                    ShowReturnedOrders();
                    break;

                case "0":
                    return;

                default:
                    System.out.println(
                            "올바른 메뉴 번호를 입력해 주세요."
                    );
            }
        }
    }


    private void PrintMenu() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "           주문 / 반품 관리"
        );
        System.out.println(
                "========================================"
        );

        System.out.println("1. 전체 주문 조회");
        System.out.println("2. 주문 상세 조회");
        System.out.println("3. 조건별 주문 조회");
        System.out.println("4. 반품 완료 주문 조회");
        System.out.println("0. 이전");

        System.out.println(
                "----------------------------------------"
        );

        System.out.print("선택 > ");
    }
    // =====================================================
    // 1. 전체 주문 조회
    // =====================================================

    private void ShowAllOrders() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("             전체 주문 조회");
        System.out.println("========================================");

        /*
         * Menu → Service → DAO 순서로 호출됩니다.
         */
        List<OrderSummaryView> orders =
                orderQueryService.FindAllOrders();

        PrintOrderSummaries(orders);
    }


    // =====================================================
    // 2. 주문 상세 조회
    // =====================================================

    private void ShowOrderDetail() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("             주문 상세 조회");
        System.out.println("========================================");
        System.out.print("주문번호 > ");

        // 사용자가 입력한 주문번호
        String orderNo =
                scanner.nextLine().trim();

        if (orderNo.isBlank()) {
            System.out.println(
                    "주문번호를 입력해주세요."
            );
            return;
        }

        /*
         * 입력받은 주문번호를 Service 메서드의
         * 파라미터로 전달합니다.
         */
        Optional<OrderDetailView> result =
                orderQueryService.FindDetailByOrderNo(
                        orderNo
                );

        if (result.isEmpty()) {
            System.out.println(
                    "일치하는 주문을 찾을 수 없습니다."
            );
            return;
        }

        PrintOrderDetail(result.get());
    }


    // =====================================================
    // 3. 조건별 주문 조회 메뉴
    // =====================================================

    private void RunConditionMenu() {

        while (true) {

            PrintConditionMenu();

            String input =
                    scanner.nextLine().trim();

            switch (input) {

                case "1":
                    ShowOrdersByStatus();
                    break;

                case "2":
                    ShowOrdersByDate();
                    break;

                case "3":
                    ShowOrdersByCustomerType();
                    break;

                case "0":
                    return;

                default:
                    System.out.println(
                            "올바른 메뉴 번호를 입력해주세요."
                    );
            }
        }
    }


    /**
     * 주문 상태별 조회
     */
    private void ShowOrdersByStatus() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("           주문 상태별 조회");
        System.out.println("========================================");
        System.out.println("1. 주문 확정 (CONFIRMED)");
        System.out.println("2. 반품 완료 (RETURNED)");
        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");

        String input =
                scanner.nextLine().trim();

        OrderStatus status;

        switch (input) {

            case "1":
                status = OrderStatus.CONFIRMED;
                break;

            case "2":
                status = OrderStatus.RETURNED;
                break;

            case "0":
                return;

            default:
                System.out.println(
                        "올바른 상태를 선택해주세요."
                );
                return;
        }

        List<OrderSummaryView> orders =
                orderQueryService.FindOrdersByStatus(
                        status
                );

        PrintOrderSummaries(orders);
    }


    /**
     * 기간별 주문 조회
     */
    private void ShowOrdersByDate() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("             기간별 주문 조회");
        System.out.println("========================================");
        System.out.println("이전으로 가려면 0을 입력하세요.");

        LocalDate startDate =
                ReadDate("시작 날짜 (YYYY-MM-DD) > ");

        if (startDate == null) {
            return;
        }

        LocalDate endDate =
                ReadDate("종료 날짜 (YYYY-MM-DD) > ");

        if (endDate == null) {
            return;
        }

        List<OrderSummaryView> orders =
                orderQueryService.FindOrdersByDate(
                        startDate,
                        endDate
                );

        System.out.println();
        System.out.println(
                "조회 기간: "
                        + startDate
                        + " ~ "
                        + endDate
        );

        PrintOrderSummaries(orders);
    }


    /**
     * 회원·비회원 주문 구분 조회
     */
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

        List<OrderSummaryView> orders;

        switch (input) {

            case "1":
                orders =
                        orderQueryService.FindMemberOrders();

                System.out.println();
                System.out.println("===== 회원 주문 =====");
                break;

            case "2":
                orders =
                        orderQueryService.FindGuestOrders();

                System.out.println();
                System.out.println("===== 비회원 주문 =====");
                break;

            case "0":
                return;

            default:
                System.out.println(
                        "올바른 메뉴 번호를 입력해주세요."
                );
                return;
        }

        PrintOrderSummaries(orders);
    }


    // =====================================================
    // 4. 반품 완료 주문 조회
    // =====================================================

    private void ShowReturnedOrders() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("          반품 완료 주문 조회");
        System.out.println("========================================");

        /*
         * 반품 처리를 실행하는 것이 아니라
         * RETURNED 상태인 주문을 조회합니다.
         */
        List<OrderSummaryView> orders =
                orderQueryService.FindOrdersByStatus(
                        OrderStatus.RETURNED
                );

        PrintOrderSummaries(orders);
    }


    // =====================================================
    // 주문 요약 목록 출력
    // =====================================================

    private void PrintOrderSummaries(
            List<OrderSummaryView> orders
    ) {

        if (orders == null || orders.isEmpty()) {

            System.out.println("조회된 주문이 없습니다.");

            return;
        }

        System.out.println();
        System.out.println("조회된 주문 개수: " + orders.size());

        System.out.println("------------------------------------------------------------");

        for (OrderSummaryView order : orders) {

            String orderedAt =
                    order.getOrderedAt() == null ? "-" : order.getOrderedAt().format(dateTimeFormatter);

            String status =
                    order.getStatus() == null
                            ? "-"
                            : order.getStatus()
                            .GetDisplayName();

            String customerName =
                    order.getCustomerName();

            if (customerName == null ||
                    customerName.isBlank()) {

                customerName = "-";
            }

            System.out.println(
                    "주문 ID       : " + order.getOrderId()
            );

            System.out.println(
                    "주문번호      : " + order.getOrderNo()
            );

            System.out.println(
                    "주문일시      : " + orderedAt
            );

            System.out.println(
                    "주문자        : " + customerName
            );

            System.out.println(
                    "구분          : "
                            + order.GetCustomerTypeName()
            );

            System.out.println(
                    "상태          : " + status
            );

            System.out.println(
                    "상품 종류     : "
                            + order.getItemCount()
            );

            System.out.println(
                    "전체 수량     : "
                            + order.getTotalQuantity()
            );

            System.out.println(
                    "총금액        : "
                            + FormatMoney(
                            order.getTotalAmount()
                    )
                            + "원"
            );

            System.out.println(
                    "------------------------------------------------------------"
            );
        }
    }


    // =====================================================
    // 주문 상세 출력
    // =====================================================

    private void PrintOrderDetail(
            OrderDetailView order
    ) {

        String orderedAt =
                order.getOrderedAt() == null
                        ? "-"
                        : order.getOrderedAt()
                        .format(dateTimeFormatter);

        String returnedAt =
                order.getReturnedAt() == null
                        ? "-"
                        : order.getReturnedAt()
                        .format(dateTimeFormatter);

        String status =
                order.getStatus() == null
                        ? "-"
                        : order.getStatus()
                        .GetDisplayName();

        String customerName =
                order.getCustomerName();

        if (customerName == null ||
                customerName.isBlank()) {

            customerName = "-";
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("              주문 상세");
        System.out.println("========================================");
        System.out.println(
                "주문 ID       : " + order.getOrderId()
        );
        System.out.println(
                "주문번호      : " + order.getOrderNo()
        );
        System.out.println(
                "주문일시      : " + orderedAt
        );
        System.out.println(
                "주문자        : " + customerName
        );
        System.out.println(
                "구분          : "
                        + order.GetCustomerTypeName()
        );
        System.out.println(
                "상태          : " + status
        );
        System.out.println(
                "반품일시      : " + returnedAt
        );
        System.out.println("----------------------------------------");

        if (order.getItems() == null ||
                order.getItems().isEmpty()) {

            System.out.println(
                    "주문 상품이 없습니다."
            );

        } else {

            for (OrderItemDetailView item :
                    order.getItems()) {

                PrintOrderItem(item);
            }
        }

        System.out.println(
                "상품 종류     : " + order.getItemCount()
        );
        System.out.println(
                "전체 수량     : "
                        + order.getTotalQuantity()
        );
        System.out.println(
                "총금액        : "
                        + FormatMoney(
                        order.getTotalAmount()
                )
                        + "원"
        );
        System.out.println("========================================");
    }


    /**
     * 주문 상품 한 줄 출력
     */
    private void PrintOrderItem(OrderItemDetailView item) {

        System.out.println("상품코드      : " + item.getProductCode());

        System.out.println("상품명        : " + item.getProductName());

        System.out.println("단가          : "
                        + FormatMoney(item.getUnitPrice()) + "원"
        );

        System.out.println("수량          : " + item.getQuantity());

        System.out.println("소계          : "
                        + FormatMoney(
                        item.getSubtotal()) + "원"
        );

        System.out.println(
                "----------------------------------------"
        );
    }


    // =====================================================
    // 날짜 입력
    // =====================================================

    private LocalDate ReadDate(String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            if (input.equals("0")) {
                return null;
            }

            try {
                return LocalDate.parse(input);

            } catch (DateTimeParseException e) {

                System.out.println(
                        "날짜는 YYYY-MM-DD 형식으로 입력해주세요."
                );

                System.out.println(
                        "예: 2026-09-17"
                );
            }
        }
    }


    // =====================================================
    // 금액 출력
    // =====================================================

    private String FormatMoney(
            BigDecimal amount
    ) {

        if (amount == null) {
            return "0";
        }

        NumberFormat numberFormat =
                NumberFormat.getNumberInstance(
                        Locale.KOREA
                );

        return numberFormat.format(amount);
    }


    // =====================================================
    // 화면 출력
    // =====================================================




    private void PrintConditionMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("           조건별 주문 조회");
        System.out.println("========================================");
        System.out.println("1. 주문 상태별 조회");
        System.out.println("2. 기간별 주문 조회");
        System.out.println("3. 회원 / 비회원 주문 조회");
        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }
}