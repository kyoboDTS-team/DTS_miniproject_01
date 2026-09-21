package com.team.orderapp.stock;

import com.team.orderapp.common.ConsoleUi;
import com.team.orderapp.product.ProductUnit;
import com.team.orderapp.product.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.team.orderapp.stock.SerialStockConsistency;



/**
 * 관리자 재고 / 시리얼 관리 메뉴
 */
public class StockMenu {

    // 표 열 너비 (한글은 2칸으로 계산)
    private static final int STOCK_TABLE_WIDTH = 80;
    private static final int HISTORY_TABLE_WIDTH = 104;
    private static final int CHECK_TABLE_WIDTH = 80;

    private final Scanner scanner;
    private final StockService stockService;

    // 로그인한 관리자 user_id
    private final Long adminUserId;


    public StockMenu(
            Scanner scanner,
            Long adminUserId
    ) {

        this.scanner = scanner;
        this.adminUserId = adminUserId;
        this.stockService =
                new StockService();
    }


    // ============================================================
    // 재고 / 시리얼 메뉴
    // 담당 : 백종민
    // ============================================================

    public void Run() {

        while (true) {

            PrintMenu();

            String input =
                    scanner.nextLine()
                            .trim();


            switch (input) {

                case "1":
                    AdjustStock();
                    break;


                case "2":
                    RegisterSerial();
                    break;


                case "3":
                    FindSerials();
                    break;

                case "4":
                    RunStockStatusMenu();
                    break;

                case "5":
                    RunAdjustmentHistoryMenu();
                    break;

                case "6":
                    ShowSerialStockConsistency();
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
    // 메뉴 출력
    // 담당 : 백종민
    // ============================================================

    private void PrintMenu() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ADMIN / STOCK", "재고 / 시리얼 관리");

        ConsoleUi.Section("재고");
        ConsoleUi.MenuItem("01", "재고 조정");
        ConsoleUi.MenuItem("04", "재고 현황 / 부족 관리");
        ConsoleUi.MenuItem("05", "재고 변경 이력");

        ConsoleUi.Section("시리얼");
        ConsoleUi.MenuItem("02", "시리얼 등록");
        ConsoleUi.MenuItem("03", "시리얼 조회");
        ConsoleUi.MenuItem("06", "시리얼 재고 정합성 검사");

        ConsoleUi.Section("시스템");
        ConsoleUi.MenuItem("00", "이전");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }


    // ============================================================
    // 일반 상품 재고 조정
    // 담당 : 백종민
    // ============================================================

    private void AdjustStock() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ADMIN", "재고 입고 / 조정");


        try {

            Long productId =
                    ReadLong(
                            "상품 ID"
                    );


            System.out.println();
            ConsoleUi.Info("양수 입력 : 입고");

            ConsoleUi.Info("음수 입력 : 차감");


            int delta =
                    ReadInteger(
                            "변경 수량"
                    );


            String reason =
                    ReadRequiredString(
                            "조정 사유"
                    );


            boolean result =
                    stockService.AdjustStock(
                            productId,
                            delta,
                            reason,
                            adminUserId
                    );


            if (result) {
                System.out.println();
                ConsoleUi.Success("재고가 정상적으로 변경되었습니다.");
            }


        } catch (Exception e) {

            System.out.println();
            ConsoleUi.Error("재고 변경 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // 시리얼 상품 등록
    // 담당 : 백종민
    // ============================================================

    private void RegisterSerial() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("SERIAL / NEW", "시리얼 등록");


        try {

            Long productId =
                    ReadLong(
                            "상품 ID"
                    );


            String serialNumber =
                    ReadRequiredString(
                            "시리얼 번호"
                    );


            boolean result =
                    stockService.RegisterSerial(
                            productId,
                            serialNumber
                    );


            if (result) {

                System.out.println();
                ConsoleUi.Success("시리얼이 정상적으로 등록되었습니다.");
            }


        } catch (Exception e) {

            System.out.println();
            ConsoleUi.Error("시리얼 등록 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // 시리얼 목록 조회
    // 담당 : 백종민
    // ============================================================

    private void FindSerials() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ADMIN", "시리얼 목록 조회");


        try {

            Long productId =
                    ReadLong(
                            "상품 ID"
                    );


            List<ProductUnit> units =
                    stockService.FindSerials(
                            productId
                    );


            if (units.isEmpty()) {

                ConsoleUi.Error("등록된 시리얼이 없습니다.");

                return;
            }


            System.out.println();
            System.out.println(
                    ConsoleUi.Cyan(
                            ConsoleUi.PadRight("번호", 8)
                                    + ConsoleUi.PadRight("시리얼번호", 24)
                                    + ConsoleUi.PadRight("상태", 12)
                    )
            );
            ConsoleUi.Divider(44);


            for (ProductUnit unit : units) {

                System.out.println(
                        ConsoleUi.PadRight(String.valueOf(unit.getProductUnitId()), 8)
                                + ConsoleUi.PadRight(unit.getSerialNumber(), 24)
                                + ConsoleUi.PadRight(ConsoleUi.Status(unit.getUnitStatus()), 12)
                );
            }

            ConsoleUi.Divider(44);


        } catch (Exception e) {

            ConsoleUi.Error("시리얼 조회 실패: " + e.getMessage());
        }
    }

    // ============================================================
    // 재고 현황 / 부족 관리 메뉴
    // ============================================================

    private void RunStockStatusMenu() {

        while (true) {

            ConsoleUi.ClearScreen();
            ConsoleUi.ScreenHeader("STOCK / STATUS", "재고 현황 / 부족 관리");

            ConsoleUi.Section("재고 현황");
            ConsoleUi.MenuItem("01", "전체 재고 현황");
            ConsoleUi.MenuItem("02", "재고 부족 상품");
            ConsoleUi.MenuItem("03", "품절 상품");
            ConsoleUi.MenuItem("00", "이전");

            System.out.println();
            ConsoleUi.Prompt("선택");


            String input =
                    ConsoleUi.Choice(scanner.nextLine());

            switch (input) {
                case "1":
                    ShowAllStockProducts();
                    break;

                case "2":
                    ShowLowStockProducts();
                    break;

                case "3":
                    ShowOutOfStockProducts();
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
    // 전체 재고 현황
    // ============================================================

    private void ShowAllStockProducts() {

        try {

            List<Product> products =
                    stockService.FindAllStockProducts();


            ConsoleUi.ClearScreen();
            ConsoleUi.ScreenHeader("STOCK / ALL", "전체 재고 현황");


            PrintStockProducts(products);

            // 목록에서 바로 입고 가능
            RunStockReceiveFromList(products);

        } catch (Exception e) {

            ConsoleUi.Error("재고 현황 조회 실패: " + e.getMessage());
        }
    }

    // ============================================================
    // 재고 부족 상품
    // ============================================================

    private void ShowLowStockProducts() {

        try {

            List<Product> products =
                    stockService.FindLowStockProducts();


            ConsoleUi.ClearScreen();
            ConsoleUi.ScreenHeader("STOCK / LOW", "재고 부족 상품");

            PrintStockProducts(products);

            // 부족 상품을 보고 바로 입고
            RunStockReceiveFromList(products);

        } catch (Exception e) {
            ConsoleUi.Error("재고 부족 상품 조회 실패: " + e.getMessage());
        }
    }

    // ============================================================
    // 품절 상품
    // ============================================================

    private void ShowOutOfStockProducts() {

        try {

            List<Product> products =
                    stockService.FindOutOfStockProducts();

            ConsoleUi.ClearScreen();
            ConsoleUi.ScreenHeader("STOCK / SOLDOUT", "품절 상품");

            PrintStockProducts(products);

            // 품절 상품을 보고 바로 입고
            RunStockReceiveFromList(products);

        } catch (Exception e) {
            ConsoleUi.Error("품절 상품 조회 실패: " + e.getMessage());
        }
    }

    // ============================================================
// 재고 목록에서 바로 입고 처리
// ============================================================

    private void RunStockReceiveFromList(
            List<Product> products
    ) {

        // 조회 결과가 없으면 입고 메뉴도 실행하지 않음
        if (products == null ||
                products.isEmpty()) {

            return;
        }


        while (true) {

            System.out.println();
            ConsoleUi.Prompt("입고할 상품 ID 입력 (0: 이전)");

            String input =
                    ConsoleUi.Choice(scanner.nextLine());


            if (input.equals("0")) {
                return;
            }

            try {
                Long productId =
                        Long.parseLong(input);

                // 현재 화면에 출력된 상품인지 확인
                Product selectedProduct = null;
                for (Product product : products) {

                    if (product.getProductId()
                            .equals(productId)) {
                        selectedProduct = product;
                        break;
                    }
                }

                if (selectedProduct == null) {

                    ConsoleUi.Info("현재 목록에 없는 상품입니다.");
                    continue;
                }


                System.out.println();
                ConsoleUi.Info("선택 상품: " + selectedProduct .getProductName());


                // 시리얼 관리 상품
                if (Boolean.TRUE.equals(
                        selectedProduct
                                .getRequiresSerial()
                )) {

                    ConsoleUi.Info("시리얼 관리 상품입니다.");

                    ReceiveSerialProduct(
                            selectedProduct
                    );

                } else {

                    // 일반 상품
                    ReceiveNormalProduct(
                            selectedProduct
                    );
                }


                // 한 번 입고 후 목록 화면으로 복귀
                return;

            } catch (NumberFormatException e) {

                ConsoleUi.Error("상품 ID는 숫자로 입력해 주세요.");
            }
        }
    }

    // ============================================================
    // 일반 상품 입고
    // ============================================================

    private void ReceiveNormalProduct(
            Product product
    ) {

        ConsoleUi.Info("현재 재고: " + product.getStockQuantity());

        ConsoleUi.Prompt("입고 수량");

        String quantityInput =
                scanner.nextLine().trim();

        int quantity;

        try {
            quantity =
                    Integer.parseInt(
                            quantityInput
                    );

        } catch (NumberFormatException e) {
            ConsoleUi.Error("입고 수량은 숫자로 입력해 주세요.");

            return;
        }

        if (quantity <= 0) {

            ConsoleUi.Info("입고 수량은 1 이상이어야 합니다.");
            return;
        }

        ConsoleUi.Prompt("입고 사유");

        String reason =
                scanner.nextLine().trim();

        if (reason.isBlank()) {

            ConsoleUi.Error("입고 사유를 입력해 주세요.");

            return;
        }

        try {

            // 기존 재고 조정 Service 재사용
            stockService.AdjustStock(
                    product.getProductId(),
                    quantity,
                    reason,
                    adminUserId
            );

            Product updatedProduct =
                    stockService.FindProductById(
                            product.getProductId()
                    );
            System.out.println();
            ConsoleUi.Success("재고 입고가 완료되었습니다.");

            if (updatedProduct != null) {

                ConsoleUi.Info("현재 재고: " + updatedProduct .getStockQuantity());
            }

        } catch (Exception e) {

            ConsoleUi.Error("재고 입고 실패: " + e.getMessage());
        }
    }

    // ============================================================
    // 시리얼 상품 입고
    // ============================================================

    private void ReceiveSerialProduct(
            Product product
    ) {

        ConsoleUi.Info("현재 재고: " + product.getStockQuantity());

        while (true) {

            System.out.println();
            ConsoleUi.Prompt("등록할 시리얼 번호 (0: 종료)");

            String serialNumber =
                    scanner.nextLine().trim();

            if (serialNumber.equals("0")) {
                return;
            }

            if (serialNumber.isBlank()) {

                ConsoleUi.Error("시리얼 번호를 입력해 주세요.");

                continue;
            }

            try {

                // 기존 시리얼 등록 기능 재사용
                stockService.RegisterSerial(
                        product.getProductId(),
                        serialNumber
                );

                ConsoleUi.Success("시리얼 등록이 완료되었습니다.");

                Product updatedProduct =
                        stockService.FindProductById(
                                product.getProductId()
                        );

                if (updatedProduct != null) {

                    ConsoleUi.Info("현재 재고: " + updatedProduct .getStockQuantity());
                }

                ConsoleUi.Prompt("시리얼을 계속 등록하시겠습니까? (Y/N)");

                String continueInput =
                        scanner.nextLine()
                                .trim()
                                .toUpperCase();

                if (!continueInput.equals("Y")) {
                    return;
                }

            } catch (Exception e) {

                ConsoleUi.Error("시리얼 등록 실패: " + e.getMessage());
            }
        }
    }

    private void RunAdjustmentHistoryMenu() {
        while (true) {
            ConsoleUi.ClearScreen();
            ConsoleUi.ScreenHeader("STOCK / HISTORY", "재고 변경 이력");
            ConsoleUi.Section("변경 이력");
            ConsoleUi.MenuItem("01", "전체 변경 이력");
            ConsoleUi.MenuItem("02", "상품별 변경 이력");
            ConsoleUi.MenuItem("03", "기간별 변경 이력");
            ConsoleUi.MenuItem("00", "이전");

            System.out.println();
            ConsoleUi.Divider(40);
            ConsoleUi.Prompt("선택");

            String input = ConsoleUi.Choice(scanner.nextLine());

            switch (input) {
                case "1":
                    ShowAllAdjustmentHistory();
                    break;
                case "2":
                    ShowAdjustmentHistoryByProduct();
                    break;
                case "3":
                    ShowAdjustmentHistoryByPeriod();
                    break;
                case "0":
                    return;
                default:
                    ConsoleUi.InvalidMenu();
                    ConsoleUi.PressEnter(scanner);
            }
        }
    }

    private void ShowAllAdjustmentHistory() {
        try {
            List<StockAdjustmentHistory> histories =
                    stockService.FindAllAdjustmentHistory();

            System.out.println();
            ConsoleUi.Info("================ 전체 재고 변경 이력 ================");
            PrintAdjustmentHistory(histories);

        } catch (Exception e) {
            ConsoleUi.Error("재고 변경 이력 조회 실패: " + e.getMessage());
        }
    }

    private void ShowAdjustmentHistoryByProduct() {
        ConsoleUi.Prompt("상품 ID");
        String input = ConsoleUi.Choice(scanner.nextLine());

        try {
            Long productId = Long.parseLong(input);

            Product product = stockService.FindProductById(productId);
            if (product == null) {
                ConsoleUi.Info("존재하지 않는 상품입니다.");
                return;
            }

            List<StockAdjustmentHistory> histories =
                    stockService.FindAdjustmentHistoryByProduct(productId);

            System.out.println();
            ConsoleUi.Info("상품: " + product.getProductName());
            ConsoleUi.Info("================ 상품별 재고 변경 이력 ================");
            PrintAdjustmentHistory(histories);

        } catch (NumberFormatException e) {
            ConsoleUi.Error("상품 ID는 숫자로 입력해 주세요.");
        } catch (Exception e) {
            ConsoleUi.Error("재고 변경 이력 조회 실패: " + e.getMessage());
        }
    }

    private void ShowAdjustmentHistoryByPeriod() {
        ConsoleUi.Prompt("시작일 (YYYY-MM-DD)");
        String startInput = scanner.nextLine().trim();

        ConsoleUi.Prompt("종료일 (YYYY-MM-DD)");
        String endInput = scanner.nextLine().trim();

        try {
            LocalDate startDate = LocalDate.parse(startInput);
            LocalDate endDate = LocalDate.parse(endInput);

            List<StockAdjustmentHistory> histories =
                    stockService.FindAdjustmentHistoryByPeriod(startDate, endDate);

            System.out.println();
            ConsoleUi.Field("조회 기간", startDate + " ~ " + endDate, 12);
            ConsoleUi.Info("================ 기간별 재고 변경 이력 ================");
            PrintAdjustmentHistory(histories);

        } catch (DateTimeParseException e) {
            ConsoleUi.Error("날짜는 YYYY-MM-DD 형식으로 입력해 주세요.");
        } catch (Exception e) {
            ConsoleUi.Error("재고 변경 이력 조회 실패: " + e.getMessage());
        }
    }

    private void PrintAdjustmentHistory(List<StockAdjustmentHistory> histories) {
        if (histories == null || histories.isEmpty()) {
            ConsoleUi.Error("조회된 재고 변경 이력이 없습니다.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        System.out.println();
        System.out.println(
                ConsoleUi.Cyan(
                        ConsoleUi.PadRight("변경일시", 18)
                                + ConsoleUi.PadRight("상품코드", 12)
                                + ConsoleUi.PadRight("상품명", 20)
                                + ConsoleUi.PadLeft("변경량", 8)
                                + "  " + ConsoleUi.PadRight("사유", 20)
                                + ConsoleUi.PadRight("관리자", 24)
                )
        );

        ConsoleUi.Divider(HISTORY_TABLE_WIDTH);

        for (StockAdjustmentHistory history : histories) {
            String admin = history.getAdjustedByEmail() != null
                    ? history.getAdjustedByEmail()
                    : "알 수 없음";

            // 입고(+)는 GREEN, 차감(-)은 YELLOW로 구분한다
            String delta = history.getQuantityDelta() > 0
                    ? ConsoleUi.Green("+" + history.getQuantityDelta())
                    : ConsoleUi.Yellow(String.valueOf(history.getQuantityDelta()));

            System.out.println(
                    ConsoleUi.PadRight(history.getAdjustedAt().format(formatter), 18)
                            + ConsoleUi.PadRight(history.getProductCode(), 12)
                            + ConsoleUi.PadRight(
                                    ConsoleUi.Truncate(history.getProductName(), 19), 20)
                            + ConsoleUi.PadLeft(delta, 8)
                            + "  " + ConsoleUi.PadRight(
                                    ConsoleUi.Truncate(history.getReason(), 19), 20)
                            + ConsoleUi.PadRight(
                                    ConsoleUi.Truncate(admin, 23), 24)
            );
        }

        ConsoleUi.Divider(HISTORY_TABLE_WIDTH);
    }

    // ============================================================
    // 재고 상품 목록 공통 출력
    // ============================================================

    private void PrintStockProducts(
            List<Product> products
    ) {

        if (products == null ||
                products.isEmpty()) {

            System.out.println();
            ConsoleUi.Warn("조회된 상품이 없습니다.");

            return;
        }

        System.out.println();
        System.out.println(
                ConsoleUi.Cyan(
                        ConsoleUi.PadRight("ID", 6)
                                + ConsoleUi.PadRight("상품코드", 14)
                                + ConsoleUi.PadRight("상품명", 20)
                                + ConsoleUi.PadLeft("현재재고", 10)
                                + ConsoleUi.PadLeft("안전재고", 10)
                                + "  " + ConsoleUi.PadRight("상태", 10)
                                + ConsoleUi.PadRight("시리얼", 8)
                )
        );

        ConsoleUi.Divider(STOCK_TABLE_WIDTH);


        for (Product product : products) {

            String stockStatus =
                    stockService
                            .GetStockStatus(product);

            String serialStatus =
                    Boolean.TRUE.equals(
                            product.getRequiresSerial()
                    )
                            ? "Y"
                            : "N";

            System.out.println(
                    ConsoleUi.PadRight(String.valueOf(product.getProductId()), 6)
                            + ConsoleUi.PadRight(product.getProductCode(), 14)
                            + ConsoleUi.PadRight(
                                    ConsoleUi.Truncate(product.getProductName(), 19), 20)
                            + ConsoleUi.PadLeft(
                                    ConsoleUi.Stock(product.getStockQuantity(),
                                            product.getReorderLevel()), 10)
                            + ConsoleUi.PadLeft(
                                    String.valueOf(product.getReorderLevel()), 10)
                            + "  " + ConsoleUi.PadRight(stockStatus, 10)
                            + ConsoleUi.PadRight(serialStatus, 8)
            );
        }

        ConsoleUi.Divider(STOCK_TABLE_WIDTH);
    }

    // ============================================================
    // 시리얼 상품 재고 정합성 검사 결과 출력
    // ============================================================
    private void ShowSerialStockConsistency() {
        try {
            List<SerialStockConsistency> results =
                    stockService.CheckSerialStockConsistency();

            ConsoleUi.ClearScreen();
            ConsoleUi.ScreenHeader("ADMIN", "시리얼 상품 재고 정합성 검사");

            if (results == null || results.isEmpty()) {
                System.out.println();
                ConsoleUi.Warn("시리얼 관리 상품이 없습니다.");
                ConsoleUi.PressEnter(scanner);
                return;
            }

            System.out.println();
            System.out.println(
                    ConsoleUi.Cyan(
                            ConsoleUi.PadRight("ID", 6)
                                    + ConsoleUi.PadRight("상품코드", 14)
                                    + ConsoleUi.PadRight("상품명", 20)
                                    + ConsoleUi.PadLeft("DB재고", 10)
                                    + ConsoleUi.PadLeft("AVAILABLE", 12)
                                    + ConsoleUi.PadLeft("차이", 8)
                                    + "  " + ConsoleUi.PadRight("결과", 8)
                    )
            );

            ConsoleUi.Divider(CHECK_TABLE_WIDTH);

            int normalCount = 0;
            int errorCount = 0;

            for (SerialStockConsistency result : results) {
                boolean consistent = result.IsConsistent();

                if (consistent) {
                    normalCount++;
                } else {
                    errorCount++;
                }

                String status = consistent
                        ? ConsoleUi.Green("정상")
                        : ConsoleUi.Red("불일치");

                System.out.println(
                        ConsoleUi.PadRight(String.valueOf(result.getProductId()), 6)
                                + ConsoleUi.PadRight(result.getProductCode(), 14)
                                + ConsoleUi.PadRight(
                                        ConsoleUi.Truncate(result.getProductName(), 19), 20)
                                + ConsoleUi.PadLeft(
                                        String.valueOf(result.getStockQuantity()), 10)
                                + ConsoleUi.PadLeft(
                                        String.valueOf(result.getAvailableUnitCount()), 12)
                                + ConsoleUi.PadLeft(
                                        String.valueOf(result.GetDifference()), 8)
                                + "  " + ConsoleUi.PadRight(status, 8)
                );
            }

            ConsoleUi.Divider(CHECK_TABLE_WIDTH);

            ConsoleUi.Info("검사 결과: 전체 " + results.size() + "개 / 정상 " + normalCount + "개 / 불일치 " + errorCount + "개");

            if (errorCount > 0) {
                ConsoleUi.Error("※ 불일치 상품은 DB 재고와 AVAILABLE 시리얼 수를 확인해 주세요.");
            }

        } catch (Exception e) {
            ConsoleUi.Error("재고 정합성 검사에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 입력 Helper
    // ============================================================

    private Long ReadLong(
            String message
    ) {

        while (true) {

            try {

                ConsoleUi.Prompt(message);

                return Long.parseLong(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                ConsoleUi.Error("숫자를 입력해 주세요.");
            }
        }
    }


    private Integer ReadInteger(
            String message
    ) {

        while (true) {

            try {

                ConsoleUi.Prompt(message);

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                ConsoleUi.Error("숫자를 입력해 주세요.");
            }
        }
    }


    private String ReadRequiredString(
            String message
    ) {

        while (true) {

            ConsoleUi.Prompt(message);

            String value =
                    scanner.nextLine()
                            .trim();


            if (!value.isBlank()) {

                return value;
            }


            ConsoleUi.Error("값을 입력해 주세요.");
        }
    }


}