package com.team.orderapp.order.query;

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

        System.out.println();
        System.out.println("========================================");
        System.out.println("           비회원 주문 조회");
        System.out.println("========================================");
        System.out.println("이전으로 가려면 0을 입력하세요.");
        System.out.print("주문번호 > ");

        String orderNo =
                scanner.nextLine().trim();

        // 이전 화면으로 이동
        if (orderNo.equals("0")) {
            return;
        }

        if (orderNo.isBlank()) {
            System.out.println(
                    "주문번호를 입력해주세요."
            );
            return;
        }

        try {
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

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            System.out.println(e.getMessage());

        } catch (Exception e) {

            System.out.println(
                    "주문 조회 중 오류가 발생했습니다."
            );

            System.out.println(e.getMessage());
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

                System.out.println();
                System.out.println(
                        "주문 내역이 없습니다."
                );

                return;
            }

            while (true) {

                System.out.println();
                System.out.println("========================================");
                System.out.println("             내 주문 목록");
                System.out.println("========================================");

                PrintOrderSummaries(orders);

                System.out.println(
                        "상세 조회할 주문 ID를 입력하세요."
                );
                System.out.println("0. 이전");
                System.out.println("----------------------------------------");
                System.out.print("선택 > ");

                String input =
                        scanner.nextLine().trim();

                if (input.equals("0")) {
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

                        System.out.println(
                                "목록에 있는 주문 ID를 입력해주세요."
                        );

                        continue;
                    }

                    ShowMyOrderDetail(
                            orderId,
                            customerId
                    );

                } catch (NumberFormatException e) {

                    System.out.println(
                            "주문 ID는 숫자로 입력해주세요."
                    );
                }
            }

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            System.out.println(e.getMessage());

        } catch (Exception e) {

            System.out.println(
                    "주문 조회 중 오류가 발생했습니다."
            );

            System.out.println(e.getMessage());
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

            System.out.println(
                    "주문을 찾을 수 없거나 "
                            + "본인의 주문이 아닙니다."
            );

            return;
        }

        PrintOrderDetail(result.get());
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

        System.out.println(
                "조회된 주문 개수: " + orders.size()
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        for (OrderSummaryView order : orders) {

            String orderedAt =
                    order.getOrderedAt() == null
                            ? "-"
                            : order.getOrderedAt()
                            .format(dateTimeFormatter);

            String status =
                    order.getStatus() == null
                            ? "-"
                            : order.getStatus()
                            .GetDisplayName();

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

        System.out.println();
        System.out.println("========================================");
        System.out.println("              주문 상세");
        System.out.println("========================================");

        System.out.println(
                "주문번호      : " + order.getOrderNo()
        );

        System.out.println(
                "주문일시      : " + orderedAt
        );

        System.out.println(
                "주문상태      : " + status
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
    private void PrintOrderItem(
            OrderItemDetailView item
    ) {

        System.out.println(
                "상품코드      : " + item.getProductCode()
        );

        System.out.println(
                "상품명        : " + item.getProductName()
        );

        System.out.println(
                "단가          : "
                        + FormatMoney(
                        item.getUnitPrice()
                )
                        + "원"
        );

        System.out.println(
                "수량          : " + item.getQuantity()
        );

        System.out.println(
                "소계          : "
                        + FormatMoney(
                        item.getSubtotal()
                )
                        + "원"
        );

        System.out.println(
                "----------------------------------------"
        );
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