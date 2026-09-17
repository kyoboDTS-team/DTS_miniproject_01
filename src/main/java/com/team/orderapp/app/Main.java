package com.team.orderapp.app;

import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductDao;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * 애플리케이션 진입점(Entry Point) 클래스입니다.
 */
public class Main {

    public static void main(String[] args) {
        RunOrderSystem();
        BootstrapApplication(args);
    }

    private static void RunOrderSystem() {
        if (DbConnectionFactory.GetFactory() == null) {
            System.out.println("MyBatisFactory가 초기화되지 않았습니다. config/db.properties 설정을 확인해 주세요.");
            return;
        }

        try (SqlSession session = DbConnectionFactory.GetFactory().openSession()) {
            // productDao Mapper 가져오기
            ProductDao mapper = session.getMapper(ProductDao.class);

            // 전체 목록 데려와서 출력 (SELECT)
            List<Product> products = mapper.GetAllProducts();

            System.out.println("\n=== 등록된 상품 목록 ===");
            if (products == null || products.isEmpty()) {
                System.out.println("(등록된 상품이 없습니다.)");
            } else {
                System.out.println("조회된 상품 개수: " + products.size());

                // 등록된 상품 전체 띄우기
//                for (Product product : products) {
//                    System.out.println(product);
//                    }
            }


        } catch (Exception e) {
            System.out.println("시스템 오류: " + e.getMessage());
        }
    }

    /**
     * 애플리케이션 초기화 및 실행 흐름을 시작하는 헬퍼 메서드입니다.
     *
     * @param args 커맨드라인 인자 배열
     */
    private static void BootstrapApplication(String[] args) {
        PrintStartupBanner();
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
