package com.team.orderapp.order.query;

import com.team.orderapp.common.BusinessException;
import com.team.orderapp.common.ConsoleInput;

import java.util.List;

/**
 * 주문 조회(Query) 화면 콘솔 인터랙션을 담당하는 클래스입니다.
 */
public class OrderQueryMenu {

    private final OrderQueryService orderQueryService;

    public OrderQueryMenu() {
        this.orderQueryService = new OrderQueryService();
    }

    public OrderQueryMenu(OrderQueryService InOrderQueryService) {
        this.orderQueryService = InOrderQueryService;
    }

    /**
     * 주문 조회 메뉴를 표시하고 사용자 입력을 처리합니다.
     */
    public void DisplayMenu() {
        boolean inMenu = true;
        while (inMenu) {
            PrintQueryMenuOptions();
            int choice = ConsoleInput.ReadInt("메뉴 번호를 선택하세요: ");
            inMenu = RouteOrderQueryChoice(choice);
        }
    }

    /**
     * 전체 주문 목록을 요약 형태로 조회합니다.
     */
    public void ListOrders() {
        System.out.println("\n--- 주문 전체 목록 조회 ---");
        List<OrderSummaryView> list = orderQueryService.GetOrderSummaryList();
        if (list.isEmpty()) {
            System.out.println("조회된 주문이 없습니다.");
            return;
        }
        for (OrderSummaryView summary : list) {
            PrintOrderSummary(summary);
        }
    }

    /**
     * 특정 주문의 상세 정보를 조회합니다.
     */
    public void ViewOrderDetail() {
        System.out.println("\n--- 주문 상세 조회 ---");
        long orderId = ConsoleInput.ReadLong("조회할 주문 ID: ");
        try {
            OrderDetailView detail = orderQueryService.GetOrderDetailById(orderId);
            PrintOrderDetail(detail);
        } catch (BusinessException InException) {
            System.out.println("[오류] " + InException.getMessage());
        }
    }

    /**
     * 특정 고객 ID로 주문 목록을 검색합니다.
     */
    public void SearchOrdersByCustomer() {
        System.out.println("\n--- 고객별 주문 검색 ---");
        long customerId = ConsoleInput.ReadLong("검색할 고객 ID: ");
        List<OrderSummaryView> list = orderQueryService.SearchOrdersByCustomer(customerId);
        if (list.isEmpty()) {
            System.out.println("해당 고객의 주문 내역이 없습니다.");
            return;
        }
        for (OrderSummaryView summary : list) {
            PrintOrderSummary(summary);
        }
    }

    /**
     * 주문 조회 메뉴 옵션을 출력하는 헬퍼 메서드입니다.
     */
    private void PrintQueryMenuOptions() {
        System.out.println("\n[주문 조회 (Query) 메뉴]");
        System.out.println("1. 주문 전체 목록 조회 (요약)");
        System.out.println("2. 주문 단건 상세 조회");
        System.out.println("3. 고객별 주문 목록 검색");
        System.out.println("0. 메인 메뉴로 돌아가기");
    }

    /**
     * 메뉴 번호 분기를 처리하는 헬퍼 메서드입니다.
     *
     * @param InChoice 메뉴 선택 번호
     * @return 메뉴 유지 여부
     */
    private boolean RouteOrderQueryChoice(int InChoice) {
        switch (InChoice) {
            case 1:
                ListOrders();
                return true;
            case 2:
                ViewOrderDetail();
                return true;
            case 3:
                SearchOrdersByCustomer();
                return true;
            case 0:
                return false;
            default:
                System.out.println("잘못된 번호입니다. 다시 선택해 주세요.");
                return true;
        }
    }

    /**
     * 단일 주문 요약 정보를 콘솔에 출력하는 헬퍼 메서드입니다.
     *
     * @param InSummary 출력할 OrderSummaryView 객체
     */
    private void PrintOrderSummary(OrderSummaryView InSummary) {
        if (InSummary == null) {
            return;
        }
        System.out.println(String.format("주문번호: %d | 고객명: %s | 상태: %s | 품목수: %d | 결제금액: %,.0f원",
                InSummary.GetOrderId(),
                InSummary.GetCustomerName(),
                InSummary.GetStatus(),
                InSummary.GetItemCount(),
                InSummary.GetTotalAmount()));
    }

    /**
     * 단일 주문 상세 정보를 콘솔에 출력하는 헬퍼 메서드입니다.
     *
     * @param InDetail 출력할 OrderDetailView 객체
     */
    private void PrintOrderDetail(OrderDetailView InDetail) {
        if (InDetail == null) {
            return;
        }
        System.out.println("--------------------------------------------------");
        System.out.println("주문번호: " + InDetail.GetOrderId());
        System.out.println("고객명: " + InDetail.GetCustomerName() + " (연락처: " + InDetail.GetCustomerPhone() + ")");
        System.out.println("배송지: " + InDetail.GetCustomerAddress());
        System.out.println("주문일시: " + InDetail.GetOrderDate());
        System.out.println("주문상태: " + InDetail.GetStatus());
        System.out.println("[주문 상품 목록]");
        for (OrderDetailView.OrderItemDetail item : InDetail.GetItems()) {
            System.out.println(String.format(" - %s | 단가: %,.0f원 | 수량: %d | 소계: %,.0f원",
                    item.GetProductName(),
                    item.GetUnitPrice(),
                    item.GetQuantity(),
                    item.GetSubtotal()));
        }
        System.out.println("총 결제금액: " + InDetail.GetTotalAmount() + "원");
        System.out.println("--------------------------------------------------");
    }
}
