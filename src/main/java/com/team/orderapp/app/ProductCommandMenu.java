package com.team.orderapp.app;

import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductService;

import java.math.BigDecimal;
import java.util.Scanner;

public class ProductCommandMenu {

    private final Scanner scanner;
    private final ProductService productService;

    public ProductCommandMenu() {
        this.scanner = new Scanner(System.in);
        this.productService = new ProductService();
    }


    // ============================================================
    // 상품 관리 메인
    // ============================================================

    public void Run() {

        while (true) {

            PrintMenu();

            String input = scanner.nextLine().trim();

            switch (input) {

                case "1":
                    RegisterProduct();
                    break;

                case "0":
                    return;

                default:
                    System.out.println("올바른 메뉴 번호를 입력해 주세요.");
            }
        }
    }


    // ============================================================
    // 상품 등록
    // 담당: 백종민
    // ============================================================

    private void RegisterProduct() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("              상품 등록");
        System.out.println("========================================");

        try {

            Product product = new Product();

            // 상품 코드
            product.setProductCode(
                    ReadRequiredString("상품 코드 > ")
            );

            // 상품명
            product.setProductName(
                    ReadRequiredString("상품명 > ")
            );

            // 카테고리 ID
            product.setCategoryId(
                    ReadLong("카테고리 ID > ")
            );

            // 가격
            product.setPrice(
                    ReadBigDecimal("가격 > ")
            );

            // 안전재고
            product.setReorderLevel(
                    ReadInteger("안전재고 > ")
            );

            // 시리얼 관리 여부
            product.setRequiresSerial(
                    ReadBoolean("시리얼 관리 상품입니까? (Y/N) > ")
            );


            boolean result =
                    productService.RegisterProduct(product);


            if (result) {

                System.out.println();
                System.out.println("상품이 정상적으로 등록되었습니다.");

            } else {

                System.out.println();
                System.out.println("상품 등록에 실패했습니다.");
            }

        } catch (Exception e) {

            System.out.println(
                    "입력 오류: " + e.getMessage()
            );
        }
    }


    // ============================================================
    // 상품 수정
    // 담당: 백종민
    // ============================================================

    private void UpdateProduct() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "              상품 수정"
        );
        System.out.println(
                "========================================"
        );


        try {

            Product product =
                    new Product();


            // 어떤 상품을 수정할지 선택
            product.setProductId(
                    ReadLong(
                            "수정할 상품 ID > "
                    )
            );


            // 새로운 상품명
            product.setProductName(
                    ReadRequiredString(
                            "변경할 상품명 > "
                    )
            );


            // 새로운 카테고리
            product.setCategoryId(
                    ReadLong(
                            "변경할 카테고리 ID > "
                    )
            );


            // 새로운 가격
            product.setPrice(
                    ReadBigDecimal(
                            "변경할 가격 > "
                    )
            );


            // 새로운 안전재고 기준
            product.setReorderLevel(
                    ReadInteger(
                            "변경할 안전재고 > "
                    )
            );


            // Service에 수정 요청
            boolean result =
                    productService
                            .UpdateProduct(product);


            if (result) {

                System.out.println();
                System.out.println(
                        "상품이 정상적으로 수정되었습니다."
                );

            } else {

                System.out.println();
                System.out.println(
                        "상품 수정에 실패했습니다."
                );

                System.out.println(
                        "존재하지 않는 상품 ID인지 확인해 주세요."
                );
            }


        } catch (Exception e) {

            System.out.println(
                    "상품 수정 오류: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 화면 출력
    // ============================================================

    private void PrintMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("          상품 / 카테고리 관리");
        System.out.println("========================================");
        System.out.println("1. 상품 등록");

        // 이후 추가 예정
        // 2. 상품 수정
        // 3. 상품 삭제
        // 4. 판매 상태 변경
        // 5. 카테고리 관리

        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }


    // ============================================================
    // 입력 Helper
    // ============================================================

    private String ReadRequiredString(String message) {

        while (true) {

            System.out.print(message);

            String value =
                    scanner.nextLine().trim();

            if (!value.isBlank()) {
                return value;
            }

            System.out.println(
                    "값을 입력해 주세요."
            );
        }
    }


    private Long ReadLong(String message) {

        while (true) {

            try {

                System.out.print(message);

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


    private Integer ReadInteger(String message) {

        while (true) {

            try {

                System.out.print(message);

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


    private BigDecimal ReadBigDecimal(String message) {

        while (true) {

            try {

                System.out.print(message);

                return new BigDecimal(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "올바른 금액을 입력해 주세요."
                );
            }
        }
    }


    private Boolean ReadBoolean(String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase();

            if (input.equals("Y")) {
                return true;
            }

            if (input.equals("N")) {
                return false;
            }

            System.out.println(
                    "Y 또는 N을 입력해 주세요."
            );
        }
    }
}