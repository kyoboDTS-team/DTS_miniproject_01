package com.team.orderapp.product;


import com.team.orderapp.common.ConsoleUi;
import java.math.BigDecimal;
import java.util.Scanner;

public class ProductCommandMenu {

    private final Scanner scanner;
    private final ProductService productService;

    public ProductCommandMenu(Scanner scanner) {

        this.scanner = scanner;
        this.productService = new ProductService();
    }


    // ============================================================
    // 상품 관리 메인
    // ============================================================

    public void Run() {

        while (true) {

            PrintMenu();

            String input = ConsoleUi.Choice(scanner.nextLine());

            switch (input) {

                case "1":
                    RegisterProduct();
                    break;

                case "2":
                    // 상품 수정
                    UpdateProduct();
                    break;

                case "3":
                    // 판매 상태 변경
                    ChangeSaleStatus();
                    break;

                case "4":
                    // 상품 삭제
                    DeleteProduct();
                    break;

                case "5":

                    // 카테고리 관리 메뉴 진입
                    CategoryMenu categoryMenu =
                            new CategoryMenu(scanner);

                    categoryMenu.Run();

                    break;

                case "0":
                    return;

                default:
                    ConsoleUi.InvalidMenu();
                    ConsoleUi.PressEnter(scanner);
            }
        }
    }


    // ============================================================
    // 1번 메뉴 : 상품 등록
    // 담당: 백종민
    // ============================================================

    private void RegisterProduct() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("PRODUCT / NEW", "상품 등록");

