package com.team.orderapp.order.command;

import com.team.orderapp.common.BusinessException;
import com.team.orderapp.common.ConsoleInput;
import com.team.orderapp.order.model.Order;
import com.team.orderapp.order.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문 접수 및 변경/취소 화면 콘솔 인터랙션을 담당하는 클래스입니다.
 */
public class OrderCommandMenu {

    private final OrderCommandService orderCommandService;

    public OrderCommandMenu() {
        this.orderCommandService = new OrderCommandService();
    }

    public OrderCommandMenu(OrderCommandService InOrderCommandService) {
        this.orderCommandService = InOrderCommandService;
    }

    /**
     * 주문 접수 및 명령 처리 메뉴를 표시합니다.
     */
    public void DisplayMenu() {
        boolean inMenu = true;
        while (inMenu) {
            PrintCommandMenuOptions();
            int choice = ConsoleInput.ReadInt("메뉴 번호를 선택하세요: ");
            inMenu = RouteOrderCommandChoice(choice);
        }
    }

    /**
     * 신규 주문을 입력받아 접수하는 흐름을 처리합니다.
     */
    public void CreateOrder() {
        System.out.println("\n--- 신규 주문 접수 ---");
        long customerId = ConsoleInput.ReadLong("주문 고객 ID: ");
        List<OrderItem> items = PromptOrderItems();

        if (items.isEmpty()) {
            System.out.println("주문 항목이 없어 주문을 취소합니다.");
            return;
        }

        Order order = new Order(null, customerId, "PENDING");
        order.SetItems(items);

        try {
            Order placedOrder = orderCommandService.PlaceOrder(order);
            System.out.println("주문이 성공적으로 접수되었습니다! 주문번호: " + placedOrder.GetOrderId() + ", 총 결제금액: " + placedOrder.GetTotalAmount() + "원");
        } catch (BusinessException InException) {
            System.out.println("[주문 실패] " + InException.getMessage());
        }
    }

    /**
     * 기존 주문을 취소하는 흐름을 처리합니다.
     */
    public void CancelOrder() {
        System.out.println("\n--- 주문 취소 ---");
        long orderId = ConsoleInput.ReadLong("취소할 주문 ID: ");
        String reason = ConsoleInput.ReadString("취소 사유: ");

        try {
            orderCommandService.CancelOrder(orderId, reason);
            System.out.println("주문이 성공적으로 취소되었습니다.");
        } catch (BusinessException InException) {
            System.out.println("[취소 실패] " + InException.getMessage());
        }
    }

    /**
     * 주문 메뉴 항목을 출력하는 헬퍼 메서드입니다.
     */
    private void PrintCommandMenuOptions() {
        System.out.println("\n[주문 접수/변경 (Command) 메뉴]");
        System.out.println("1. 신규 주문 접수");
        System.out.println("2. 주문 취소");
        System.out.println("0. 메인 메뉴로 돌아가기");
    }

    /**
     * 메뉴 번호 분기를 처리하는 헬퍼 메서드입니다.
     *
     * @param InChoice 메뉴 선택 번호
     * @return 메뉴 유지 여부
     */
    private boolean RouteOrderCommandChoice(int InChoice) {
        switch (InChoice) {
            case 1:
                CreateOrder();
                return true;
            case 2:
                CancelOrder();
                return true;
            case 0:
                return false;
            default:
                System.out.println("잘못된 번호입니다. 다시 선택해 주세요.");
                return true;
        }
    }

    /**
     * 사용자로부터 복수의 주문 상품 항목을 입력받는 헬퍼 메서드입니다.
     *
     * @return 입력된 OrderItem 목록
     */
    private List<OrderItem> PromptOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        while (true) {
            System.out.println("\n[상품 추가] (종료하려면 상품 ID에 0 입력)");
            long productId = ConsoleInput.ReadLong("상품 ID: ");
            if (productId == 0) {
                break;
            }
            double price = ConsoleInput.ReadDouble("단가: ");
            int quantity = ConsoleInput.ReadPositiveInt("수량: ");

            OrderItem item = new OrderItem(null, null, productId, price, quantity);
            items.add(item);
            System.out.println("상품 추가됨: ID " + productId + " (" + quantity + "개)");
        }
        return items;
    }
}
