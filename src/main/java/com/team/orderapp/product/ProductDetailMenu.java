package com.team.orderapp.product;

import com.team.orderapp.cart.CartMenu;
import com.team.orderapp.cart.CartService;
import com.team.orderapp.common.ConsoleUi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProductDetailMenu {

    private final Scanner scanner;
    private final CartService cartService;
    private final CategoryService categoryService;

    // 카테고리 이름은 화면 표시용이라 한 번만 읽어 재사용한다
    private Map<Long, String> categoryNames;

    public ProductDetailMenu(Scanner scanner) {
        this.scanner = scanner;
        this.cartService = new CartService();
        this.categoryService = new CategoryService();
    }


    /**
     * 상품 상세 화면 실행
     */
    public void Run(Product product) {

        if (product == null) {
            ConsoleUi.Error("존재하지 않는 상품입니다.");
            return;
        }

        while (true) {

            PrintProductDetail(product);

            boolean canAddToCart = CanAddToCart(product);

            if (canAddToCart) {
                ConsoleUi.Option("1", "장바구니 담기");
            }

            ConsoleUi.Option("0", "이전");
            System.out.println();
            ConsoleUi.Prompt("선택");

            String input = ConsoleUi.Choice(scanner.nextLine());

            if (input.equals("0")) {
                return;
            }

            if (input.equals("1") && canAddToCart) {

                // 담기에 성공한 뒤 [0]을 고르면 상품 목록으로 돌아간다
                if (!AddToCart(product)) {
                    return;
                }

                continue;
            }

            ConsoleUi.InvalidMenu();
            ConsoleUi.PressEnter(scanner);
        }
    }


    /**
     * 상품 상세 정보 출력
     */
    private void PrintProductDetail(Product product) {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("PRODUCT / DETAIL", product.getProductCode());

        System.out.println();
        ConsoleUi.Field("상품명", product.getProductName());
        ConsoleUi.Field("카테고리", FindCategoryName(product.getCategoryId()));
        ConsoleUi.Field("가격", ConsoleUi.Money(product.getPrice()) + "원");
        ConsoleUi.Field("현재 재고",
                ConsoleUi.Stock(product.getStockQuantity(), product.getReorderLevel()));
        ConsoleUi.Field("판매 상태", FormatSaleStatus(product));
        ConsoleUi.Field("시리얼 관리", ConvertSerialStatus(product));

        System.out.println();
        ConsoleUi.Divider();

        if (!"SELLING".equals(product.getSaleStatus())) {
            ConsoleUi.Error("현재 판매하지 않는 상품입니다.");
        } else if (product.getStockQuantity() == null ||
                product.getStockQuantity() <= 0) {

            ConsoleUi.Error("현재 품절된 상품입니다.");
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
     *
     * @return 이 화면에 머무르면 true, 상품 목록으로 돌아가면 false
     */
    private boolean AddToCart(Product product) {

        Integer quantity = ReadQuantity(product.getStockQuantity());

        // 수량 입력에서 0을 누르면 담기를 취소한다
        if (quantity == null) {
            ConsoleUi.Cancelled();
            return true;
        }

        try {
            cartService.AddProduct(product, quantity);

        } catch (IllegalArgumentException e) {
            ConsoleUi.Error(e.getMessage());
            ConsoleUi.PressEnter(scanner);
            return true;

        } catch (RuntimeException e) {
            ConsoleUi.Error("장바구니 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            ConsoleUi.PressEnter(scanner);
            return true;
        }

        System.out.println();
        ConsoleUi.Success("장바구니에 추가되었습니다.");
        ConsoleUi.Info("현재 장바구니 " + ConsoleUi.Yellow("(" + cartService.GetCartCount() + ")"));

        return AskNextStep();
    }


    /**
     * 담기 직후 다음 행동을 고르는 헬퍼 메서드입니다.
     *
     * @return 상품 상세에 머무르면 true, 상품 목록으로 돌아가면 false
     */
    private boolean AskNextStep() {

        while (true) {

            System.out.println();
            ConsoleUi.Option("1", "계속 보기");
            ConsoleUi.Option("2", "장바구니 보기");
            ConsoleUi.Option("0", "상품 목록으로");
            System.out.println();
            ConsoleUi.Prompt("선택");

            String input = ConsoleUi.Choice(scanner.nextLine());

            switch (input) {

                case "1":
                    return true;

                case "2":
                    new CartMenu(scanner).Run();
                    return true;

                case "0":
                    return false;

                default:
                    ConsoleUi.InvalidMenu();
            }
        }
    }


    /**
     * 장바구니 수량 입력
     *
     * @return 입력한 수량. 0을 입력하면 null
     */
    private Integer ReadQuantity(int stockQuantity) {

        while (true) {

            ConsoleUi.Prompt("수량 (0: 취소)");
            String input = scanner.nextLine().trim();

            try {
                int quantity = Integer.parseInt(input);

                if (quantity == 0) {
                    return null;
                }

                if (quantity < 0) {
                    ConsoleUi.Error("수량은 1개 이상이어야 합니다.");
                    continue;
                }

                if (quantity > stockQuantity) {
                    ConsoleUi.Error("현재 재고보다 많은 수량은 담을 수 없습니다.");
                    ConsoleUi.Warn("현재 재고: " + stockQuantity + "개");
                    continue;
                }

                return quantity;

            } catch (NumberFormatException e) {
                ConsoleUi.Error("올바른 값을 입력해 주세요.");
            }
        }
    }


    /**
     * 판매 상태를 색상 규칙에 맞게 표시합니다.
     *
     * DB 상태값(SELLING / STOPPED)을 화면에서도 그대로 쓰고, 재고가 0이면 SOLD OUT을 덧붙입니다.
     */
    private String FormatSaleStatus(Product product) {

        String status = ConsoleUi.Status(product.getSaleStatus());

        if ("SELLING".equals(product.getSaleStatus())
                && (product.getStockQuantity() == null || product.getStockQuantity() <= 0)) {

            return status + "  " + ConsoleUi.Red("SOLD OUT");
        }

        return status;
    }


    /**
     * 시리얼 관리 여부를 화면용 문자열로 변환
     */
    private String ConvertSerialStatus(Product product) {

        if (Boolean.TRUE.equals(
                product.getRequiresSerial()
        )) {
            return "YES";
        }

        return "NO";
    }


    /**
     * 카테고리 번호에 해당하는 이름을 찾는 헬퍼 메서드입니다.
     *
     * 조회에 실패하면 화면이 멈추지 않도록 번호를 그대로 보여 줍니다.
     */
    private String FindCategoryName(Long categoryId) {

        if (categoryId == null) {
            return "-";
        }

        if (categoryNames == null) {
            categoryNames = LoadCategoryNames();
        }

        return categoryNames.getOrDefault(categoryId, String.valueOf(categoryId));
    }


    /**
     * 카테고리 번호와 이름을 한 번만 읽어 두는 헬퍼 메서드입니다.
     */
    private Map<Long, String> LoadCategoryNames() {

        Map<Long, String> names = new HashMap<>();

        try {
            List<Category> categories = categoryService.FindAll();

            if (categories != null) {
                for (Category category : categories) {
                    names.put(category.getCategoryId(), category.getCategoryName());
                }
            }

        } catch (RuntimeException e) {
            // 이름을 못 읽어도 상세 화면은 보여 준다
        }

        return names;
    }
}
