package com.team.orderapp.export;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * 관리자 상품 CSV 저장 / 불러오기 메뉴
 */
public class ProductCsvMenu {

    private final Scanner scanner;
    private final ProductCsvService csvService;


    public ProductCsvMenu(
            Scanner scanner
    ) {

        this.scanner = scanner;
        this.csvService =
                new ProductCsvService();
    }


    // ============================================================
    // CSV 메뉴
    // ============================================================

    public void Run() {

        while (true) {

            PrintMenu();

            String input =
                    scanner.nextLine()
                            .trim();


            switch (input) {

                case "1":
                    ExportProducts();
                    break;


                case "2":
                    ImportProducts();
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
    // 상품 CSV 저장
    // ============================================================

    private void ExportProducts() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "             상품 CSV 저장"
        );
        System.out.println(
                "========================================"
        );


        System.out.print(
                "파일명 (엔터: products.csv) > "
        );


        String fileName =
                scanner.nextLine()
                        .trim();


        try {

            Path savedPath =
                    csvService
                            .ExportProducts(
                                    fileName
                            );


            System.out.println();
            System.out.println(
                    "CSV 저장이 완료되었습니다."
            );

            System.out.println(
                    "저장 위치: "
                            + savedPath
            );


        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "CSV 저장 실패: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // 상품 CSV 불러오기
    // ============================================================

    private void ImportProducts() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "           상품 CSV 불러오기"
        );
        System.out.println(
                "========================================"
        );


        System.out.print(
                "불러올 파일명 (엔터: products.csv) > "
        );


        String fileName =
                scanner.nextLine()
                        .trim();


        System.out.println();

        System.out.println(
                "CSV의 모든 상품을 신규 등록합니다."
        );

        System.out.println(
                "한 행이라도 오류가 있으면 전체 등록이 취소됩니다."
        );


        System.out.print(
                "계속하시겠습니까? (Y/N) > "
        );


        String confirm =
                scanner.nextLine()
                        .trim()
                        .toUpperCase();


        if (!confirm.equals("Y")) {

            System.out.println(
                    "CSV 불러오기를 취소했습니다."
            );

            return;
        }


        try {

            int insertedCount =
                    csvService
                            .ImportProducts(
                                    fileName
                            );


            System.out.println();
            System.out.println(
                    "CSV 상품 등록이 완료되었습니다."
            );

            System.out.println(
                    "등록 상품 수: "
                            + insertedCount
                            + "개"
            );


        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "CSV 불러오기 실패: "
                            + e.getMessage()
            );

            System.out.println(
                    "등록된 상품은 없습니다."
            );
        }
    }


    // ============================================================
    // 메뉴 출력
    // ============================================================

    private void PrintMenu() {

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "          CSV 저장 / 불러오기"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "1. 상품 CSV 저장"
        );

        System.out.println(
                "2. 상품 CSV 불러오기"
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
}