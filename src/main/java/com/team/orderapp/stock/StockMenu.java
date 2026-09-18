package com.team.orderapp.stock;

import com.team.orderapp.product.ProductUnit;

import java.util.List;
import java.util.Scanner;

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

        System.out.println(
                "1. 일반 상품 재고 입고 / 조정"
        );

        System.out.println(
                "2. 시리얼 상품 입고 / 시리얼 등록"
        );

        System.out.println(
                "3. 시리얼 목록 조회"
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