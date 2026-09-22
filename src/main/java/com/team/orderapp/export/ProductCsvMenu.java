package com.team.orderapp.export;

import com.team.orderapp.common.ConsoleUi;

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

            String input = ConsoleUi.Choice(scanner.nextLine());

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
                    ConsoleUi.InvalidMenu();
                    ConsoleUi.PressEnter(scanner);
            }
        }
    }


    // ============================================================
    // 상품 CSV 저장
    // ============================================================
    private void ExportProducts() {
        ConsoleUi.Prompt("파일명 (엔터: products.csv)");
        String fileName = scanner.nextLine().trim();

        try {
            Path savedPath = productCsvService.ExportProducts(fileName);

            ConsoleUi.Success("CSV 저장이 완료되었습니다.");
            ConsoleUi.Field("저장 위치", String.valueOf(savedPath), 10);

        } catch (Exception e) {
            ConsoleUi.Error("CSV 저장에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 상품 CSV 불러오기
    // ============================================================
    private void ImportProducts() {
        ConsoleUi.Prompt("불러올 파일명 (엔터: products.csv)");
        String fileName = scanner.nextLine().trim();

        System.out.println();
        ConsoleUi.Info("CSV의 모든 상품을 신규 등록합니다.");
        ConsoleUi.Warn("한 행이라도 오류가 있으면 전체 등록이 취소됩니다.");
        System.out.println();
        ConsoleUi.YesNoOptions("계속", "취소");
        System.out.println();
        ConsoleUi.Prompt("계속하시겠습니까? (Y/N)");

        String confirm = scanner.nextLine().trim().toUpperCase();

        if (!confirm.equals("Y")) {
            ConsoleUi.Cancelled();
            return;
        }

        try {
            int insertedCount = productCsvService.ImportProducts(fileName);

            ConsoleUi.Success("CSV 상품 등록이 완료되었습니다.");
            ConsoleUi.Field("등록 상품 수", insertedCount + "개", 14);

        } catch (Exception e) {
            ConsoleUi.Error("CSV 불러오기에 실패했습니다. " + e.getMessage());
            ConsoleUi.Warn("등록된 상품은 없습니다.");
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 재고 현황 CSV 메뉴
    // ============================================================
    private void RunStockCsvMenu() {
        while (true) {
            ConsoleUi.ClearScreen();
            ConsoleUi.ScreenHeader("CSV / STOCK", "재고 현황 CSV");

            ConsoleUi.Section("저장할 범위");
            ConsoleUi.MenuItem("01", "전체 재고 현황");
            ConsoleUi.MenuItem("02", "재고 부족 상품");
            ConsoleUi.MenuItem("03", "품절 상품");
            ConsoleUi.MenuItem("00", "이전");

            System.out.println();
            ConsoleUi.Prompt("선택");

            String input = ConsoleUi.Choice(scanner.nextLine());

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
                    ConsoleUi.InvalidMenu();
                    ConsoleUi.PressEnter(scanner);
            }
        }
    }


    // ============================================================
    // 전체 재고 CSV 저장
    // ============================================================
    private void ExportAllStock() {
        ConsoleUi.Prompt("파일명 (엔터: stock_status.csv)");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportAllStock(fileName);

            ConsoleUi.Success("전체 재고 CSV 저장이 완료되었습니다.");
            ConsoleUi.Field("저장 위치", String.valueOf(path), 10);

        } catch (Exception e) {
            ConsoleUi.Error("CSV 저장에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 재고 부족 상품 CSV 저장
    // ============================================================
    private void ExportLowStock() {
        ConsoleUi.Prompt("파일명 (엔터: low_stock.csv)");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportLowStock(fileName);

            ConsoleUi.Success("재고 부족 상품 CSV 저장이 완료되었습니다.");
            ConsoleUi.Field("저장 위치", String.valueOf(path), 10);

        } catch (Exception e) {
            ConsoleUi.Error("CSV 저장에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 품절 상품 CSV 저장
    // ============================================================
    private void ExportOutOfStock() {
        ConsoleUi.Prompt("파일명 (엔터: out_of_stock.csv)");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportOutOfStock(fileName);

            ConsoleUi.Success("품절 상품 CSV 저장이 완료되었습니다.");
            ConsoleUi.Field("저장 위치", String.valueOf(path), 10);

        } catch (Exception e) {
            ConsoleUi.Error("CSV 저장에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 재고 변경 이력 CSV 저장
    // ============================================================
    private void ExportAdjustmentHistory() {
        ConsoleUi.Prompt("파일명 (엔터: stock_adjustment_history.csv)");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportAdjustmentHistory(fileName);

            ConsoleUi.Success("재고 변경 이력 CSV 저장이 완료되었습니다.");
            ConsoleUi.Field("저장 위치", String.valueOf(path), 10);

        } catch (Exception e) {
            ConsoleUi.Error("CSV 저장에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 시리얼 현황 CSV 저장
    // ============================================================
    private void ExportSerialStatus() {
        ConsoleUi.Prompt("파일명 (엔터: serial_status.csv)");
        String fileName = scanner.nextLine().trim();

        try {
            Path path = csvExportService.ExportSerialStatus(fileName);

            ConsoleUi.Success("시리얼 현황 CSV 저장이 완료되었습니다.");
            ConsoleUi.Field("저장 위치", String.valueOf(path), 10);

        } catch (Exception e) {
            ConsoleUi.Error("CSV 저장에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // CSV 메뉴 출력
    // ============================================================
    private void PrintMenu() {
        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ADMIN / CSV", "CSV 저장 / 불러오기");

        ConsoleUi.Section("상품");
        ConsoleUi.MenuItem("01", "상품 CSV 저장");
        ConsoleUi.MenuItem("02", "상품 CSV 불러오기");

        ConsoleUi.Section("재고 / 시리얼");
        ConsoleUi.MenuItem("03", "재고 현황 CSV 저장");
        ConsoleUi.MenuItem("04", "재고 변경 이력 CSV 저장");
        ConsoleUi.MenuItem("05", "시리얼 현황 CSV 저장");

        ConsoleUi.Section("시스템");
        ConsoleUi.MenuItem("00", "이전");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }
}