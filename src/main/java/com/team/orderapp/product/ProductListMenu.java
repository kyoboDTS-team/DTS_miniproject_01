package com.team.orderapp.product;

import java.util.List;
import java.util.Scanner;

public class ProductListMenu {

    private final Scanner scanner;

    public ProductListMenu(Scanner scanner) {
        this.scanner = scanner;
    }


    /**
     * 상품 목록을 출력하고 선택한 상품 ID를 반환합니다.
     ** @param products 출력할 상품 목록
     *      * @param title 화면 제목
     *      * @return 선택한 productId
     *      *         0을 선택하면 null
     */
    public Long SelectProduct(List<Product> products, String title) {

        if (products == null || products.isEmpty()) {
            System.out.println("조회된 상품이 없습니다.");
            return null;
        }

        while (true) {

            PrintProducts(products, title);

            System.out.print("상품 번호 선택 > ");
            String input = scanner.nextLine().trim();

            // 이전 화면
            if (input.equals("0")) {
                return null;
            }

            try {
                long productId = Long.parseLong(input);

                // 현재 출력된 목록에 존재하는 상품인지 확인
                if (!ContainsProduct(products, productId)) {

                    System.out.println("목록에 있는 상품 번호를 입력해주세요.");
                    continue;
                }

                return productId;

            } catch (NumberFormatException e) {

                System.out.println("상품 번호는 숫자로 입력해주세요.");
            }
        }
    }


    /**
     * 상품 목록 출력
     */
    private void PrintProducts(List<Product> products, String title) {

        System.out.println();
        System.out.println("========================================");
        System.out.println("              " + title);
        System.out.println("========================================");

        System.out.printf(
                "%-6s %-10s %-20s %12s %8s%n",
                "번호",
                "상품코드",
                "상품명",
                "가격",
                "재고"
        );

        System.out.println("------------------------------------------------------------");

        for (Product product : products) {

            System.out.printf(
                    "%-6d %-10s %-20s %,12d원 %8d%n",
                    product.getProductId(),
                    product.getProductCode(),
                    product.getProductName(),
                    product.getPrice().longValue(),
                    product.getStockQuantity()
            );
        }

        System.out.println(
                "------------------------------------------------------------"
        );
        System.out.println("상세 조회할 상품 번호를 선택하세요.");
        System.out.println("0. 이전");
        System.out.println(
                "------------------------------------------------------------"
        );
    }


    /**
     * 사용자가 선택한 상품이 현재 목록에 존재하는지 확인
     */
    private boolean ContainsProduct(
            List<Product> products,
            long productId
    ) {

        for (Product product : products) {

            if (product.getProductId() == productId) {
                return true;
            }
        }

        return false;
    }
}