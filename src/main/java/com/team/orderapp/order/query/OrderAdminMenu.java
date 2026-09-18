package com.team.orderapp.order.query;

import java.util.Scanner;

/**
 * 관리자 주문 / 반품 관리 메뉴
 *
 * 주문 조회 : 박형준 기능 연결
 * 반품 처리 : 김상진 반품 기능에서 완료
 *
 * 관리자는 주문 및 반품 완료 내역을 확인하는 역할
 */
public class OrderAdminMenu {

    private final Scanner scanner;

    public OrderAdminMenu(Scanner scanner) {
        this.scanner = scanner;
    }


    public void Run() {

        while (true) {

            PrintMenu();

            String input =
                    scanner.nextLine().trim();

            switch (input) {

                case "1":
                    // TODO: 박형준 - 전체 주문 조회 연결
                    System.out.println(
                            "[TODO] 전체 주문 조회 연결 예정"
                    );
                    break;

                case "2":
                    // TODO: 박형준 - 주문 상세 조회 연결
                    System.out.println(
                            "[TODO] 주문 상세 조회 연결 예정"
                    );
                    break;

                case "3":
                    // TODO: 박형준 - 조건별 주문 조회 연결
                    System.out.println(
                            "[TODO] 조건별 주문 조회 연결 예정"
                    );
                    break;

                case "4":
                    // TODO:
                    // 박형준 조회 기능에서
                    // status = RETURNED 조건으로 연결
                    System.out.println(
                            "[TODO] 반품 완료 주문 조회 연결 예정"
                    );
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


    private void PrintMenu() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "           주문 / 반품 관리"
        );
        System.out.println(
                "========================================"
        );

        System.out.println("1. 전체 주문 조회");
        System.out.println("2. 주문 상세 조회");
        System.out.println("3. 조건별 주문 조회");
        System.out.println("4. 반품 완료 주문 조회");
        System.out.println("0. 이전");

        System.out.println(
                "----------------------------------------"
        );

        System.out.print("선택 > ");
    }
}