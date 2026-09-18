package com.team.orderapp.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * 상품 조건 조회 화면을 담당합니다.
 *
 * 담당 기능:
 * 1. 검색 방식 선택
 * 2. 카테고리 번호 입력
 * 3. 최소·최대 가격 입력
 * 4. ProductService 조건 조회 호출
 * 5. 조회된 List<Product> 반환
 *
 * 목록 출력과 상품 상세 화면 이동은
 * ProductMenu에서 처리합니다.
 */
public class ProductConditionMenu {

    private final Scanner scanner;
    private final ProductService productService;


    /**
     * ProductMenu에서 Scanner와 ProductService를 전달받습니다.
     */
    public ProductConditionMenu(
            Scanner scanner,
            ProductService productService
    ) {
        this.scanner = scanner;
        this.productService = productService;
    }


    /**
     * 조건 조회 메뉴를 실행합니다.
     *
     * @return 조건에 맞는 상품 목록
     *         0번을 선택하면 null
     */
    public List<Product> Run() {

        while (true) {

            PrintMenu();

            String input = scanner.nextLine().trim();

            switch (input) {

                case "1":
                    // 카테고리별 상품 조회
                    return FindByCategory();

                case "2":
                    // 가격 범위별 상품 조회
                    return FindByPriceRange();

                case "3":
                    // 카테고리와 가격을 모두 적용해 조회
                    return FindByCategoryAndPriceRange();

                case "0":
                    // 이전 화면으로 이동
                    return null;

                default:
                    System.out.println(
                            "올바른 메뉴 번호를 입력해주세요."
                    );
            }
        }
    }


    /**
     * 카테고리 번호를 입력받아 해당 카테고리의
     * 상품 목록을 조회합니다.
     */
    private List<Product> FindByCategory() {

        long categoryId =
                ReadLong("카테고리 번호 > ");

        return productService
                .FindProductsByCategoryId(categoryId);
    }


    /**
     * 최소 가격과 최대 가격을 입력받아
     * 해당 가격 범위의 상품을 조회합니다.
     */
    private List<Product> FindByPriceRange() {

        BigDecimal minPrice =
                ReadPrice("최소 가격 > ");

        BigDecimal maxPrice =
                ReadPrice("최대 가격 > ");

        return productService.FindByPriceRange(
                minPrice,
                maxPrice
        );
    }


    /**
     * 카테고리, 최소 가격, 최대 가격을 입력받아
     * 모든 조건에 맞는 상품을 조회합니다.
     */
    private List<Product> FindByCategoryAndPriceRange() {

        long categoryId =
                ReadLong("카테고리 번호 > ");

        BigDecimal minPrice =
                ReadPrice("최소 가격 > ");

        BigDecimal maxPrice =
                ReadPrice("최대 가격 > ");

        return productService
                .FindByCategoryAndPriceRange(
                        categoryId,
                        minPrice,
                        maxPrice
                );
    }


    /**
     * 조건 조회 메뉴 출력
     */
    private void PrintMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("              상품 조건 조회");
        System.out.println("========================================");
        System.out.println("1. 카테고리로 조회");
        System.out.println("2. 가격 범위로 조회");
        System.out.println("3. 카테고리 + 가격으로 조회");
        System.out.println("0. 이전");
        System.out.println("----------------------------------------");
        System.out.print("선택 > ");
    }


    /**
     * 카테고리 번호와 같은 양의 정수를 입력받습니다.
     */
    private long ReadLong(String message) {

        while (true) {

            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                long value =
                        Long.parseLong(input);

                // 카테고리 ID는 1 이상이어야 함
                if (value <= 0) {
                    System.out.println(
                            "0보다 큰 번호를 입력해주세요."
                    );
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println(
                        "숫자로 입력해주세요."
                );
            }
        }
    }


    /**
     * 가격을 BigDecimal 형태로 입력받습니다.
     */
    private BigDecimal ReadPrice(String message) {

        while (true) {

            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                BigDecimal price =
                        new BigDecimal(input);

                // 음수 가격은 허용하지 않음
                if (price.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

                    System.out.println(
                            "가격은 0원 이상이어야 합니다."
                    );
                    continue;
                }

                return price;

            } catch (NumberFormatException e) {
                System.out.println(
                        "가격은 숫자로 입력해주세요."
                );
            }
        }
    }
}