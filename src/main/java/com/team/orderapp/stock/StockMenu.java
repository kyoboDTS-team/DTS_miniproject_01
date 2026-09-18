package com.team.orderapp.stock;

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
                    System.out.println(
                            "올바른 메뉴 번호를 입력해 주세요."
                    );
            }
        }
    }

    // ============================================================
    // 메뉴 출력
    // 담당 : 백종민
    // ============================================================

    private void PrintMenu() {

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "          재고 / 시리얼 관리"
        );

        System.out.println(
                "========================================"
        );

        System.out.println("1. 재고 조정");
        System.out.println("2. 시리얼 등록");
        System.out.println("3. 시리얼 조회");
        System.out.println("4. 재고 현황 / 부족 관리");
        System.out.println("5. 재고 변경 이력");
        System.out.println("6. 시리얼 재고 정합성 검사");
        System.out.println("0. 이전");
        System.out.println(
                "----------------------------------------"
        );

        System.out.print(
                "선택 > "
        );
    }


    // ============================================================
    // 일반 상품 재고 조정
    // 담당 : 백종민
    // ============================================================

    private void AdjustStock() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "           재고 입고 / 조정"
        );
        System.out.println(
                "========================================"
        );


        try {

            Long productId =
                    ReadLong(
                            "상품 ID > "
                    );


            System.out.println();
            System.out.println(
                    "양수 입력 : 입고"
            );

            System.out.println(
                    "음수 입력 : 차감"
            );


            int delta =
                    ReadInteger(
                            "변경 수량 > "
                    );


            String reason =
                    ReadRequiredString(
                            "조정 사유 > "
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
                System.out.println(
                        "재고가 정상적으로 변경되었습니다."
                );
            }


        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "재고 변경 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 시리얼 상품 등록
    // 담당 : 백종민
    // ============================================================

    private void RegisterSerial() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "            시리얼 등록"
        );
        System.out.println(
                "========================================"
        );


        try {

            Long productId =
                    ReadLong(
                            "상품 ID > "
                    );


            String serialNumber =
                    ReadRequiredString(
                            "시리얼 번호 > "
                    );


            boolean result =
                    stockService.RegisterSerial(
                            productId,
                            serialNumber
                    );


            if (result) {

                System.out.println();
                System.out.println(
                        "시리얼이 정상적으로 등록되었습니다."
                );
            }


        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "시리얼 등록 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 시리얼 목록 조회
    // 담당 : 백종민
    // ============================================================

    private void FindSerials() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "           시리얼 목록 조회"
        );
        System.out.println(
                "========================================"
        );


        try {

            Long productId =
                    ReadLong(
                            "상품 ID > "
                    );


            List<ProductUnit> units =
                    stockService.FindSerials(
                            productId
                    );


            if (units.isEmpty()) {

                System.out.println(
                        "등록된 시리얼이 없습니다."
                );

                return;
            }


            System.out.println();
            System.out.println(
                    "번호 | 시리얼번호 | 상태"
            );

            System.out.println(
                    "----------------------------------------"
            );


            for (ProductUnit unit : units) {

                System.out.println(
                        unit.getProductUnitId()
                                + " | "
                                + unit.getSerialNumber()
                                + " | "
                                + unit.getUnitStatus()
                );
            }


        } catch (Exception e) {

            System.out.println(
                    "시리얼 조회 실패: "
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // 재고 현황 / 부족 관리 메뉴
    // ============================================================

    private void RunStockStatusMenu() {

        while (true) {

            System.out.println();
            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "          재고 현황 / 부족 관리"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println("1. 전체 재고 현황");
            System.out.println("2. 재고 부족 상품");
            System.out.println("3. 품절 상품");
            System.out.println("0. 이전");

            System.out.println(
                    "----------------------------------------"
            );

            System.out.print("선택 > ");


            String input =
                    scanner.nextLine().trim();

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
                    System.out.println(
                            "올바른 메뉴 번호를 입력해 주세요."
                    );
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


            System.out.println();
            System.out.println(
                    "=========================================================================="
            );

            System.out.println(
                    "                           전체 재고 현황"
            );

            System.out.println(
                    "=========================================================================="
            );


            PrintStockProducts(products);

            // 목록에서 바로 입고 가능
            RunStockReceiveFromList(products);

        } catch (Exception e) {

            System.out.println(
                    "재고 현황 조회 실패: "
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // 재고 부족 상품
    // ============================================================

    private void ShowLowStockProducts() {

        try {

            List<Product> products =
                    stockService.FindLowStockProducts();


            System.out.println();
            System.out.println(
                    "=========================================================================="
            );

            System.out.println(
                    "                          재고 부족 상품"
            );

            System.out.println(
                    "=========================================================================="
            );

            PrintStockProducts(products);

            // 부족 상품을 보고 바로 입고
            RunStockReceiveFromList(products);

        } catch (Exception e) {
            System.out.println(
                    "재고 부족 상품 조회 실패: "
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // 품절 상품
    // ============================================================

    private void ShowOutOfStockProducts() {

        try {

            List<Product> products =
                    stockService.FindOutOfStockProducts();

            System.out.println();
            System.out.println(
                    "=========================================================================="
            );
            System.out.println(
                    "                              품절 상품"
            );
            System.out.println(
                    "=========================================================================="
            );

            PrintStockProducts(products);

            // 품절 상품을 보고 바로 입고
            RunStockReceiveFromList(products);

        } catch (Exception e) {
            System.out.println(
                    "품절 상품 조회 실패: "
                            + e.getMessage()
            );
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
            System.out.print(
                    "입고할 상품 ID 입력 (0: 이전) > "
            );

            String input =
                    scanner.nextLine().trim();


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

                    System.out.println(
                            "현재 목록에 없는 상품입니다."
                    );
                    continue;
                }


                System.out.println();
                System.out.println(
                        "선택 상품: "
                                + selectedProduct
                                .getProductName()
                );


                // 시리얼 관리 상품
                if (Boolean.TRUE.equals(
                        selectedProduct
                                .getRequiresSerial()
                )) {

                    System.out.println(
                            "시리얼 관리 상품입니다."
                    );

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

                System.out.println(
                        "상품 ID는 숫자로 입력해 주세요."
                );
            }
        }
    }

    // ============================================================
    // 일반 상품 입고
    // ============================================================

    private void ReceiveNormalProduct(
            Product product
    ) {

        System.out.println(
                "현재 재고: "
                        + product.getStockQuantity()
        );

        System.out.print(
                "입고 수량 > "
        );

        String quantityInput =
                scanner.nextLine().trim();

        int quantity;

        try {
            quantity =
                    Integer.parseInt(
                            quantityInput
                    );

        } catch (NumberFormatException e) {
            System.out.println(
                    "입고 수량은 숫자로 입력해 주세요."
            );

            return;
        }

        if (quantity <= 0) {

            System.out.println(
                    "입고 수량은 1 이상이어야 합니다."
            );
            return;
        }

        System.out.print(
                "입고 사유 > "
        );

        String reason =
                scanner.nextLine().trim();

        if (reason.isBlank()) {

            System.out.println(
                    "입고 사유를 입력해 주세요."
            );

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
            System.out.println(
                    "재고 입고가 완료되었습니다."
            );

            if (updatedProduct != null) {

                System.out.println(
                        "현재 재고: "
                                + updatedProduct
                                .getStockQuantity()
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "재고 입고 실패: "
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // 시리얼 상품 입고
    // ============================================================

    private void ReceiveSerialProduct(
            Product product
    ) {

        System.out.println(
                "현재 재고: "
                        + product.getStockQuantity()
        );

        while (true) {

            System.out.println();
            System.out.print(
                    "등록할 시리얼 번호 "
                            + "(0: 종료) > "
            );

            String serialNumber =
                    scanner.nextLine().trim();

            if (serialNumber.equals("0")) {
                return;
            }

            if (serialNumber.isBlank()) {

                System.out.println(
                        "시리얼 번호를 입력해 주세요."
                );

                continue;
            }

            try {

                // 기존 시리얼 등록 기능 재사용
                stockService.RegisterSerial(
                        product.getProductId(),
                        serialNumber
                );

                System.out.println(
                        "시리얼 등록이 완료되었습니다."
                );

                Product updatedProduct =
                        stockService.FindProductById(
                                product.getProductId()
                        );

                if (updatedProduct != null) {

                    System.out.println(
                            "현재 재고: "
                                    + updatedProduct
                                    .getStockQuantity()
                    );
                }

                System.out.print(
                        "시리얼을 계속 등록하시겠습니까? "
                                + "(Y/N) > "
                );

                String continueInput =
                        scanner.nextLine()
                                .trim()
                                .toUpperCase();

                if (!continueInput.equals("Y")) {
                    return;
                }

            } catch (Exception e) {

                System.out.println(
                        "시리얼 등록 실패: "
                                + e.getMessage()
                );
            }
        }
    }

    private void RunAdjustmentHistoryMenu() {
        while (true) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("             재고 변경 이력");
            System.out.println("========================================");
            System.out.println("1. 전체 변경 이력");
            System.out.println("2. 상품별 변경 이력");
            System.out.println("3. 기간별 변경 이력");
            System.out.println("0. 이전");
            System.out.println("----------------------------------------");
            System.out.print("선택 > ");

            String input = scanner.nextLine().trim();

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
                    System.out.println("올바른 메뉴 번호를 입력해 주세요.");
            }
        }
    }

    private void ShowAllAdjustmentHistory() {
        try {
            List<StockAdjustmentHistory> histories =
                    stockService.FindAllAdjustmentHistory();

            System.out.println();
            System.out.println("================ 전체 재고 변경 이력 ================");
            PrintAdjustmentHistory(histories);

        } catch (Exception e) {
            System.out.println("재고 변경 이력 조회 실패: " + e.getMessage());
        }
    }

    private void ShowAdjustmentHistoryByProduct() {
        System.out.print("상품 ID > ");
        String input = scanner.nextLine().trim();

        try {
            Long productId = Long.parseLong(input);

            Product product = stockService.FindProductById(productId);
            if (product == null) {
                System.out.println("존재하지 않는 상품입니다.");
                return;
            }

            List<StockAdjustmentHistory> histories =
                    stockService.FindAdjustmentHistoryByProduct(productId);

            System.out.println();
            System.out.println("상품: " + product.getProductName());
            System.out.println("================ 상품별 재고 변경 이력 ================");
            PrintAdjustmentHistory(histories);

        } catch (NumberFormatException e) {
            System.out.println("상품 ID는 숫자로 입력해 주세요.");
        } catch (Exception e) {
            System.out.println("재고 변경 이력 조회 실패: " + e.getMessage());
        }
    }

    private void ShowAdjustmentHistoryByPeriod() {
        System.out.print("시작일 (YYYY-MM-DD) > ");
        String startInput = scanner.nextLine().trim();

        System.out.print("종료일 (YYYY-MM-DD) > ");
        String endInput = scanner.nextLine().trim();

        try {
            LocalDate startDate = LocalDate.parse(startInput);
            LocalDate endDate = LocalDate.parse(endInput);

            List<StockAdjustmentHistory> histories =
                    stockService.FindAdjustmentHistoryByPeriod(startDate, endDate);

            System.out.println();
            System.out.println(startDate + " ~ " + endDate);
            System.out.println("================ 기간별 재고 변경 이력 ================");
            PrintAdjustmentHistory(histories);

        } catch (DateTimeParseException e) {
            System.out.println("날짜는 YYYY-MM-DD 형식으로 입력해 주세요.");
        } catch (Exception e) {
            System.out.println("재고 변경 이력 조회 실패: " + e.getMessage());
        }
    }

    private void PrintAdjustmentHistory(List<StockAdjustmentHistory> histories) {
        if (histories == null || histories.isEmpty()) {
            System.out.println("조회된 재고 변경 이력이 없습니다.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        System.out.printf(
                "%-18s %-12s %-18s %-8s %-20s %-25s%n",
                "변경일시", "상품코드", "상품명", "변경량", "사유", "관리자"
        );

        System.out.println(
                "------------------------------------------------------------------------------------------------"
        );

        for (StockAdjustmentHistory history : histories) {
            String admin = history.getAdjustedByEmail() != null
                    ? history.getAdjustedByEmail()
                    : "알 수 없음";

            String delta = history.getQuantityDelta() > 0
                    ? "+" + history.getQuantityDelta()
                    : String.valueOf(history.getQuantityDelta());

            System.out.printf(
                    "%-18s %-12s %-18s %-8s %-20s %-25s%n",
                    history.getAdjustedAt().format(formatter),
                    history.getProductCode(),
                    history.getProductName(),
                    delta,
                    history.getReason(),
                    admin
            );
        }
    }

    // ============================================================
    // 재고 상품 목록 공통 출력
    // ============================================================

    private void PrintStockProducts(
            List<Product> products
    ) {

        if (products == null ||
                products.isEmpty()) {

            System.out.println(
                    "조회된 상품이 없습니다."
            );

            return;
        }

        System.out.printf(
                "%-6s %-14s %-20s %-10s %-10s %-8s %-8s%n",
                "ID",
                "상품코드",
                "상품명",
                "현재재고",
                "안전재고",
                "상태",
                "시리얼"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );


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

            System.out.printf(
                    "%-6d %-14s %-20s %-10d %-10d %-8s %-8s%n",

                    product.getProductId(),
                    product.getProductCode(),
                    product.getProductName(),
                    product.getStockQuantity(),
                    product.getReorderLevel(),
                    stockStatus,
                    serialStatus
            );
        }
    }

    // ============================================================
    // 시리얼 상품 재고 정합성 검사 결과 출력
    // ============================================================
    private void ShowSerialStockConsistency() {
        try {
            List<SerialStockConsistency> results =
                    stockService.CheckSerialStockConsistency();

            System.out.println();
            System.out.println("================================================================================");
            System.out.println("                         시리얼 상품 재고 정합성 검사");
            System.out.println("================================================================================");

            if (results == null || results.isEmpty()) {
                System.out.println("시리얼 관리 상품이 없습니다.");
                return;
            }

            System.out.printf(
                    "%-6s %-14s %-20s %-10s %-12s %-8s %-8s%n",
                    "ID", "상품코드", "상품명", "DB재고", "AVAILABLE", "차이", "결과"
            );

            System.out.println(
                    "--------------------------------------------------------------------------------"
            );

            int normalCount = 0;
            int errorCount = 0;

            for (SerialStockConsistency result : results) {
                boolean consistent = result.IsConsistent();

                if (consistent) {
                    normalCount++;
                } else {
                    errorCount++;
                }

                String status = consistent ? "정상" : "불일치";

                System.out.printf(
                        "%-6d %-14s %-20s %-10d %-12d %-8d %-8s%n",
                        result.getProductId(),
                        result.getProductCode(),
                        result.getProductName(),
                        result.getStockQuantity(),
                        result.getAvailableUnitCount(),
                        result.GetDifference(),
                        status
                );
            }

            System.out.println(
                    "--------------------------------------------------------------------------------"
            );

            System.out.println(
                    "검사 결과: 전체 " + results.size()
                            + "개 / 정상 " + normalCount
                            + "개 / 불일치 " + errorCount + "개"
            );

            if (errorCount > 0) {
                System.out.println(
                        "※ 불일치 상품은 DB 재고와 AVAILABLE 시리얼 수를 확인해 주세요."
                );
            }

        } catch (Exception e) {
            System.out.println("재고 정합성 검사 실패: " + e.getMessage());
        }
    }


    // ============================================================
    // 입력 Helper
    // ============================================================

    private Long ReadLong(
            String message
    ) {

        while (true) {

            try {

                System.out.print(
                        message
                );

                return Long.parseLong(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "숫자를 입력해 주세요."
                );
            }
        }
    }


    private Integer ReadInteger(
            String message
    ) {

        while (true) {

            try {

                System.out.print(
                        message
                );

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "숫자를 입력해 주세요."
                );
            }
        }
    }


    private String ReadRequiredString(
            String message
    ) {

        while (true) {

            System.out.print(
                    message
            );

            String value =
                    scanner.nextLine()
                            .trim();


            if (!value.isBlank()) {

                return value;
            }


            System.out.println(
                    "값을 입력해 주세요."
            );
        }
    }


}