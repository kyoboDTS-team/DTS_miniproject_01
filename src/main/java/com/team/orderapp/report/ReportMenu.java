package com.team.orderapp.report;

import java.math.BigDecimal;
import java.text.NumberFormat;
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

            String input =
                    scanner.nextLine().trim();


            switch (input) {

                case "1":

                    // 전체 매출 합계
                    ShowTotalSales();

                    break;


                case "2":

                    // 일별 주문 / 매출 통계
                    ShowDailySalesStats();

                    break;


                case "3":

                    // 상품별 판매 통계
                    ShowProductSalesStats();

                    break;


                case "0":

                    // 관리자 메뉴로 복귀
                    return;


                default:

                    System.out.println(
                            "올바른 메뉴 번호를 입력해 주세요."
                    );
            }
        }
    }


    // ============================================================
    // 1. 전체 매출 합계
    // ============================================================

    private void ShowTotalSales() {

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "             전체 매출 합계"
        );

        System.out.println(
                "========================================"
        );


        try {

            BigDecimal totalSales =
                    reportService.GetTotalSales();


            System.out.println(
                    "총 매출: "
                            + FormatMoney(totalSales)
                            + "원"
            );


            System.out.println();

            System.out.println(
                    "※ 반품 완료(RETURNED) 주문은 "
                            + "매출에서 제외됩니다."
            );


        } catch (Exception e) {

            System.out.println(
                    "통계 조회 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 2. 일별 주문 / 매출 통계
    // ============================================================

    private void ShowDailySalesStats() {

        System.out.println();
        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                   일별 주문 / 매출 통계"
        );

        System.out.println(
                "============================================================"
        );


        try {

            List<DailySalesStat> stats =
                    reportService
                            .GetDailySalesStats();


            if (stats == null ||
                    stats.isEmpty()) {

                System.out.println(
                        "집계할 주문 데이터가 없습니다."
                );

                return;
            }


            System.out.printf(
                    "%-12s %-10s %-10s %-15s%n",
                    "날짜",
                    "주문건수",
                    "판매수량",
                    "매출"
            );


            System.out.println(
                    "------------------------------------------------------------"
            );


            for (DailySalesStat stat : stats) {

                System.out.printf(
                        "%-12s %-10d %-10d %,d원%n",

                        stat.getOrderDate(),

                        stat.getOrderCount(),

                        stat.getTotalQuantity(),

                        stat.getTotalSales()
                                .longValue()
                );
            }


        } catch (Exception e) {

            System.out.println(
                    "일별 통계 조회 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 3. 상품별 판매 통계
    // ============================================================

    private void ShowProductSalesStats() {

        System.out.println();
        System.out.println(
                "======================================================================"
        );

        System.out.println(
                "                       상품별 판매 통계"
        );

        System.out.println(
                "======================================================================"
        );


        try {

            List<ProductSalesStat> stats =
                    reportService
                            .GetProductSalesStats();


            if (stats == null ||
                    stats.isEmpty()) {

                System.out.println(
                        "집계할 판매 데이터가 없습니다."
                );

                return;
            }


            System.out.printf(
                    "%-6s %-12s %-20s %-10s %-15s%n",
                    "ID",
                    "상품코드",
                    "상품명",
                    "판매수량",
                    "매출"
            );


            System.out.println(
                    "----------------------------------------------------------------------"
            );


            for (ProductSalesStat stat : stats) {

                System.out.printf(
                        "%-6d %-12s %-20s %-10d %,d원%n",

                        stat.getProductId(),

                        stat.getProductCode(),

                        stat.getProductName(),

                        stat.getTotalQuantity(),

                        stat.getTotalSales()
                                .longValue()
                );
            }


        } catch (Exception e) {

            System.out.println(
                    "상품별 통계 조회 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 메뉴 출력
    // ============================================================

    private void PrintMenu() {

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "                 통계"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "1. 전체 매출 합계"
        );

        System.out.println(
                "2. 일별 주문 / 매출 통계"
        );

        System.out.println(
                "3. 상품별 판매 통계"
        );

        System.out.println(
                "0. 이전"
        );

        System.out.println(
                "----------------------------------------"
        );

        System.out.print(
                "선택 > "
        );
    }


    // ============================================================
    // 금액 출력 Helper
    // ============================================================

    private String FormatMoney(
            BigDecimal amount
    ) {

        if (amount == null) {
            return "0";
        }

        return NumberFormat
                .getNumberInstance()
                .format(amount);
    }
}