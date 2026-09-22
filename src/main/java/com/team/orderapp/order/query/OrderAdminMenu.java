package com.team.orderapp.order.query;

import com.team.orderapp.common.ConsoleUi;
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

    // 표 열 너비 (한글은 2칸으로 계산)
    private static final int COL_ID = 6;
    private static final int COL_ORDER_NO = 23;
    private static final int COL_DATE = 18;
    private static final int COL_CUSTOMER = 12;
    private static final int COL_STATUS = 11;
    private static final int COL_QUANTITY = 6;
    private static final int COL_AMOUNT = 14;
    private static final int TABLE_WIDTH =
            COL_ID + COL_ORDER_NO + COL_DATE + COL_CUSTOMER
                    + COL_STATUS + COL_QUANTITY + COL_AMOUNT;

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
                    ConsoleUi.Choice(scanner.nextLine());

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
                    ConsoleUi.InvalidMenu();
                    ConsoleUi.PressEnter(scanner);
            }
        }
    }


    private void PrintMenu() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ADMIN / ORDER", "주문 / 반품 관리");

        ConsoleUi.Section("주문 조회");
        ConsoleUi.MenuItem("01", "전체 주문 조회");
        ConsoleUi.MenuItem("02", "주문 상세 조회");
        ConsoleUi.MenuItem("03", "조건별 주문 조회");
        ConsoleUi.MenuItem("04", "반품 완료 주문 조회");

        ConsoleUi.Section("시스템");
        ConsoleUi.MenuItem("00", "이전");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }
    // =====================================================
    // 1. 전체 주문 조회
    // =====================================================

    private void ShowAllOrders() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / ALL", "전체 주문 조회");

        /*
         * Menu → Service → DAO 순서로 호출됩니다.
         */
        List<OrderSummaryView> orders =
                orderQueryService.FindAllOrders();

        PrintOrderSummaries(orders);
        ConsoleUi.PressEnter(scanner);
    }


    // =====================================================
    // 2. 주문 상세 조회
    // =====================================================

    private void ShowOrderDetail() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / DETAIL", "주문 상세 조회");
        ConsoleUi.Prompt("주문번호");

        // 사용자가 입력한 주문번호
        String orderNo =
                scanner.nextLine().trim();

        if (orderNo.isBlank()) {
            ConsoleUi.Error("주문번호를 입력해 주세요.");
            ConsoleUi.PressEnter(scanner);
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
            ConsoleUi.Error("일치하는 주문을 찾을 수 없습니다.");
            ConsoleUi.PressEnter(scanner);
            return;
        }

        PrintOrderDetail(result.get());
        ConsoleUi.PressEnter(scanner);
    }


    // =====================================================
    // 3. 조건별 주문 조회 메뉴
    // =====================================================

    private void RunConditionMenu() {

        while (true) {

            PrintConditionMenu();

            String input =
                    ConsoleUi.Choice(scanner.nextLine());

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
                    ConsoleUi.InvalidMenu();
                    ConsoleUi.PressEnter(scanner);
            }
        }
    }


    /**
     * 주문 상태별 조회
     */
    private void ShowOrdersByStatus() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / STATUS", "주문 상태별 조회");
        System.out.println();
        ConsoleUi.Option("1", ConsoleUi.Status("CONFIRMED") + " 주문 확정");
        ConsoleUi.Option("2", ConsoleUi.Status("RETURNED") + " 반품 완료");
        ConsoleUi.Option("0", "이전");
        System.out.println();
        ConsoleUi.Prompt("선택");

        String input =
                ConsoleUi.Choice(scanner.nextLine());

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
                ConsoleUi.Error("올바른 상태를 선택해주세요.");
                return;
        }

        List<OrderSummaryView> orders =
                orderQueryService.FindOrdersByStatus(
                        status
                );

        PrintOrderSummaries(orders);
        ConsoleUi.PressEnter(scanner);
    }


    /**
     * 기간별 주문 조회
     */
    private void ShowOrdersByDate() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / PERIOD", "기간별 주문 조회");
        ConsoleUi.Info("이전으로 가려면 0을 입력하세요.");

        LocalDate startDate =
                ReadDate("시작 날짜 (YYYY-MM-DD)");

        if (startDate == null) {
            return;
        }

        LocalDate endDate =
                ReadDate("종료 날짜 (YYYY-MM-DD)");

        if (endDate == null) {
            return;
        }

        List<OrderSummaryView> orders =
                orderQueryService.FindOrdersByDate(
                        startDate,
                        endDate
                );

        System.out.println();
        ConsoleUi.Info("조회 기간: " + startDate + " ~ " + endDate);

        PrintOrderSummaries(orders);
        ConsoleUi.PressEnter(scanner);
    }


    /**
     * 회원·비회원 주문 구분 조회
     */
    private void ShowOrdersByCustomerType() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / CUSTOMER", "회원 / 비회원 주문 조회");
        System.out.println();
        ConsoleUi.Option("1", "회원 주문");
        ConsoleUi.Option("2", "비회원 주문");
        ConsoleUi.Option("0", "이전");
        System.out.println();
        ConsoleUi.Prompt("선택");

        String input =
                ConsoleUi.Choice(scanner.nextLine());

        List<OrderSummaryView> orders;

        switch (input) {

            case "1":
                orders =
                        orderQueryService.FindMemberOrders();

                System.out.println();
                        ConsoleUi.Section("회원 주문");
                break;

            case "2":
                orders =
                        orderQueryService.FindGuestOrders();

                System.out.println();
                ConsoleUi.Section("비회원 주문");
                break;

            case "0":
                return;

            default:
                ConsoleUi.Error("올바른 메뉴 번호를 입력해주세요.");
                return;
        }

        PrintOrderSummaries(orders);
        ConsoleUi.PressEnter(scanner);
    }


    // =====================================================
    // 4. 반품 완료 주문 조회
    // =====================================================

    private void ShowReturnedOrders() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / RETURNED", "반품 완료 주문 조회");

        /*
         * 반품 처리를 실행하는 것이 아니라
         * RETURNED 상태인 주문을 조회합니다.
         */
        List<OrderSummaryView> orders =
                orderQueryService.FindOrdersByStatus(
                        OrderStatus.RETURNED
                );

        PrintOrderSummaries(orders);
        ConsoleUi.PressEnter(scanner);
    }


    // =====================================================
    // 주문 요약 목록 출력
    // =====================================================

    private void PrintOrderSummaries(
            List<OrderSummaryView> orders
    ) {

        if (orders == null || orders.isEmpty()) {

            System.out.println();
            ConsoleUi.Warn("조회된 주문이 없습니다.");

            return;
        }

        System.out.println();
        System.out.println(
                ConsoleUi.Cyan(
                        ConsoleUi.PadRight("ID", COL_ID)
                                + ConsoleUi.PadRight("주문번호", COL_ORDER_NO)
                                + ConsoleUi.PadRight("주문일시", COL_DATE)
                                + ConsoleUi.PadRight("주문자", COL_CUSTOMER)
                                + ConsoleUi.PadRight("상태", COL_STATUS)
                                + ConsoleUi.PadLeft("수량", COL_QUANTITY)
                                + ConsoleUi.PadLeft("금액", COL_AMOUNT)
                )
        );
        ConsoleUi.Divider(TABLE_WIDTH);

        for (OrderSummaryView order : orders) {

            String customerName =
                    order.getCustomerName();

            if (customerName == null ||
                    customerName.isBlank()) {

                customerName = order.GetCustomerTypeName();
            }

            System.out.println(
                    ConsoleUi.PadRight(String.valueOf(order.getOrderId()), COL_ID)
                            + ConsoleUi.PadRight(order.getOrderNo(), COL_ORDER_NO)
                            + ConsoleUi.PadRight(
                                    FormatDateTime(order.getOrderedAt()), COL_DATE)
                            + ConsoleUi.PadRight(
                                    ConsoleUi.Truncate(customerName, COL_CUSTOMER - 1), COL_CUSTOMER)
                            + ConsoleUi.PadRight(FormatStatus(order.getStatus()), COL_STATUS)
                            + ConsoleUi.PadLeft(
                                    String.valueOf(order.getTotalQuantity()), COL_QUANTITY)
                            + ConsoleUi.PadLeft(
                                    FormatMoney(order.getTotalAmount()) + "원", COL_AMOUNT)
            );
        }

        ConsoleUi.Divider(TABLE_WIDTH);
        System.out.println(
                ConsoleUi.PadLeft("조회된 주문 " + orders.size() + "건", TABLE_WIDTH)
        );
    }


    // =====================================================
    // 주문 상세 출력
    // =====================================================

    private void PrintOrderDetail(
            OrderDetailView order
    ) {

        String customerName =
                order.getCustomerName();

        if (customerName == null ||
                customerName.isBlank()) {

            customerName = "-";
        }

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / DETAIL", order.getOrderNo());

        System.out.println();
        ConsoleUi.Field("주문 ID", String.valueOf(order.getOrderId()));
        ConsoleUi.Field("주문일시", FormatDateTime(order.getOrderedAt()));
        ConsoleUi.Field("주문자", customerName + "  (" + order.GetCustomerTypeName() + ")");
        ConsoleUi.Field("주문상태", FormatStatus(order.getStatus()));

        if (order.getReturnedAt() != null) {
            ConsoleUi.Field("반품일시", ConsoleUi.Yellow(FormatDateTime(order.getReturnedAt())));
        }

        System.out.println();

        if (order.getItems() == null ||
                order.getItems().isEmpty()) {

            ConsoleUi.Warn("주문 상품이 없습니다.");

        } else {

            System.out.println(
                    ConsoleUi.Cyan(
                            ConsoleUi.PadRight("코드", 10)
                                    + ConsoleUi.PadRight("상품명", 22)
                                    + ConsoleUi.PadLeft("단가", 13)
                                    + ConsoleUi.PadLeft("수량", 6)
                                    + ConsoleUi.PadLeft("소계", 14)
                    )
            );
            ConsoleUi.Divider(65);

            for (OrderItemDetailView item :
                    order.getItems()) {

                PrintOrderItem(item);
            }

            ConsoleUi.Divider(65);
        }

        System.out.println(
                ConsoleUi.PadLeft("상품 종류 " + order.getItemCount()
                        + "  ·  전체 수량 " + order.getTotalQuantity()
                        + "  ·  총금액 " + ConsoleUi.Amount(order.getTotalAmount()), 65)
        );
    }


    /**
     * 주문 상품 한 줄 출력
     */
    private void PrintOrderItem(OrderItemDetailView item) {

        System.out.println(
                ConsoleUi.PadRight(item.getProductCode(), 10)
                        + ConsoleUi.PadRight(
                                ConsoleUi.Truncate(item.getProductName(), 21), 22)
                        + ConsoleUi.PadLeft(FormatMoney(item.getUnitPrice()) + "원", 13)
                        + ConsoleUi.PadLeft(String.valueOf(item.getQuantity()), 6)
                        + ConsoleUi.PadLeft(FormatMoney(item.getSubtotal()) + "원", 14)
        );
    }


    /**
     * 주문일시를 화면용 문자열로 바꾸는 헬퍼 메서드입니다.
     */
    private String FormatDateTime(java.time.LocalDateTime dateTime) {

        return dateTime == null
                ? "-"
                : dateTime.format(dateTimeFormatter);
    }


    /**
     * 주문 상태를 색상 규칙에 맞게 표시하는 헬퍼 메서드입니다.
     */
    private String FormatStatus(com.team.orderapp.order.model.OrderStatus status) {

        return status == null
                ? "-"
                : ConsoleUi.Status(status.name());
    }


    // =====================================================
    // 날짜 입력
    // =====================================================

    private LocalDate ReadDate(String message) {

        while (true) {

            ConsoleUi.Prompt(message);

            String input =
                    ConsoleUi.Choice(scanner.nextLine());

            if (input.equals("0")) {
                return null;
            }

            try {
                return LocalDate.parse(input);

            } catch (DateTimeParseException e) {

                ConsoleUi.Error("날짜는 YYYY-MM-DD 형식으로 입력해주세요.");

                ConsoleUi.Info("예: 2026-09-17");
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

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / SEARCH", "조건별 주문 조회");
        ConsoleUi.Section("조회 조건");
        ConsoleUi.MenuItem("01", "주문 상태별 조회");
        ConsoleUi.MenuItem("02", "기간별 주문 조회");
        ConsoleUi.MenuItem("03", "회원 / 비회원 주문 조회");
        ConsoleUi.MenuItem("00", "이전");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }
}