package com.team.orderapp.service;

import com.team.orderapp.mapper.ProductMapper;
import com.team.orderapp.model.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {

    /**
     * 카테고리 ID를 조회하고, 없을 경우에만 생성하는 헬퍼 메서드입니다.
     */
    private static Long GetOrCreateDefaultCategory(ProductMapper InMapper) {
        Long categoryId = InMapper.GetFirstCategoryId();
        if (categoryId != null) {
            return categoryId;
        }

        InMapper.InsertCategory("CAT-DEFAULT", "기본 카테고리");
        return InMapper.GetFirstCategoryId();
    }

    /**
     * 간편 상품 등록 (기본 카테고리 및 상품 코드 자동 생성)
     */
    public static void AddNewProduct(ProductMapper InMapper, String InName, int InPrice) {
        Long categoryId = GetOrCreateDefaultCategory(InMapper);

        // 상품 코드 자동 생성 (예: PROD-12345)
        String productCode = "PROD-" + Math.abs(InName.hashCode() % 100000);

        InMapper.InsertProductSimple(productCode, categoryId, InName, BigDecimal.valueOf(InPrice), 100);
        System.out.println("[" + InName + "] (코드: " + productCode + ") 상품이 DB에 깔끔하게 등록되었어!");
    }

    /**
     * 상세 상품 객체 등록
     */
    public static void AddProduct(ProductMapper InMapper, Product InProduct) {
        if (InProduct.GetCategoryId() == null) {
            InProduct.SetCategoryId(GetOrCreateDefaultCategory(InMapper));
        }
        InMapper.InsertProduct(InProduct);
        System.out.println("[" + InProduct.GetProductName() + "] 상품이 성공적으로 등록되었습니다.");
    }

    /**
     * 전체 상품 목록 출력
     */
    public static void ShowAllProducts(ProductMapper InMapper) {
        List<Product> products = InMapper.GetAllProducts();
        System.out.println("\n=== 등록된 상품 목록 ===");
        if (products == null || products.isEmpty()) {
            System.out.println("(등록된 상품이 없습니다.)");
            return;
        }

        for (Product p : products) {
            System.out.println(String.format("ID: %d | 코드: %s | 이름: %s | 가격: %,.0f원 | 재고: %d개 | 상태: %s",
                    p.GetProductId(),
                    p.GetProductCode(),
                    p.GetProductName(),
                    p.GetPrice(),
                    p.GetStockQuantity(),
                    p.GetSaleStatus()));
        }
    }
}
