package com.team.orderapp.report;

import com.team.orderapp.common.ConsoleUi;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * 관리자 통계 메뉴
 */
public class ReportMenu {

    private final Scanner scanner;
    private final ReportService reportService;


    public ReportMenu(Scanner scanner) {

        this.scanner = scanner;
        this.reportService =
                new ReportService();
    }


    // ============================================================
    // 통계 메인 메뉴
    // ============================================================

    public void Run() {

        while (true) {

            PrintMenu();
            String input = ConsoleUi.Choice(scanner.nextLine());

            switch (input) {

                case "1":
                    ShowAdminDashboard();
                    break;

                case "2":
                    ShowTotalSales();
                    break;

                case "3":
                    ShowDailySalesStats();
                    break;

                case "4":
                    ShowProductSalesStats();
                    break;

                case "0":
                    return;

                default:
                    ConsoleUi.InvalidMenu();
            }
        }
    }


    // ============================================================
    // 1. 전체 매출 합계
    // ============================================================

    private void ShowTotalSales() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("REPORT / SALES", "전체 매출 합계");

        try {

            BigDecimal totalSales =
                    reportService.GetTotalSales();

            System.out.println();
            ConsoleUi.Field("총 매출", ConsoleUi.Amount(totalSales), 12);

            System.out.println();
            ConsoleUi.Warn("반품 완료(RETURNED) 주문은 매출에서 제외됩니다.");

        } catch (Exception e) {

            ConsoleUi.Error("통계 조회 중 문제가 발생했습니다.");
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 2. 일별 주문 / 매출 통계
    // ============================================================

    private void ShowDailySalesStats() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("REPORT / DAILY", "일별 주문 / 매출 통계");

        try {

            List<DailySalesStat> stats =
                    reportService
                            .GetDailySalesStats();


            if (stats == null ||
                    stats.isEmpty()) {

                System.out.println();
                ConsoleUi.Warn("집계할 주문 데이터가 없습니다.");
                ConsoleUi.PressEnter(scanner);

                return;
            }

            System.out.println();
            System.out.println(
                    ConsoleUi.Cyan(
                            ConsoleUi.PadRight("날짜", 14)
                                    + ConsoleUi.PadLeft("주문건수", 10)
                                    + ConsoleUi.PadLeft("판매수량", 10)
                                    + ConsoleUi.PadLeft("매출", 16)
                    )
            );
            ConsoleUi.Divider(50);

            for (DailySalesStat stat : stats) {

                System.out.println(
                        ConsoleUi.PadRight(String.valueOf(stat.getOrderDate()), 14)
                                + ConsoleUi.PadLeft(String.valueOf(stat.getOrderCount()), 10)
                                + ConsoleUi.PadLeft(String.valueOf(stat.getTotalQuantity()), 10)
                                + ConsoleUi.PadLeft(
                                        ConsoleUi.Money(stat.getTotalSales()) + "원", 16)
                );
            }

            ConsoleUi.Divider(50);

        } catch (Exception e) {

            ConsoleUi.Error("일별 통계 조회 중 문제가 발생했습니다.");
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 3. 상품별 판매 통계
    // ============================================================

    private void ShowProductSalesStats() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("REPORT / PRODUCT", "상품별 판매 통계");

        try {

            List<ProductSalesStat> stats =
                    reportService
                            .GetProductSalesStats();


            if (stats == null ||
                    stats.isEmpty()) {

                System.out.println();
                ConsoleUi.Warn("집계할 판매 데이터가 없습니다.");
                ConsoleUi.PressEnter(scanner);

                return;
            }

            System.out.println();
            System.out.println(
                    ConsoleUi.Cyan(
                            ConsoleUi.PadRight("ID", 6)
                                    + ConsoleUi.PadRight("코드", 10)
                                    + ConsoleUi.PadRight("상품명", 22)
                                    + ConsoleUi.PadLeft("판매수량", 10)
                                    + ConsoleUi.PadLeft("매출", 16)
                    )
            );
            ConsoleUi.Divider(64);

            for (ProductSalesStat stat : stats) {

                System.out.println(
                        ConsoleUi.PadRight(String.valueOf(stat.getProductId()), 6)
                                + ConsoleUi.PadRight(stat.getProductCode(), 10)
                                + ConsoleUi.PadRight(
                                        ConsoleUi.Truncate(stat.getProductName(), 21), 22)
                                + ConsoleUi.PadLeft(String.valueOf(stat.getTotalQuantity()), 10)
                                + ConsoleUi.PadLeft(
                                        ConsoleUi.Money(stat.getTotalSales()) + "원", 16)
                );
            }

            ConsoleUi.Divider(64);

        } catch (Exception e) {

            ConsoleUi.Error("상품별 통계 조회 중 문제가 발생했습니다.");
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 관리자 현황 요약 출력
    // ============================================================

    private void ShowAdminDashboard() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("REPORT / DASHBOARD", "관리자 현황 요약");

        try {
            AdminDashboardStat stat = reportService.GetAdminDashboardStat();

            System.out.println();
            ConsoleUi.Field("전체 상품", stat.getTotalProductCount() + "개", 16);
            ConsoleUi.Field("판매중 상품",
                    ConsoleUi.Green(stat.getSellingProductCount() + "개"), 16);
            ConsoleUi.Field("판매중지 상품",
                    ConsoleUi.Red(stat.getStoppedProductCount() + "개"), 16);

            System.out.println();
            ConsoleUi.Field("재고 부족 상품",
                    ConsoleUi.Yellow(stat.getLowStockProductCount() + "개"), 16);
            ConsoleUi.Field("품절 상품",
                    ConsoleUi.Red(stat.getOutOfStockProductCount() + "개"), 16);
            ConsoleUi.Field("시리얼 상품", stat.getSerialProductCount() + "개", 16);

            System.out.println();
            ConsoleUi.Field("전체 매출", ConsoleUi.Amount(stat.getTotalSales()), 16);

        } catch (Exception e) {
            ConsoleUi.Error("관리자 현황 조회 중 문제가 발생했습니다.");
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 통계 메뉴 출력
    // ============================================================

    private void PrintMenu() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ADMIN / REPORT", "통계");

        ConsoleUi.Section("통계");
        ConsoleUi.MenuItem("01", "관리자 현황 요약");
        ConsoleUi.MenuItem("02", "전체 매출 합계");
        ConsoleUi.MenuItem("03", "일별 주문 / 매출 통계");
        ConsoleUi.MenuItem("04", "상품별 판매 통계");
        ConsoleUi.MenuItem("00", "이전");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }
}
