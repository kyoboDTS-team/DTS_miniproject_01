package com.team.orderapp.app;

/**
 * 애플리케이션 진입점(Entry Point) 클래스입니다.
 * 메인 메서드는 간소화되어 있으며, 실제 실행 흐름은 MainMenu 및 헬퍼 메서드로 위임됩니다.
 */
public class Main {

    public static void main(String[] InArgs) {
        BootstrapApplication(InArgs);
    }

    /**
     * 애플리케이션 초기화 및 실행 흐름을 시작하는 헬퍼 메서드입니다.
     *
     * @param InArgs 커맨드라인 인자 배열
     */
    private static void BootstrapApplication(String[] InArgs) {
        // 애플리케이션 시작 배너 출력 및 MainMenu 위임
        PrintStartupBanner();
        MainMenu mainMenu = new MainMenu();
        mainMenu.Run();
    }

    /**
     * 애플리케이션 시작 시 환영 배너를 출력하는 헬퍼 메서드입니다.
     */
    private static void PrintStartupBanner() {
        System.out.println("==================================================");
        System.out.println("       주문/재고 관리 시스템 (OrderApp) v1.0       ");
        System.out.println("==================================================");
    }
}
