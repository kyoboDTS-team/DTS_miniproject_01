package com.team.orderapp.product;

import com.team.orderapp.cart.CartService;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

public class ProductDetailMenu {

    private final Scanner scanner;
    private final CartService cartService;

    public ProductDetailMenu(Scanner scanner) {
        this.scanner = scanner;
        this.cartService = new CartService();
    }


    /**
     * 상품 상세 화면 실행
     */
    public void Run(Product product) {

        if (product == null) {
            System.out.println("상품 정보가 없습니다.");
            return;
        }

        while (true) {

            PrintProductDetail(product);

            // 판매 중지 또는 품절이면 장바구니 담기 불가능
            if (!CanAddToCart(product)) {
                System.out.println("0. 이전");
                System.out.println("----------------------------------------");
                System.out.print("선택 > ");

                String input = scanner.nextLine().trim();

                if (input.equals("0")) {
                    return;
                }

                System.out.println("0번을 입력해주세요.");
                continue;
            }

            System.out.println("1. 장바구니 담기");
            System.out.println("0. 이전");
            System.out.println("----------------------------------------");
            System.out.print("선택 > ");

            String input = scanner.nextLine().trim();

            switch (input) {

                case "1":
                    AddToCart(product);
                    break;

                case "0":
                    return;

                default:
                    System.out.println(
                            "올바른 메뉴 번호를 입력해주세요."
                    );
            }
        }
    }


    /**
     * 상품 상세 정보 출력
     */
    private void PrintProductDetail(Product product) {

        NumberFormat numberFormat =
                NumberFormat.getNumberInstance(Locale.KOREA);

        String price =
                numberFormat.format(product.getPrice());

        String saleStatus =
                ConvertSaleStatus(product);

        System.out.println();
        System.out.println("========================================");
        System.out.println("              상품 상세");
        System.out.println("========================================");
        System.out.println(
                "상품번호      : " + product.getProductId()
        );
        System.out.println(
                "상품코드      : " + product.getProductCode()
        );
        System.out.println(
                "상품명        : " + product.getProductName()
        );
        System.out.println(
                "카테고리 번호 : " + product.getCategoryId()
        );
        System.out.println(
                "가격          : " + price + "원"
        );
        System.out.println(
                "현재 재고     : "
                        + product.getStockQuantity() + "개"
        );
        System.out.println(
                "판매 상태     : " + saleStatus
        );
        System.out.println(
                "시리얼 관리   : "
                        + ConvertSerialStatus(product)
        );
        System.out.println("----------------------------------------");

        if (!"SELLING".equals(product.getSaleStatus())) {
            System.out.println(
                    "현재 판매하지 않는 상품입니다."
            );
        } else if (product.getStockQuantity() == null ||
                product.getStockQuantity() <= 0) {

            System.out.println(
                    "현재 품절된 상품입니다."
            );
        }
    }


    /**
     * 장바구니에 담을 수 있는 상품인지 확인
     */
    private boolean CanAddToCart(Product product) {

        if (!"SELLING".equals(product.getSaleStatus())) {
            return false;
        }

        return product.getStockQuantity() != null
                && product.getStockQuantity() > 0;
    }


    /**
     * 장바구니 담기
     */
    private void AddToCart(Product product) {

        int quantity =
                ReadQuantity(product.getStockQuantity());

        /*
         * CartService가 완성되면 이 위치에서 호출
         *
         * 예:
         * cartService.AddProduct(product, quantity);
         */

        cartService.AddProduct(product, quantity);
        System.out.println();
        System.out.println(
                product.getProductName()
                        + " " + quantity
                        + "개를 장바구니에 담도록 요청했습니다."
        );
    }


    /**
     * 장바구니 수량 입력
     */
    private int ReadQuantity(int stockQuantity) {

        while (true) {

            System.out.print("수량 > ");
            String input = scanner.nextLine().trim();

            try {
                int quantity =
                        Integer.parseInt(input);

                if (quantity <= 0) {
                    System.out.println(
                            "수량은 1개 이상이어야 합니다."
                    );
                    continue;
                }

                if (quantity > stockQuantity) {
                    System.out.println(
                            "현재 재고보다 많은 수량은 "
                                    + "담을 수 없습니다."
                    );
                    System.out.println(
                            "현재 재고: "
                                    + stockQuantity + "개"
                    );
                    continue;
                }

                return quantity;

            } catch (NumberFormatException e) {
                System.out.println(
                        "수량은 숫자로 입력해주세요."
                );
            }
        }
    }


    /**
     * 판매 상태를 화면용 한글로 변환
     */
    private String ConvertSaleStatus(Product product) {

        if (!"SELLING".equals(product.getSaleStatus())) {
            return "판매중지";
        }

        if (product.getStockQuantity() == null ||
                product.getStockQuantity() <= 0) {

            return "품절";
        }

        return "판매중";
    }


    /**
     * 시리얼 관리 여부를 화면용 문자열로 변환
     */
    private String ConvertSerialStatus(Product product) {

        if (Boolean.TRUE.equals(
                product.getRequiresSerial()
        )) {
            return "필요";
        }

        return "해당 없음";
    }
}