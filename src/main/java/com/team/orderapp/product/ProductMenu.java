package com.team.orderapp.product;

import com.team.orderapp.common.BusinessException;
import com.team.orderapp.common.ConsoleInput;

import java.util.List;

/**
 * 상품 관리 콘솔 화면 및 사용자 인터랙션을 담당하는 클래스입니다.
 */
public class ProductMenu {

    private final ProductService productService;

    public ProductMenu() {
        this.productService = new ProductService();
    }

    public ProductMenu(ProductService InProductService) {
        this.productService = InProductService;
    }

    /**
     * 상품 관리 서브 메뉴를 화면에 표시하고 입력을 처리합니다.
     */
    public void DisplayMenu() {
        boolean inMenu = true;
        while (inMenu) {
            PrintProductMenuOptions();
            int choice = ConsoleInput.ReadInt("메뉴 번호를 선택하세요: ");
            inMenu = RouteProductChoice(choice);
        }
    }

    /**
     * 신규 상품 등록 사용자 흐름을 처리합니다.
     */
    public void AddProduct() {
        System.out.println("\n--- 신규 상품 등록 ---");
        Product product = PromptProductInput();
        try {
            Product created = productService.CreateProduct(product);
            System.out.println("상품 등록 완료: " + created.GetName() + " (ID: " + created.GetProductId() + ")");
        } catch (BusinessException InException) {
            System.out.println("[오류] " + InException.getMessage());
        }
    }

    /**
     * 상품 단건 조회를 처리합니다.
     */
    public void ViewProduct() {
        System.out.println("\n--- 상품 단건 조회 ---");
        int productId = ConsoleInput.ReadInt("조회할 상품 ID: ");
        try {
            Product product = productService.GetProduct(productId);
            PrintProductDetail(product);
        } catch (BusinessException InException) {
            System.out.println("[오류] " + InException.getMessage());
        }
    }

    /**
     * 전체 상품 목록 조회를 처리합니다.
     */
    public void ListProducts() {
        System.out.println("\n--- 상품 전체 목록 ---");
        List<Product> products = productService.GetAllProducts();
        if (products.isEmpty()) {
            System.out.println("등록된 상품이 없습니다.");
            return;
        }
        for (Product p : products) {
            PrintProductDetail(p);
        }
    }

    /**
     * 상품 정보 수정을 처리합니다.
     */
    public void UpdateProduct() {
        System.out.println("\n--- 상품 정보 수정 ---");
        int productId = ConsoleInput.ReadInt("수정할 상품 ID: ");
        try {
            Product existing = productService.GetProduct(productId);
            PrintProductDetail(existing);

            System.out.println("새로운 상품 정보를 입력하세요:");
            Product updated = PromptProductInput();
            updated.SetProductId((long) productId);

            productService.UpdateProduct(updated);
            System.out.println("상품 정보가 성공적으로 수정되었습니다.");
        } catch (BusinessException InException) {
            System.out.println("[오류] " + InException.getMessage());
        }
    }

    /**
     * 상품 메뉴 옵션을 콘솔에 출력하는 헬퍼 메서드입니다.
     */
    private void PrintProductMenuOptions() {
        System.out.println("\n[상품 관리 메뉴]");
        System.out.println("1. 신규 상품 등록");
        System.out.println("2. 상품 단건 조회");
        System.out.println("3. 상품 목록 조회");
        System.out.println("4. 상품 정보 수정");
        System.out.println("0. 메인 메뉴로 돌아가기");
    }

    /**
     * 메뉴 번호 분기를 처리하는 헬퍼 메서드입니다.
     *
     * @param InChoice 메뉴 선택 번호
     * @return 메뉴 유지 여부
     */
    private boolean RouteProductChoice(int InChoice) {
        switch (InChoice) {
            case 1:
                AddProduct();
                return true;
            case 2:
                ViewProduct();
                return true;
            case 3:
                ListProducts();
                return true;
            case 4:
                UpdateProduct();
                return true;
            case 0:
                return false;
            default:
                System.out.println("잘못된 번호입니다. 다시 선택해 주세요.");
                return true;
        }
    }

    /**
     * 콘솔로부터 상품 입력값을 받아 Product 객체를 생성하는 헬퍼 메서드입니다.
     *
     * @return 입력된 Product 객체
     */
    private Product PromptProductInput() {
        String name = ConsoleInput.ReadString("상품명: ");
        double price = ConsoleInput.ReadDouble("가격: ");
        int stock = ConsoleInput.ReadInt("초기 재고: ");
        String description = ConsoleInput.ReadString("상품 설명: ");
        String category = ConsoleInput.ReadString("카테고리: ");

        Product product = new Product();
        product.SetName(name);
        product.SetPrice(price);
        product.SetCurrentStock(stock);
        product.SetDescription(description);
        product.SetCategory(category);
        return product;
    }

    /**
     * 상품 정보를 포맷팅하여 콘솔에 출력하는 헬퍼 메서드입니다.
     *
     * @param InProduct 출력할 Product 객체
     */
    private void PrintProductDetail(Product InProduct) {
        if (InProduct == null) {
            return;
        }
        System.out.println(String.format("ID: %d | 상품명: %s | 가격: %,.0f원 | 현재재고: %d개 | 분류: %s",
                InProduct.GetProductId(),
                InProduct.GetName(),
                InProduct.GetPrice(),
                InProduct.GetCurrentStock(),
                InProduct.GetCategory()));
    }
}
