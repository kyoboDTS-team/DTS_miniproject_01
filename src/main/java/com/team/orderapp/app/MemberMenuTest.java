package com.team.orderapp.app;

import java.util.Scanner;

/**
 * 로그인 기능을 연결하기 전 회원 메뉴와 내 정보 기능을 단독으로 확인하기 위한 임시 실행 파일입니다.
 */
public class MemberMenuTest {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // DB app_user 테이블에 실제로 존재하는 회원 이메일로 바꿔서 실행하세요.
            new MemberMenu(scanner, "customer1@naver.com").Run();
        }
    }
}
