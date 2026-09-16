package com.team.orderapp.stock;

import com.team.orderapp.auth.LoginSession;
import com.team.orderapp.common.BusinessException;
import com.team.orderapp.common.ConsoleInput;
import com.team.orderapp.product.Product;

import java.util.List;

/**
 * 재고 관리 콘솔 메뉴 및 사용자 인터랙션을 담당하는 클래스입니다.
 */
public class StockMenu {

    private final StockService stockService;

    public StockMenu() {
        this.stockService = new StockService();
    }

    public StockMenu(StockService InStockService) {
        this.stockService = InStockService;
    }

    /**
     * 재고 관리 서브 메뉴를 화면에 표시하고 입력을 처리합니다.
     */
    public void DisplayMenu() {
        boolean inMenu = true;
        while (inMenu) {
            PrintStockMenuOptions();
            int choice = ConsoleInput.ReadInt("메뉴 번호를 선택하세요: ");
            inMenu = RouteStockChoice(choice);
        }
    }

    /**
     * 상품 재고 입출고/조정 요청을 처리합니다.
     */
    public void AdjustStock() {
        System.out.println("\n--- 재고 수량 조정 ---");
        long productId = ConsoleInput.ReadLong("조정할 상품 ID: ");
        int delta = ConsoleInput.ReadInt("조정 수량 (입고는 양수, 출고/폐기는 음수): ");
        String reason = ConsoleInput.ReadString("조정 사유: ");

        String worker = LoginSession.IsLoggedIn() ? LoginSession.GetCurrentUser().GetUsername() : "SYSTEM";

        try {
            StockAdjustment result = stockService.AdjustProductStock(productId, delta, reason, worker);
            System.out.println("재고가 성공적으로 조정되었습니다. (변동: " + result.GetQuantityDelta() + ")");
        } catch (BusinessException InException) {
            System.out.println("[오류] " + InException.getMessage());
        }
    }

    /**
     * 특정 상품의 재고 변경 이력을 조회합니다.
     */
    public void ViewStockHistory() {
        System.out.println("\n--- 재고 변동 이력 조회 ---");
        long productId = ConsoleInput.ReadLong("이력을 조회할 상품 ID: ");

        List<StockAdjustment> history = stockService.GetAdjustmentHistory(productId);
        if (history.isEmpty()) {
            System.out.println("해당 상품의 재고 변동 이력이 없습니다.");
            return;
        }
        for (StockAdjustment adj : history) {
            PrintAdjustmentRecord(adj);
        }
    }

    /**
     * 재고 부족 위험 상품 목록을 조회합니다.
     */
    public void CheckLowStock() {
        System.out.println("\n--- 재고 부족 알림 조회 ---");
        int threshold = ConsoleInput.ReadInt("기준 임계 수량(이하): ");

        List<Product> lowStockProducts = stockService.GetLowStockProducts(threshold);
        if (lowStockProducts.isEmpty()) {
            System.out.println("재고 부족 상품이 없습니다.");
            return;
        }
        for (Product product : lowStockProducts) {
            System.out.println(String.format("ID: %d | 상품명: %s | 현재재고: %d개 (임계치: %d개)",
                    product.GetProductId(),
                    product.GetName(),
                    product.GetCurrentStock(),
                    threshold));
        }
    }

    /**
     * 재고 메뉴 옵션을 콘솔에 출력하는 헬퍼 메서드입니다.
     */
    private void PrintStockMenuOptions() {
        System.out.println("\n[재고 관리 메뉴]");
        System.out.println("1. 재고 수량 입출고/조정");
        System.out.println("2. 상품별 재고 변동 이력 조회");
        System.out.println("3. 재고 부족 상품 조회");
        System.out.println("0. 메인 메뉴로 돌아가기");
    }

    /**
     * 메뉴 번호 분기를 처리하는 헬퍼 메서드입니다.
     *
     * @param InChoice 메뉴 선택 번호
     * @return 메뉴 유지 여부
     */
    private boolean RouteStockChoice(int InChoice) {
        switch (InChoice) {
            case 1:
                AdjustStock();
                return true;
            case 2:
                ViewStockHistory();
                return true;
            case 3:
                CheckLowStock();
                return true;
            case 0:
                return false;
            default:
                System.out.println("잘못된 번호입니다. 다시 선택해 주세요.");
                return true;
        }
    }

    /**
     * 단일 재고 조정 기록을 콘솔에 출력하는 헬퍼 메서드입니다.
     *
     * @param InAdjustment 출력할 StockAdjustment 객체
     */
    private void PrintAdjustmentRecord(StockAdjustment InAdjustment) {
        if (InAdjustment == null) {
            return;
        }
        System.out.println(String.format("이력ID: %d | 상품ID: %d | 변동수량: %+d | 사유: %s | 작업자: %s",
                InAdjustment.GetAdjustmentId(),
                InAdjustment.GetProductId(),
                InAdjustment.GetQuantityDelta(),
                InAdjustment.GetReason(),
                InAdjustment.GetAdjustedBy()));
    }
}
