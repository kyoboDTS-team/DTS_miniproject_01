package com.team.orderapp.export;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * 관리자 CSV 저장 / 불러오기 메뉴
 */
public class ProductCsvMenu {

    private final Scanner scanner;
    private final ProductCsvService productCsvService;
    private final CsvExportService csvExportService;

    public ProductCsvMenu(Scanner scanner) {
        this.scanner = scanner;
        this.productCsvService = new ProductCsvService();
        this.csvExportService = new CsvExportService();
    }


    // ============================================================
    // CSV 메뉴 실행
    // ============================================================
    public void Run() {
        while (true) {
            PrintMenu();

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    ExportProducts();
                    break;
                case "2":
                    ImportProducts();
                    break;
                case "3":
                    RunStockCsvMenu();
                    break;
                case "4":
                    ExportAdjustmentHistory();
                    break;
                case "5":
                    ExportSerialStatus();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("올바른 메뉴 번호를 입력해 주세요.");
            }
        }
    }


    // ============================================================
    // 상품 CSV 저장
    // ============================================================
    private void ExportProducts() {
        System.out.print("파일명 (엔터: products.csv) > ");
        String fileName = scanner.nextLine().trim();

        try {
            Path savedPath = productCsvService.ExportProducts(fileName);

            System.out.println("CSV 저장이 완료되었습니다.");
            System.out.println("저장 위치: " + savedPath);

        } catch (Exception e) {
            System.out.println("CSV 저장 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // 상품 CSV 불러오기
    // ============================================================
    private void ImportProducts() {
        System.out.print("불러올 파일명 (엔터: products.csv) > ");
        String fileName = scanner.nextLine().trim();

        System.out.println("CSV의 모든 상품을 신규 등록합니다.");
        System.out.println("한 행이라도 오류가 있으면 전체 등록이 취소됩니다.");
        System.out.print("계속하시겠습니까? (Y/N) > ");

        String confirm = scanner.nextLine().trim().toUpperCase();

        if (!confirm.equals("Y")) {
            System.out.println("CSV 불러오기를 취소했습니다.");
            return;
        }

        try {
            int insertedCount = productCsvService.ImportProducts(fileName);

            System.out.println("CSV 상품 등록이 완료되었습니다.");
            System.out.println("등록 상품 수: " + insertedCount + "개");

        } catch (Exception e) {
            System.out.println("CSV 불러오기 실패: " + e.getMessage());
            System.out.println("등록된 상품은 없습니다.");
        }
    }


    // ============================================================
    // 재고 현황 CSV 메뉴
    // ============================================================
    private void RunStockCsvMenu() {
        while (true) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("            재고 현황 CSV");
            System.out.println("========================================");
            System.out.println("1. 전체 재고 현황");
            System.out.println("2. 재고 부족 상품");
            System.out.println("3. 품절 상품");
            System.out.println("0. 이전");
            System.out.println("----------------------------------------");
            System.out.print("선택 > ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    ExportAllStock();
                    break;
                case "2":
                    ExportLowStock();
                    break;
                case "3":
                    ExportOutOfStock();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("올바른 메뉴 번호를 입력해 주세요.");
            }
        }
    }


    // ============================================================
    // 전체 재고 CSV 저장
    // ============================================================
    private void ExportAllStock() {
        System.out.print("파일명 (엔터: stock_status.csv) > ");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportAllStock(fileName);

            System.out.println("전체 재고 CSV 저장이 완료되었습니다.");
            System.out.println("저장 위치: " + path);

        } catch (Exception e) {
            System.out.println("CSV 저장 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // 재고 부족 상품 CSV 저장
    // ============================================================
    private void ExportLowStock() {
        System.out.print("파일명 (엔터: low_stock.csv) > ");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportLowStock(fileName);

            System.out.println("재고 부족 상품 CSV 저장이 완료되었습니다.");
            System.out.println("저장 위치: " + path);

        } catch (Exception e) {
            System.out.println("CSV 저장 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // 품절 상품 CSV 저장
    // ============================================================
    private void ExportOutOfStock() {
        System.out.print("파일명 (엔터: out_of_stock.csv) > ");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportOutOfStock(fileName);

            System.out.println("품절 상품 CSV 저장이 완료되었습니다.");
            System.out.println("저장 위치: " + path);

        } catch (Exception e) {
            System.out.println("CSV 저장 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // 재고 변경 이력 CSV 저장
    // ============================================================
    private void ExportAdjustmentHistory() {
        System.out.print("파일명 (엔터: stock_adjustment_history.csv) > ");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportAdjustmentHistory(fileName);

            System.out.println("재고 변경 이력 CSV 저장이 완료되었습니다.");
            System.out.println("저장 위치: " + path);

        } catch (Exception e) {
            System.out.println("CSV 저장 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // 시리얼 현황 CSV 저장
    // ============================================================
    private void ExportSerialStatus() {
        System.out.print("파일명 (엔터: serial_status.csv) > ");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportSerialStatus(fileName);

            System.out.println("시리얼 현황 CSV 저장이 완료되었습니다.");
            System.out.println("저장 위치: " + path);

        } catch (Exception e) {
            System.out.println("CSV 저장 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // CSV 메뉴 출력
    // ============================================================
    private void PrintMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("          CSV 저장 / 불러오기");
        System.out.println("========================================");
        System.out.println("1. 상품 CSV 저장");
        System.out.println("2. 상품 CSV 불러오기");
        System.out.println("3. 재고 현황 CSV 저장");
        System.out.println("4. 재고 변경 이력 CSV 저장");
        System.out.println("5. 시리얼 현황 CSV 저장");
        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }
}