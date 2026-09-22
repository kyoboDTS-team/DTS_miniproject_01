package com.team.orderapp.auth;

import com.team.orderapp.common.ConsoleUi;

import java.util.Scanner;

/**
 * 로그인 정보를 입력받는 콘솔 화면입니다.
 *
 * 검증은 LoginService가 하고, 이 화면은 입력받기와 결과 출력만 맡습니다.
 * 로그인 후 어느 메뉴로 이동할지는 이 화면에서 정하지 않고, 권한을 반환해
 * 호출한 쪽(GuestMenu 5번)이 결정합니다.
 */
public class LoginMenu {

    /*
     * 작업: 로그인 입력 화면(이메일·비밀번호 입력, 결과 출력, 권한 반환)
     *
     * 작업자: 김상진
     */

    private static final String CANCEL_INPUT = "0";

    // 연속 입력 화면에서 필드 이름 칸의 너비 (계획서 7장)
    private static final int PROMPT_WIDTH = 12;

    // common/ConsoleInput이 아직 구현되지 않아 Scanner를 직접 사용. 완성되면 교체 필요.
    private final Scanner scanner;

    private final LoginService loginService = new LoginService();

    /**
     * 상위 화면에서 쓰던 Scanner를 그대로 물려받습니다.
     * 앱 전체에서 Scanner(System.in)를 하나만 만들어 쓰는 팀 규칙을 따르기 위함입니다.
     *
     * @param scanner 상위 화면의 Scanner
     */
    public LoginMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 로그인 화면을 실행합니다. 성공할 때까지 입력을 다시 받고, 이메일에 0을 입력하면 취소합니다.
     *
     * @return 로그인한 사용자의 권한. 취소하거나 시스템 문제로 실패하면 null
     */
    public UserRole Run() {

        ConsoleUi.ClearScreen();
        ConsoleUi.ScreenHeader("SIGN IN", "로그인");

        System.out.println();
        ConsoleUi.Info("이메일에 0을 입력하면 취소합니다.");
        System.out.println();

        while (true) {

            ConsoleUi.Prompt("Email", PROMPT_WIDTH);
            String email = scanner.nextLine().trim();

            if (CANCEL_INPUT.equals(email)) {
                ConsoleUi.Cancelled();
                return null;
            }

            ConsoleUi.Prompt("Password", PROMPT_WIDTH);
            String password = scanner.nextLine();

            try {

                UserRole role = loginService.Login(email, password);

                ConsoleUi.ClearScreen();
                ConsoleUi.CompleteBox(
                        "SIGN IN",
                        "로그인되었습니다",
                        ConsoleUi.InfoLine(role.getDisplayName(), LoginSession.getEmail())
                );

                ConsoleUi.PressEnter(scanner);

                return role;

            } catch (IllegalArgumentException e) {

                // 입력 실수는 메시지를 그대로 보여 주고 다시 입력받는다.
                System.out.println();
                ConsoleUi.Error(e.getMessage());
                System.out.println();

            } catch (IllegalStateException e) {

                // DB 연결 실패 등 다시 입력해도 해결되지 않는 문제는 화면을 빠져나간다.
                System.out.println();
                ConsoleUi.Error("로그인 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
                return null;
            }
        }

    }
}
