package com.team.orderapp.product;

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
                    scanner.nextLine().trim();

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
                    System.out.println(
                            "올바른 메뉴 번호를 입력해 주세요."
                    );
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

            System.out.println();
            System.out.println(
                    "========================================"
            );
            System.out.println(
                    "             카테고리 목록"
            );
            System.out.println(
                    "========================================"
            );

            if (categories.isEmpty()) {

                System.out.println(
                        "등록된 카테고리가 없습니다."
                );

                return;
            }


            System.out.println(
                    "ID | 상위ID | 코드 | 이름"
            );
            System.out.println(
                    "----------------------------------------"
            );


            for (Category category : categories) {

                String parentId =
                        category.getParentCategoryId() == null
                                ? "-"
                                : category.getParentCategoryId().toString();

                System.out.println(
                        category.getCategoryId()
                                + " | "
                                + parentId
                                + " | "
                                + category.getCategoryCode()
                                + " | "
                                + category.getCategoryName()
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "카테고리 조회 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 등록
    // ============================================================

    private void RegisterCategory() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "             카테고리 등록"
        );
        System.out.println(
                "========================================"
        );


        try {

            Category category =
                    new Category();


            category.setCategoryCode(
                    ReadRequiredString(
                            "카테고리 코드 > "
                    )
            );


            category.setCategoryName(
                    ReadRequiredString(
                            "카테고리 이름 > "
                    )
            );


            Long parentId =
                    ReadLong(
                            "상위 카테고리 ID (상위 카테고리는 0) > "
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

                System.out.println(
                        "카테고리가 정상적으로 등록되었습니다."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "카테고리 등록 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 수정
    // ============================================================

    private void UpdateCategory() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "             카테고리 수정"
        );
        System.out.println(
                "========================================"
        );


        try {

            Category category =
                    new Category();


            category.setCategoryId(
                    ReadLong(
                            "수정할 카테고리 ID > "
                    )
            );


            category.setCategoryCode(
                    ReadRequiredString(
                            "변경할 카테고리 코드 > "
                    )
            );


            category.setCategoryName(
                    ReadRequiredString(
                            "변경할 카테고리 이름 > "
                    )
            );


            Long parentId =
                    ReadLong(
                            "상위 카테고리 ID (상위 카테고리는 0) > "
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

                System.out.println(
                        "카테고리가 정상적으로 수정되었습니다."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "카테고리 수정 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 삭제
    // ============================================================

    private void DeleteCategory() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "             카테고리 삭제"
        );
        System.out.println(
                "========================================"
        );


        try {

            Long categoryId =
                    ReadLong(
                            "삭제할 카테고리 ID > "
                    );


            System.out.print(
                    "정말 삭제하시겠습니까? (Y/N) > "
            );


            String confirm =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase();


            if (!confirm.equals("Y")) {

                System.out.println(
                        "카테고리 삭제를 취소했습니다."
                );

                return;
            }


            boolean result =
                    categoryService
                            .DeleteCategory(categoryId);


            if (result) {

                System.out.println(
                        "카테고리가 정상적으로 삭제되었습니다."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "카테고리 삭제 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 메뉴
    // ============================================================

    private void PrintMenu() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "             카테고리 관리"
        );
        System.out.println(
                "========================================"
        );

        System.out.println(
                "1. 카테고리 목록 조회"
        );

        System.out.println(
                "2. 카테고리 등록"
        );

        System.out.println(
                "3. 카테고리 수정"
        );

        System.out.println(
                "4. 카테고리 삭제"
        );

        System.out.println(
                "0. 이전"
        );

        System.out.println(
                "----------------------------------------"
        );

        System.out.print(
                "선택 > "
        );
    }


    // ============================================================
    // 입력 Helper
    // ============================================================

    private String ReadRequiredString(
            String message
    ) {

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


    private Long ReadLong(
            String message
    ) {

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
}