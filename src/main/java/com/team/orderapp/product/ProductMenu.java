package com.team.orderapp.product;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 상품 관련 화면의 전체 흐름을 관리하는 클래스입니다.
 *
 * 직접 상품 정보를 출력하거나 입력받기보다는
 * 다음 화면들을 순서대로 연결하는 역할을 합니다.
 *
 * 1. ProductService를 이용해 상품 조회
 * 2. ProductListMenu로 상품 목록 출력
 * 3. 사용자가 선택한 상품 번호 받기
 * 4. ProductDetailMenu로 상품 상세 출력
 */
public class ProductMenu {

    // 상품 DB 조회를 담당하는 Service
    private final ProductService productService;

    // 상품 목록 출력과 상품 선택을 담당
    private final ProductListMenu productListMenu;

    // 카테고리와 가격 조건 입력을 담당
    private final ProductConditionMenu productConditionMenu;

    // 선택된 상품의 상세 화면을 담당
    private final ProductDetailMenu productDetailMenu;


    /**
     * Main에서 생성한 Scanner를 전달받습니다.
     *
     * 모든 메뉴가 같은 Scanner를 공유해야
     * 입력 버퍼 문제를 줄일 수 있습니다.
     */
    public ProductMenu(Scanner scanner) {

        // 상품 기능을 호출하기 위한 Service
        this.productService =
                new ProductService();

        // 목록 화면 생성
        this.productListMenu =
                new ProductListMenu(scanner);

        // 조건 검색 화면 생성
        // 같은 ProductService 객체를 전달하여 사용
        this.productConditionMenu =
                new ProductConditionMenu(
                        scanner,
                        productService
                );

        // 상품 상세 화면 생성
        this.productDetailMenu =
                new ProductDetailMenu(scanner);
    }


    /**
     * 전체 상품 조회를 실행합니다.
     *
     * GuestMenu 또는 MemberMenu의
     * "1. 상품 전체 조회"에서 호출합니다.
     */
    public void ShowAllProducts() {

        try {
            // Service에서 전체 상품 목록 조회
            List<Product> products =
                    productService.FindAllProducts();

            // 목록 출력 → 상품 선택 → 상세 화면 연결
            ShowListAndDetail(
                    products,
                    "상품 전체 조회"
            );

        } catch (Exception e) {
            System.out.println(
                    "상품 전체 조회 중 오류가 발생했습니다."
            );
            System.out.println(e.getMessage());
        }
    }


    /**
     * 조건별 상품 조회를 실행합니다.
     *
     * GuestMenu 또는 MemberMenu의
     * "2. 상품 조건 조회"에서 호출합니다.
     */
    public void ShowProductsByCondition() {

        while (true) {

            try {
                /*
                 * ConditionMenu에서:
                 *
                 * 1. 카테고리 조회
                 * 2. 가격 범위 조회
                 * 3. 카테고리 + 가격 범위 조회
                 *
                 * 중 하나를 선택하고 조회 결과를 받습니다.
                 */
                List<Product> products =
                        productConditionMenu.Run();

                /*
                 * ConditionMenu에서 0번을 선택하면
                 * null이 반환됩니다.
                 *
                 * 이 경우 상품 조건 조회를 끝내고
                 * GuestMenu 또는 MemberMenu로 돌아갑니다.
                 */
                if (products == null) {
                    return;
                }

                // 검색 결과 목록과 상세 화면 연결
                ShowListAndDetail(
                        products,
                        "상품 검색 결과"
                );

                /*
                 * 검색 결과 목록에서 0번을 선택하면
                 * ShowListAndDetail()이 종료됩니다.
                 *
                 * 이후 while문이 다시 실행되어
                 * 상품 조건 선택 화면으로 돌아갑니다.
                 */

            } catch (IllegalArgumentException |
                     IllegalStateException e) {

                // 잘못된 카테고리나 가격 범위 처리
                System.out.println(e.getMessage());

            } catch (Exception e) {

                // 예상하지 못한 DB 또는 프로그램 오류
                System.out.println(
                        "상품 조건 조회 중 오류가 발생했습니다."
                );
                System.out.println(e.getMessage());
            }
        }
    }


    /**
     * 전체 조회와 조건 조회가 공통으로 사용하는 흐름입니다.
     *
     * 1. 상품 목록 출력
     * 2. 상품번호 선택
     * 3. 선택한 상품 상세 조회
     * 4. 상세 화면 출력
     */
    private void ShowListAndDetail(
            List<Product> products,
            String title
    ) {

        // 조회 결과가 없는 경우
        if (products == null || products.isEmpty()) {
            System.out.println(
                    "조회된 상품이 없습니다."
            );
            return;
        }

        while (true) {

            /*
             * 상품 목록을 출력하고
             * 사용자가 선택한 productId를 받습니다.
             */
            Long selectedProductId =
                    productListMenu.SelectProduct(
                            products,
                            title
                    );

            /*
             * ProductListMenu에서 0번을 선택하면
             * null이 반환됩니다.
             */
            if (selectedProductId == null) {
                return;
            }

            // 선택한 상품을 상세 화면으로 연결
            ShowProductDetail(selectedProductId);

            /*
             * 상세 화면에서 0번을 선택하면
             * 다시 while문 처음으로 돌아가
             * 기존 상품 목록을 다시 출력합니다.
             */
        }
    }


    /**
     * 선택한 상품번호로 최신 상품 정보를 다시 조회하고
     * 상세 화면을 실행합니다.
     */
    private void ShowProductDetail(Long productId) {

        // productId로 상품 하나 조회
        Optional<Product> result =
                productService.FindProductById(
                        productId
                );

        // 상품이 삭제됐거나 존재하지 않는 경우
        if (result.isEmpty()) {
            System.out.println(
                    "선택한 상품을 찾을 수 없습니다."
            );
            return;
        }

        // Optional 안에 있는 Product 꺼내기
        Product product = result.get();

        // 상세 화면 실행
        productDetailMenu.Run(product);
    }
}