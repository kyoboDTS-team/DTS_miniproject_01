package com.team.orderapp.app;

import com.team.orderapp.common.DbConnectionFactory;

import java.util.Scanner;

/**
 * 애플리케이션 진입점
 *
 * Main에서는 세부 기능을 구현하지 않고
 * 프로그램 시작과 최초 메뉴 실행만 담당합니다.
 */
public class Main {

    public static void main(String[] args) {

        // DB / MyBatis 초기화 확인
        if (DbConnectionFactory.GetFactory() == null) {

            System.out.println(
                    "MyBatisFactory가 초기화되지 않았습니다."
            );

            System.out.println(
                    "config/db.properties 설정을 확인해 주세요."
            );

            return;
        }


        // Scanner는 Main에서 하나만 생성해서
        // 모든 메뉴가 같이 사용하도록 함
        try (Scanner scanner = new Scanner(System.in)) {

            // 프로그램 최초 진입은 비회원 메뉴
            GuestMenu guestMenu =
                    new GuestMenu(scanner);

            guestMenu.Run();
        }
    }
}