        try {

            Product product = new Product();

            // 상품 코드
            product.setProductCode(
                    ReadRequiredString("상품 코드")
            );

            // 상품명
            product.setProductName(
                    ReadRequiredString("상품명")
            );

            // 카테고리 ID
            product.setCategoryId(
                    ReadLong("카테고리 ID")
            );

            // 가격
            product.setPrice(
                    ReadBigDecimal("가격")
            );

            // 안전재고
            product.setReorderLevel(
                    ReadInteger("안전재고")
            );

            // 시리얼 관리 여부
            product.setRequiresSerial(
                    ReadBoolean("시리얼 관리 상품입니까? (Y/N)")
            );


            boolean result =
                    productService.RegisterProduct(product);


            if (result) {

                System.out.println();
                ConsoleUi.Success("상품이 정상적으로 등록되었습니다.");

            } else {

                System.out.println();
                ConsoleUi.Error("상품 등록에 실패했습니다.");
            }

        } catch (Exception e) {

            ConsoleUi.Error("입력 오류: " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 2번 메뉴 : 상품 수정
    // 담당: 백종민
    // ============================================================

    private void UpdateProduct() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("PRODUCT / EDIT", "상품 수정");


        try {

            Product product =
                    new Product();


            // 어떤 상품을 수정할지 선택
            product.setProductId(
                    ReadLong(
                            "수정할 상품 ID"
                    )
            );


            // 새로운 상품명
            product.setProductName(
                    ReadRequiredString(
                            "변경할 상품명"
                    )
            );


            // 새로운 카테고리
            product.setCategoryId(
                    ReadLong(
                            "변경할 카테고리 ID"
                    )
            );


            // 새로운 가격
            product.setPrice(
                    ReadBigDecimal(
                            "변경할 가격"
                    )
            );


            // 새로운 안전재고 기준
            product.setReorderLevel(
                    ReadInteger(
                            "변경할 안전재고"
                    )
            );


            // Service에 수정 요청
            boolean result =
                    productService
                            .UpdateProduct(product);


            if (result) {

                System.out.println();
                ConsoleUi.Success("상품이 정상적으로 수정되었습니다.");

            } else {

                System.out.println();
                ConsoleUi.Error("상품 수정에 실패했습니다.");

                ConsoleUi.Error("존재하지 않는 상품 ID인지 확인해 주세요.");
            }


        } catch (Exception e) {

            ConsoleUi.Error("상품 수정 오류: " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }

    // ============================================================
    // 판매 상태 변경
    // 담당: 백종민
    // ============================================================

    private void ChangeSaleStatus() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("PRODUCT / STATUS", "판매 상태 변경");


        try {

            // 상태를 변경할 상품 선택
            Long productId =
                    ReadLong(
                            "상품 ID"
                    );


            System.out.println();
            ConsoleUi.Option("1", ConsoleUi.Status("SELLING") + " 판매중");
            ConsoleUi.Option("2", ConsoleUi.Status("STOPPED") + " 판매중지");
            System.out.println();

            ConsoleUi.Prompt("변경할 상태");


            String input =
                    ConsoleUi.Choice(scanner.nextLine());


            String saleStatus;


            // 사용자가 선택한 번호를
            // DB에서 사용하는 상태 문자열로 변환
            switch (input) {

                case "1":

                    saleStatus = "SELLING";
                    break;


                case "2":

                    saleStatus = "STOPPED";
                    break;


                default:

                    ConsoleUi.Error("올바른 상태를 선택해 주세요.");
                    ConsoleUi.PressEnter(scanner);

                    return;
            }


            // Service에 판매 상태 변경 요청
            boolean result =
                    productService.ChangeSaleStatus(
                            productId,
                            saleStatus
                    );


            if (result) {

                System.out.println();

                ConsoleUi.Success("판매 상태가 " + ConsoleUi.Status(saleStatus) + " 상태로 변경되었습니다.");


            } else {

                System.out.println();

                ConsoleUi.Error("판매 상태 변경에 실패했습니다.");

                ConsoleUi.Error("존재하지 않는 상품 ID인지 확인해 주세요.");
            }


        } catch (Exception e) {

            ConsoleUi.Error("판매 상태 변경 오류: " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }

    // ============================================================
// 상품 삭제
// 담당: 백종민
// ============================================================

    private void DeleteProduct() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("PRODUCT / DELETE", "상품 삭제");


        try {

            // 삭제할 상품 선택
            Long productId =
                    ReadLong("삭제할 상품 ID");


            // 실수로 삭제하는 것을 막기 위한 최종 확인
            System.out.println();
            ConsoleUi.Warn("삭제하면 되돌릴 수 없습니다.");
            ConsoleUi.YesNoOptions("삭제", "취소");
            System.out.println();
            ConsoleUi.Prompt("정말 삭제하시겠습니까? (Y/N)");

            String confirm =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase();


            if (!confirm.equals("Y")) {

                ConsoleUi.Cancelled();
                ConsoleUi.PressEnter(scanner);

                return;
            }


            // Service에서 존재 여부와 이력을 모두 검사
            boolean result =
                    productService.DeleteProduct(productId);


            if (result) {

                System.out.println();
                ConsoleUi.Success("상품이 정상적으로 삭제되었습니다.");

            } else {

                System.out.println();
                ConsoleUi.Error("상품 삭제에 실패했습니다.");
            }


        } catch (Exception e) {

            // 존재하지 않는 상품 / 이력 있는 상품 등의 이유 출력
            System.out.println();
            ConsoleUi.Error("상품 삭제 실패: " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 화면 출력
    // ============================================================

    private void PrintMenu() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ADMIN / PRODUCT", "상품 / 카테고리 관리");
        ConsoleUi.Section("상품");
        ConsoleUi.MenuItem("01", "상품 등록");
        ConsoleUi.MenuItem("02", "상품 수정");
        ConsoleUi.MenuItem("03", "판매 상태 변경");
        ConsoleUi.MenuItem("04", "상품 삭제");

        ConsoleUi.Section("카테고리");
        ConsoleUi.MenuItem("05", "카테고리 관리");

        ConsoleUi.Section("시스템");

        // 이후 추가 예정
        // 2. 상품 수정
        // 3. 상품 삭제
        // 4. 판매 상태 변경
        // 5. 카테고리 관리

        ConsoleUi.MenuItem("00", "이전");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }


    // ============================================================
    // 입력 Helper
    // ============================================================

    private String ReadRequiredString(String message) {

        while (true) {

            ConsoleUi.Prompt(message);

            String value =
                    scanner.nextLine().trim();

            if (!value.isBlank()) {
                return value;
            }

            ConsoleUi.Error("값을 입력해 주세요.");
        }
    }


    private Long ReadLong(String message) {

        while (true) {

            try {

                ConsoleUi.Prompt(message);

                return Long.parseLong(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                ConsoleUi.Error("숫자를 입력해 주세요.");
            }
        }
    }


    private Integer ReadInteger(String message) {

        while (true) {

            try {

                ConsoleUi.Prompt(message);

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                ConsoleUi.Error("숫자를 입력해 주세요.");
            }
        }
    }


    private BigDecimal ReadBigDecimal(String message) {

        while (true) {

            try {

                ConsoleUi.Prompt(message);

                return new BigDecimal(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                ConsoleUi.Error("올바른 금액을 입력해 주세요.");
            }
        }
    }


    private Boolean ReadBoolean(String message) {

        while (true) {

            ConsoleUi.Prompt(message);

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

            ConsoleUi.Error("Y 또는 N을 입력해 주세요.");
        }
    }
}