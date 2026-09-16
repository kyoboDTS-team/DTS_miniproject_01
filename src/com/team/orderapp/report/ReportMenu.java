package com.team.orderapp.report;

import com.team.orderapp.common.ConsoleInput;

import java.time.LocalDate;
import java.util.List;

/**
 * 통계 및 보고서 화면 콘솔 인터랙션을 담당하는 클래스입니다.
 */
public class ReportMenu {

    private final ReportService reportService;

    public ReportMenu() {
        this.reportService = new ReportService();
    }

    public ReportMenu(ReportService InReportService) {
        this.reportService = InReportService;
    }

    /**
     * 보고서 서브 메뉴를 화면에 표시하고 입력을 처리합니다.
     */
    public void DisplayMenu() {
        boolean inMenu = true;
        while (inMenu) {
            PrintReportMenuOptions();
            int choice = ConsoleInput.ReadInt("메뉴 번호를 선택하세요: ");
            inMenu = RouteReportChoice(choice);
        }
    }

    /**
     * 일간 매출 요약 보고서를 조회하여 출력합니다.
     */
    public void ShowDailyReport() {
        System.out.println("\n--- 일간 매출 요약 보고서 ---");
        String dateStr = ConsoleInput.ReadString("조회 일자 (YYYY-MM-DD) [미입력 시 오늘]: ");
        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);

        OrderAmountSummary summary = reportService.GetDailyRevenueSummary(date);
        PrintSummaryTable(summary);
    }

    /**
     * 월간 매출 요약 보고서를 조회하여 출력합니다.
     */
    public void ShowMonthlyReport() {
        System.out.println("\n--- 월간 매출 요약 보고서 ---");
        int year = ConsoleInput.ReadInt("조회 연도 (예: 2026): ");
        int month = ConsoleInput.ReadInt("조회 월 (1~12): ");

        OrderAmountSummary summary = reportService.GetMonthlyRevenueSummary(year, month);
        PrintSummaryTable(summary);
    }

    /**
     * 베스트셀러(인기 상품) 상위 목록을 조회하여 출력합니다.
     */
    public void ShowTopSellingReport() {
        System.out.println("\n--- 인기 상품 TOP N 보고서 ---");
        int limit = ConsoleInput.ReadPositiveInt("조회할 상위 N개 개수: ");

        List<String> topList = reportService.GetTopSellingProducts(limit);
        if (topList.isEmpty()) {
            System.out.println("판매 집계 데이터가 없습니다.");
            return;
        }
        System.out.println("[인기 상품 순위]");
        for (int i = 0; i < topList.size(); i++) {
            System.out.println((i + 1) + ". " + topList.get(i));
        }
    }

    /**
     * 보고서 메뉴 옵션을 출력하는 헬퍼 메서드입니다.
     */
    private void PrintReportMenuOptions() {
        System.out.println("\n[통계 및 보고서 (Report) 메뉴]");
        System.out.println("1. 일간 매출 보고서");
        System.out.println("2. 월간 매출 보고서");
        System.out.println("3. 인기 상품 TOP N 보고서");
        System.out.println("0. 메인 메뉴로 돌아가기");
    }

    /**
     * 메뉴 번호 분기를 처리하는 헬퍼 메서드입니다.
     *
     * @param InChoice 메뉴 선택 번호
     * @return 메뉴 유지 여부
     */
    private boolean RouteReportChoice(int InChoice) {
        switch (InChoice) {
            case 1:
                ShowDailyReport();
                return true;
            case 2:
                ShowMonthlyReport();
                return true;
            case 3:
                ShowTopSellingReport();
                return true;
            case 0:
                return false;
            default:
                System.out.println("잘못된 번호입니다. 다시 선택해 주세요.");
                return true;
        }
    }

    /**
     * 매출 집계 요약 객체를 테이블 형태로 콘솔에 출력하는 헬퍼 메서드입니다.
     *
     * @param InSummary 출력할 OrderAmountSummary 객체
     */
    private void PrintSummaryTable(OrderAmountSummary InSummary) {
        if (InSummary == null) {
            return;
        }
        System.out.println("--------------------------------------------------");
        System.out.println("조회 기간: " + InSummary.GetPeriod());
        System.out.println("총 주문 건수: " + InSummary.GetTotalOrderCount() + "건");
        System.out.println(String.format("총 매출액: %,.0f원", InSummary.GetTotalRevenue()));
        System.out.println(String.format("건당 평균 주문액: %,.0f원", InSummary.GetAverageOrderAmount()));
        System.out.println("--------------------------------------------------");
    }
}
