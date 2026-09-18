package com.team.orderapp.app;

import com.team.orderapp.common.DbConnectionFactory;

import java.util.Scanner;

/**
 * 애플리케이션 진입점
 */
public class Main {

    // 관리자로 바꿀수있는 테스트용 나중에 지울거임
    // true  : 관리자 메뉴 바로 실행
    // false : 비회원 메뉴부터 실행
    private static final boolean ADMIN_TEST_MODE = true;
    private static final boolean MEMBER_TEST_MODE = false;

    public static void main(String[] args) {

        // main에서는 실제 실행 Helper만 호출
        RunApplication();
    }


    // ============================================================
    // 애플리케이션 실행
    // ============================================================

    private static void RunApplication() {

        // DB 초기화 실패 시 프로그램 종료
        if (!IsDatabaseReady()) {
            return;
        }


        // Scanner는 프로그램 전체에서 하나만 사용
        try (Scanner scanner = new Scanner(System.in)) {

            if (ADMIN_TEST_MODE) {

                // 개발용 관리자 직접 실행
                RunAdminTestMode(scanner);

            } else {

                // 실제 프로그램 시작
                RunGuestMode(scanner);
            }
        }
    }


    // ============================================================
    // DB 연결 상태 확인
    // ============================================================

    private static boolean IsDatabaseReady() {

        if (DbConnectionFactory.GetFactory() == null) {

            System.out.println(
                    "MyBatisFactory가 초기화되지 않았습니다."
            );

            System.out.println(
                    "config/db.properties 설정을 확인해 주세요."
            );

            return false;
        }


        return true;
    }


    // ============================================================
    // 실제 프로그램 시작
    // ============================================================

    private static void RunGuestMode(
            Scanner scanner
    ) {

        GuestMenu guestMenu =
                new GuestMenu(scanner);

        guestMenu.Run();
    }


    // ============================================================
    // 개발용 관리자 테스트 모드
    // ============================================================

    private static void RunAdminTestMode(
            Scanner scanner
    ) {

        /*
         * TODO:
         * 로그인 기능 완성 전까지 사용하는 임시 관리자 정보
         *
         * adminUserId는 app_user 테이블에 실제 존재하는
         * ADMIN 계정의 user_id를 사용해야 합니다.
         */
        Long adminUserId = 1L;

        String adminEmail =
                "admin@example.com";


        AdminMenu adminMenu =
                new AdminMenu(
                        scanner,
                        adminUserId,
                        adminEmail
                );


        adminMenu.Run();
    }
    // ============================================================
    // 개발용 회원 테스트
    // ============================================================

    private static void RunMemberTestMode(
            Scanner scanner
    ) {

        // 테스트할 회원 이메일
        String memberEmail =
                "user@example.com";


        MemberMenu memberMenu =
                new MemberMenu(
                        scanner,
                        memberEmail
                );

        memberMenu.Run();
    }
}