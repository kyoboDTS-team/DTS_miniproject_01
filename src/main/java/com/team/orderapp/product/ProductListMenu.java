package com.team.orderapp.product;

import com.team.orderapp.common.ConsoleUi;

import java.util.List;
import java.util.Scanner;

public class ProductListMenu {

    // 표 열 너비 (한글은 2칸으로 계산)
    private static final int COL_NO = 6;
    private static final int COL_CODE = 9;
    private static final int COL_NAME = 20;
    private static final int COL_PRICE = 13;
    private static final int COL_STOCK = 7;
    private static final int TABLE_WIDTH =
            COL_NO + COL_CODE + COL_NAME + COL_PRICE + COL_STOCK;

    private final Scanner scanner;

    public ProductListMenu(Scanner scanner) {
        this.scanner = scanner;
    }


    /**
     * 상품 목록을 출력하고 선택한 상품 ID를 반환합니다.
     *
     * @param products 출력할 상품 목록
     * @param title 화면 제목
     * @param screenCode 화면 코드 (예: PRODUCT / ALL)
     * @return 선택한 productId. 0을 선택하면 null
     */
    public Long SelectProduct(List<Product> products, String title, String screenCode) {

        if (products == null || products.isEmpty()) {
            ConsoleUi.Warn("조회된 상품이 없습니다.");
            ConsoleUi.PressEnter(scanner);
            return null;
        }

        while (true) {

            PrintProducts(products, title, screenCode);

            ConsoleUi.Prompt("선택");
            String input = scanner.nextLine().trim();

            // 이전 화면
            if (ConsoleUi.Choice(input).equals("0")) {
                return null;
            }

            try {
                long productId = Long.parseLong(input);

                // 현재 출력된 목록에 존재하는 상품인지 확인
                if (!ContainsProduct(products, productId)) {

                    ConsoleUi.Error("목록에 있는 상품 번호를 입력해 주세요.");
                    ConsoleUi.PressEnter(scanner);
                    continue;
                }

                return productId;

            } catch (NumberFormatException e) {

                ConsoleUi.Error("상품 번호는 숫자로 입력해 주세요.");
                ConsoleUi.PressEnter(scanner);
            }
        }
    }


    /**
     * 화면 코드를 생략하면 전체 상품 화면으로 표시합니다.
     */
    public Long SelectProduct(List<Product> products, String title) {
        return SelectProduct(products, title, "PRODUCT / ALL");
    }


    /**
     * 상품 목록 출력
     */
    private void PrintProducts(List<Product> products, String title, String screenCode) {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader(screenCode, title, products.size() + "건");

        System.out.println();
        System.out.println(
                ConsoleUi.Cyan(
                        ConsoleUi.PadRight("번호", COL_NO)
                                + ConsoleUi.PadRight("코드", COL_CODE)
                                + ConsoleUi.PadRight("상품명", COL_NAME)
                                + ConsoleUi.PadLeft("가격", COL_PRICE)
                                + ConsoleUi.PadLeft("재고", COL_STOCK)
                )
        );
        ConsoleUi.Divider(TABLE_WIDTH);

        for (Product product : products) {

            // 재고는 상태에 따라 색이 달라지므로, 색을 입히기 전 값으로 자리를 맞춘 뒤 색을 입힌다
            String stock = ConsoleUi.Stock(
                    product.getStockQuantity(),
                    product.getReorderLevel()
            );

            System.out.println(
                    ConsoleUi.PadRight(String.format("%02d", product.getProductId()), COL_NO)
                            + ConsoleUi.PadRight(product.getProductCode(), COL_CODE)
                            + ConsoleUi.PadRight(
                                    ConsoleUi.Truncate(product.getProductName(), COL_NAME - 1),
                                    COL_NAME)
                            + ConsoleUi.PadLeft(
                                    ConsoleUi.Money(product.getPrice()) + "원", COL_PRICE)
                            + ConsoleUi.PadLeft(stock, COL_STOCK)
            );
        }

        ConsoleUi.Divider(TABLE_WIDTH);

        System.out.println();
        ConsoleUi.Info("상품 번호를 입력하면 상세 정보를 확인합니다.");
        ConsoleUi.Option("0", "이전");
        System.out.println();
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
