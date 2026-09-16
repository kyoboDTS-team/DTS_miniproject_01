package com.team.orderapp.app;

import com.team.orderapp.common.MyBatisFactory;
import org.apache.ibatis.session.SqlSession;

import com.team.orderapp.mapper.ProductMapper;
import com.team.orderapp.service.ProductService;

/**
 * 애플리케이션 진입점(Entry Point) 클래스입니다.
 * 메인 메서드는 간소화되어 있으며, 실제 실행 흐름은 MainMenu 및 헬퍼 메서드로 위임됩니다.
 */
public class Main {

    public static void main(String[] InArgs) {
        RunOrderSystem();
        BootstrapApplication(InArgs);
    }

    private static void RunOrderSystem() {
        if (MyBatisFactory.GetFactory() == null) {
            System.out.println("MyBatisFactory가 초기화되지 않았습니다. config/db.properties 설정을 확인해 주세요.");
            return;
        }

        try (SqlSession session = MyBatisFactory.GetFactory().openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);

//            // 1. 헬퍼 함수로 상품 추가 (INSERT)
//            ProductService.AddNewProduct(mapper, "스테인리스 텀블러", 15000);
//            ProductService.AddNewProduct(mapper, "무선 마우스", 25000);
//
//            // 2. DB에 변경사항 도장 쾅! (이걸 안 하면 등록 안 됨)
//            session.commit();

            // 3. 헬퍼 함수로 전체 목록 데려와서 출력 (SELECT)
            ProductService.ShowAllProducts(mapper);

        } catch (Exception e) {
            System.out.println("시스템 오류: " + e.getMessage());
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
