package com.team.orderapp.order.query;

import com.team.orderapp.common.ConsoleUi;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

/**
 * 회원,비회원 주문 / 반품 관리 메뉴
 *
 *
 * 반품 기능이 완성되면 OrderCommandService를 연결하면 됨.
 */
public class OrderQueryMenu {

    // 표 열 너비 (한글은 2칸으로 계산)
    private static final int COL_ID = 6;
    private static final int COL_ORDER_NO = 23;
    private static final int COL_DATE = 18;
    private static final int COL_STATUS = 11;
    private static final int COL_QUANTITY = 6;
    private static final int COL_AMOUNT = 14;
    private static final int TABLE_WIDTH =
            COL_ID + COL_ORDER_NO + COL_DATE + COL_STATUS + COL_QUANTITY + COL_AMOUNT;

    private final Scanner scanner;

    private final OrderQueryService orderQueryService;

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm"
            );
    public OrderQueryMenu(Scanner scanner) {

        this.scanner = scanner;

        this.orderQueryService =
                new OrderQueryService();
    }


    // ============================================================
    // 비회원 주문 조회 (GuestMenu 4번에서 호출)
    // ============================================================

    /**
     * 비회원이 주문번호로 주문 상세를 조회합니다.
     */
    public void ShowGuestOrder() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / GUEST", "비회원 주문 조회");

        System.out.println();
        ConsoleUi.Info("주문번호에 0을 입력하면 이전 화면으로 돌아갑니다.");
        System.out.println();
        ConsoleUi.Prompt("주문번호");

        String orderNo =
                scanner.nextLine().trim();

        // 이전 화면으로 이동
        if (orderNo.equals("0")) {
            return;
        }

        if (orderNo.isBlank()) {
            ConsoleUi.Error("주문번호를 입력해 주세요.");
            return;
        }

        try {
            Optional<OrderDetailView> result =
                    orderQueryService.FindDetailByOrderNo(
                            orderNo
                    );

            if (result.isEmpty()) {
                ConsoleUi.Error("일치하는 주문을 찾을 수 없습니다.");
                return;
            }

            PrintOrderDetail(result.get());
            ConsoleUi.PressEnter(scanner);

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            ConsoleUi.Error(e.getMessage());

        } catch (Exception e) {

            ConsoleUi.Error("주문 조회 중 오류가 발생했습니다.");
        }
    }
    // =====================================================
    // 회원 내 주문 목록 / 상세
    // MemberMenu 4번에서 호출
    // =====================================================

    /**
     * 로그인한 회원의 주문 목록을 조회합니다.
     *
     * @param customerId 로그인한 회원의 customer PK
     */
    public void ShowMyOrders(Long customerId) {

        try {
            /*
             * customerId를 조건으로 회원 본인의
             * 주문 목록만 조회합니다.
             */
            List<OrderSummaryView> orders =
                    orderQueryService.FindMyOrders(
                            customerId
                    );

            if (orders == null || orders.isEmpty()) {

                ConsoleUi.ClearScreen();
                ConsoleUi.ScreenHeader("ORDER / MY", "내 주문 목록");
                System.out.println();
                ConsoleUi.Warn("주문 내역이 없습니다.");
                ConsoleUi.PressEnter(scanner);

                return;
            }

            while (true) {

                ConsoleUi.ClearScreen();
                ConsoleUi.ScreenHeader("ORDER / MY", "내 주문 목록", orders.size() + "건");

                PrintOrderSummaries(orders);

                System.out.println();
                ConsoleUi.Info("주문 ID를 입력하면 상세 정보를 확인합니다.");
                ConsoleUi.Option("0", "이전");
                System.out.println();
                ConsoleUi.Prompt("선택");

                String input =
                        scanner.nextLine().trim();

                if (ConsoleUi.Choice(input).equals("0")) {
                    return;
                }

                try {
                    long orderId =
                            Long.parseLong(input);

                    /*
                     * 현재 출력된 본인 주문 목록에
                     * 입력한 주문 ID가 있는지 확인합니다.
                     */
                    if (!ContainsOrder(
                            orders,
                            orderId
                    )) {

                        ConsoleUi.Error("목록에 있는 주문 ID를 입력해 주세요.");
                        ConsoleUi.PressEnter(scanner);

                        continue;
                    }

                    ShowMyOrderDetail(
                            orderId,
                            customerId
                    );

                } catch (NumberFormatException e) {

                    ConsoleUi.Error("주문 ID는 숫자로 입력해 주세요.");
                    ConsoleUi.PressEnter(scanner);
                }
            }

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            ConsoleUi.Error(e.getMessage());

        } catch (Exception e) {

            ConsoleUi.Error("주문 조회 중 오류가 발생했습니다.");
        }
    }
    // =====================================================
    // 회원 본인 주문 상세 조회
    // =====================================================

    /**
     * orderId와 customerId를 모두 조건으로 사용하여
     * 회원 본인의 주문만 상세 조회합니다.
     */
    private void ShowMyOrderDetail(
            Long orderId,
            Long customerId
    ) {

        Optional<OrderDetailView> result =
                orderQueryService.FindMyOrderDetail(
                        orderId,
                        customerId
                );

        if (result.isEmpty()) {

            ConsoleUi.Error("주문을 찾을 수 없거나 본인의 주문이 아닙니다.");

            return;
        }

        PrintOrderDetail(result.get());
        ConsoleUi.PressEnter(scanner);
    }
    // =====================================================
    // 현재 목록에 주문이 존재하는지 확인
    // =====================================================

    private boolean ContainsOrder(
            List<OrderSummaryView> orders,
            long orderId
    ) {

        for (OrderSummaryView order : orders) {

            if (order.getOrderId() != null &&
                    order.getOrderId() == orderId) {

                return true;
            }
        }

        return false;
    }

    // =====================================================
    // 주문 목록 출력
    // =====================================================

    private void PrintOrderSummaries(
            List<OrderSummaryView> orders
    ) {

        System.out.println();
        System.out.println(
                ConsoleUi.Cyan(
                        ConsoleUi.PadRight("ID", COL_ID)
                                + ConsoleUi.PadRight("주문번호", COL_ORDER_NO)
                                + ConsoleUi.PadRight("주문일시", COL_DATE)
                                + ConsoleUi.PadRight("상태", COL_STATUS)
                                + ConsoleUi.PadLeft("수량", COL_QUANTITY)
                                + ConsoleUi.PadLeft("금액", COL_AMOUNT)
                )
        );
        ConsoleUi.Divider(TABLE_WIDTH);

        for (OrderSummaryView order : orders) {

            System.out.println(
                    ConsoleUi.PadRight(String.valueOf(order.getOrderId()), COL_ID)
                            + ConsoleUi.PadRight(order.getOrderNo(), COL_ORDER_NO)
                            + ConsoleUi.PadRight(FormatDateTime(order.getOrderedAt()), COL_DATE)
                            + ConsoleUi.PadRight(FormatStatus(order.getStatus()), COL_STATUS)
                            + ConsoleUi.PadLeft(String.valueOf(order.getTotalQuantity()), COL_QUANTITY)
                            + ConsoleUi.PadLeft(
                                    FormatMoney(order.getTotalAmount()) + "원", COL_AMOUNT)
            );
        }

        ConsoleUi.Divider(TABLE_WIDTH);
    }

    // =====================================================
    // 주문 상세 출력
    // =====================================================

    private void PrintOrderDetail(
            OrderDetailView order
    ) {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ORDER / DETAIL", order.getOrderNo());

        System.out.println();
        ConsoleUi.Field("주문일시", FormatDateTime(order.getOrderedAt()));
        ConsoleUi.Field("주문상태", FormatStatus(order.getStatus()));
        ConsoleUi.Field("주문자", order.GetCustomerTypeName()
                + (order.getCustomerName() == null ? "" : "  " + order.getCustomerName()));

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
    private void PrintOrderItem(
            OrderItemDetailView item
    ) {

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
     *
     * DB 상태값을 그대로 쓰되 CONFIRMED는 GREEN, RETURNED는 YELLOW로 구분합니다.
     */
    private String FormatStatus(com.team.orderapp.order.model.OrderStatus status) {

        return status == null
                ? "-"
                : ConsoleUi.Status(status.name());
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

}