package com.team.orderapp.app;

import com.team.orderapp.common.MyBatisFactory;
import org.apache.ibatis.session.SqlSession;

/**
 * 애플리케이션 진입점(Entry Point) 클래스입니다.
 * 메인 메서드는 간소화되어 있으며, 실제 실행 흐름은 MainMenu 및 헬퍼 메서드로 위임됩니다.
 */
public class Main {

    public static void main(String[] InArgs) {
        TestMyBatisConnection();
        BootstrapApplication(InArgs);
    }

    private static void TestMyBatisConnection() {
        // SqlSession이 마이바티스에서 DB랑 통신하는 객체
        try (SqlSession session = MyBatisFactory.GetFactory().openSession()) {
            if (session != null) {
                System.out.println("마이바티스 DB 연결 완벽하게 성공!");
            }
        } catch (Exception e) {
            System.out.println("DB 연결 실패: " + e.getMessage());
        }
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
