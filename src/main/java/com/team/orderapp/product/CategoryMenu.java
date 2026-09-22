package com.team.orderapp.product;

import com.team.orderapp.common.ConsoleUi;

import java.util.List;
import java.util.Scanner;

/**
 * 관리자 카테고리 관리 메뉴
 */
public class CategoryMenu {

    private final Scanner scanner;
    private final CategoryService categoryService;


    public CategoryMenu(Scanner scanner) {

        this.scanner = scanner;
        this.categoryService =
                new CategoryService();
    }


    // ============================================================
    // 카테고리 관리 메인
    // ============================================================

    public void Run() {

        while (true) {

            PrintMenu();

            String input =
                    ConsoleUi.Choice(scanner.nextLine());

            switch (input) {

                case "1":
                    ShowCategories();
                    break;

                case "2":
                    RegisterCategory();
                    break;

                case "3":
                    UpdateCategory();
                    break;

                case "4":
                    DeleteCategory();
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
    // 목록 조회
    // ============================================================

    private void ShowCategories() {

        try {

            List<Category> categories =
                    categoryService.FindAll();

            ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("CATEGORY / LIST", "카테고리 목록");

            if (categories.isEmpty()) {

                System.out.println();
                ConsoleUi.Warn("등록된 카테고리가 없습니다.");
                ConsoleUi.PressEnter(scanner);

                return;
            }

            System.out.println();
            System.out.println(
                    ConsoleUi.Cyan(
                            ConsoleUi.PadRight("ID", 6)
                                    + ConsoleUi.PadRight("상위ID", 9)
                                    + ConsoleUi.PadRight("코드", 10)
                                    + ConsoleUi.PadRight("이름", 20)
                    )
            );
            ConsoleUi.Divider(45);

            for (Category category : categories) {

                String parentId =
                        category.getParentCategoryId() == null
                                ? "-"
                                : category.getParentCategoryId().toString();

                System.out.println(
                        ConsoleUi.PadRight(String.valueOf(category.getCategoryId()), 6)
                                + ConsoleUi.PadRight(parentId, 9)
                                + ConsoleUi.PadRight(category.getCategoryCode(), 10)
                                + ConsoleUi.PadRight(category.getCategoryName(), 20)
                );
            }

            ConsoleUi.Divider(45);

        } catch (Exception e) {

            ConsoleUi.Error("카테고리 조회 중 문제가 발생했습니다.");
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 등록
    // ============================================================

    private void RegisterCategory() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("CATEGORY / NEW", "카테고리 등록");


        try {

            Category category =
                    new Category();


            category.setCategoryCode(
                    ReadRequiredString(
                            "카테고리 코드"
                    )
            );


            category.setCategoryName(
                    ReadRequiredString(
                            "카테고리 이름"
                    )
            );


            Long parentId =
                    ReadLong(
                            "상위 카테고리 ID (상위 카테고리는 0)"
                    );


            // 0은 최상위 카테고리
            category.setParentCategoryId(
                    parentId == 0
                            ? null
                            : parentId
            );


            boolean result =
                    categoryService
                            .RegisterCategory(category);


            if (result) {

                ConsoleUi.Success("카테고리가 정상적으로 등록되었습니다.");
            }

        } catch (Exception e) {

            ConsoleUi.Error("카테고리 등록에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 수정
    // ============================================================

    private void UpdateCategory() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("CATEGORY / EDIT", "카테고리 수정");


        try {

            Category category =
                    new Category();


            category.setCategoryId(
                    ReadLong(
                            "수정할 카테고리 ID"
                    )
            );


            category.setCategoryCode(
                    ReadRequiredString(
                            "변경할 카테고리 코드"
                    )
            );


            category.setCategoryName(
                    ReadRequiredString(
                            "변경할 카테고리 이름"
                    )
            );


            Long parentId =
                    ReadLong(
                            "상위 카테고리 ID (상위 카테고리는 0)"
                    );


            category.setParentCategoryId(
                    parentId == 0
                            ? null
                            : parentId
            );


            boolean result =
                    categoryService
                            .UpdateCategory(category);


            if (result) {

                ConsoleUi.Success("카테고리가 정상적으로 수정되었습니다.");
            }

        } catch (Exception e) {

            ConsoleUi.Error("카테고리 수정에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 삭제
    // ============================================================

    private void DeleteCategory() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("CATEGORY / DELETE", "카테고리 삭제");


        try {

            Long categoryId =
                    ReadLong(
                            "삭제할 카테고리 ID"
                    );


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


            boolean result =
                    categoryService
                            .DeleteCategory(categoryId);


            if (result) {

                ConsoleUi.Success("카테고리가 정상적으로 삭제되었습니다.");
            }

        } catch (Exception e) {

            ConsoleUi.Error("카테고리 삭제에 실패했습니다. " + e.getMessage());
        }

        ConsoleUi.PressEnter(scanner);
    }


    // ============================================================
    // 메뉴
    // ============================================================

    private void PrintMenu() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("ADMIN / CATEGORY", "카테고리 관리");

        ConsoleUi.Section("카테고리");
        ConsoleUi.MenuItem("01", "카테고리 목록 조회");
        ConsoleUi.MenuItem("02", "카테고리 등록");
        ConsoleUi.MenuItem("03", "카테고리 수정");
        ConsoleUi.MenuItem("04", "카테고리 삭제");
        ConsoleUi.MenuItem("00", "이전");

        System.out.println();
        ConsoleUi.Prompt("선택");
    }


    // ============================================================
    // 입력 Helper
    // ============================================================

    private String ReadRequiredString(
            String message
    ) {

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


    private Long ReadLong(
            String message
    ) {

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
}