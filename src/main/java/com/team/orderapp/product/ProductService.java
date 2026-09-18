package com.team.orderapp.product;

import com.team.orderapp.common.DbConnectionFactory;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductService {

    // ============================================================
    // 상품 등록
    // 담당: 백종민
    // ============================================================

    /**
     * 신규 상품을 등록합니다.
     */
    public boolean RegisterProduct(Product product) {

        // 1. 입력값 검증
        ValidateProductForRegister(product);

        // 2. 신규 상품 기본값 설정
        ApplyRegisterDefaults(product);

        // 3. DB 등록
        try (SqlSession session = OpenSession()) {

            try {
                ProductDao productDao = GetProductDao(session);

                boolean result = productDao.Insert(product);

                if (!result) {
                    session.rollback();
                    return false;
                }

                session.commit();
                return true;

            } catch (Exception e) {

                session.rollback();
                throw e;
            }

        } catch (Exception e) {

            System.out.println(
                    "상품 등록 중 오류: " + e.getMessage()
            );

            return false;
        }
    }


    /**
     * 상품 등록 시 입력값을 검증합니다.
     */
    private void ValidateProductForRegister(Product product) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "상품 정보가 없습니다."
            );
        }

        if (product.getProductCode() == null ||
                product.getProductCode().isBlank()) {

            throw new IllegalArgumentException(
                    "상품 코드를 입력해 주세요."
            );
        }

        if (product.getProductName() == null ||
                product.getProductName().isBlank()) {

            throw new IllegalArgumentException(
                    "상품명을 입력해 주세요."
            );
        }

        if (product.getCategoryId() == null) {

            throw new IllegalArgumentException(
                    "카테고리를 선택해 주세요."
            );
        }

        if (product.getPrice() == null ||
                product.getPrice()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "가격은 0원 이상이어야 합니다."
            );
        }

        if (product.getReorderLevel() == null ||
                product.getReorderLevel() < 0) {

            throw new IllegalArgumentException(
                    "안전재고는 0 이상이어야 합니다."
            );
        }
    }


    /**
     * 신규 상품 등록 시 기본값을 설정합니다.
     */
    private void ApplyRegisterDefaults(Product product) {

        // 신규 상품은 입고 전이므로 재고 0
        product.setStockQuantity(0);

        // 신규 상품은 기본적으로 판매중
        product.setSaleStatus("SELLING");

        // 시리얼 관리 여부를 선택하지 않으면 일반 상품 처리
        if (product.getRequiresSerial() == null) {
            product.setRequiresSerial(false);
        }
    }


    // ============================================================
    // 상품 조회
    // 담당: 박형준
    //
    // 예:
    // FindAllProducts()
    // FindProductById()
    // FindProductsByCondition()
    // ============================================================


    // ============================================================
    // 상품 수정
    // 담당: 백종민
    // ============================================================

    /**
     * 상품 수정
     *
     * 수정 대상:
     * - 상품명
     * - 카테고리
     * - 가격
     * - 안전재고
     */
    public boolean UpdateProduct(Product product) {

        // 수정 입력값 검증
        ValidateProductForUpdate(product);

        try (SqlSession session = OpenSession()) {

            try {

                ProductDao productDao =
                        GetProductDao(session);

                // DB UPDATE
                boolean result =
                        productDao.Update(product);

                // UPDATE된 행이 없으면
                // 없는 상품 ID일 가능성이 있음
                if (!result) {

                    session.rollback();
                    return false;
                }

                // 정상 수정
                session.commit();

                return true;

            } catch (Exception e) {

                // UPDATE 도중 오류가 나면 취소
                session.rollback();

                throw e;
            }

        } catch (Exception e) {

            System.out.println(
                    "상품 수정 중 오류: "
                            + e.getMessage()
            );

            return false;
        }
    }


    /**
     * 상품 수정 입력값 검증
     */
    private void ValidateProductForUpdate(
            Product product
    ) {

        if (product == null) {

            throw new IllegalArgumentException(
                    "상품 정보가 없습니다."
            );
        }

        // 어떤 상품을 수정할지 반드시 필요
        if (product.getProductId() == null ||
                product.getProductId() <= 0) {

            throw new IllegalArgumentException(
                    "올바른 상품 ID를 입력해 주세요."
            );
        }

        if (product.getProductName() == null ||
                product.getProductName().isBlank()) {

            throw new IllegalArgumentException(
                    "상품명을 입력해 주세요."
            );
        }

        if (product.getCategoryId() == null ||
                product.getCategoryId() <= 0) {

            throw new IllegalArgumentException(
                    "올바른 카테고리 ID를 입력해 주세요."
            );
        }

        if (product.getPrice() == null ||
                product.getPrice()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "가격은 0원 이상이어야 합니다."
            );
        }

        if (product.getReorderLevel() == null ||
                product.getReorderLevel() < 0) {

            throw new IllegalArgumentException(
                    "안전재고는 0 이상이어야 합니다."
            );
        }
    }


    // ============================================================
    // 상품 삭제 / 판매 상태 변경
    // 담당: 백종민
    // ============================================================

    /**
     * 상품의 판매 상태를 변경.
     *
     * SELLING : 판매중
     * STOPPED : 판매중지
     */
    public boolean ChangeSaleStatus(
            Long productId,
            String saleStatus
    ) {

        // 상품 ID 검증
        if (productId == null || productId <= 0) {

            throw new IllegalArgumentException(
                    "올바른 상품 ID를 입력해 주세요."
            );
        }


        // 판매 상태 검증
        if (saleStatus == null ||
                (!saleStatus.equals("SELLING") &&
                        !saleStatus.equals("STOPPED"))) {

            throw new IllegalArgumentException(
                    "올바른 판매 상태가 아닙니다."
            );
        }


        try (SqlSession session = OpenSession()) {

            try {

                ProductDao productDao =
                        GetProductDao(session);


                // 판매 상태 UPDATE
                boolean result =
                        productDao.UpdateSaleStatus(
                                productId,
                                saleStatus
                        );


                // product_id가 없어서
                // 변경된 행이 없는 경우
                if (!result) {

                    session.rollback();

                    return false;
                }


                // 정상 처리
                session.commit();

                return true;


            } catch (Exception e) {

                // SQL 실행 중 문제가 발생하면 원상복구
                session.rollback();

                throw e;
            }


        } catch (Exception e) {

            System.out.println(
                    "판매 상태 변경 중 오류: "
                            + e.getMessage()
            );

            return false;
        }
    }

    /**
     * 상품을 삭제합니다.
     *
     * 삭제 조건:
     * - 실제 존재하는 상품
     * - 주문 이력 없음
     * - 재고 조정 이력 없음
     * - 시리얼(product_unit) 이력 없음
     *
     * 이력이 있는 상품은 삭제하지 않고
     * STOPPED 상태 사용을 안내합니다.
     */
    public boolean DeleteProduct(Long productId) {

        // 상품 ID 기본 검증
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "올바른 상품 ID를 입력해 주세요."
            );
        }


        try (SqlSession session = OpenSession()) {

            ProductDao productDao =
                    GetProductDao(session);

            try {

                // 1. 실제 존재하는 상품인지 확인
                Optional<Product> product =
                        productDao.FindById(productId);

                if (product.isEmpty()) {
                    throw new IllegalArgumentException(
                            "존재하지 않는 상품입니다."
                    );
                }


                // 2. 주문 / 재고 / 시리얼 이력 확인
                boolean hasHistory =
                        productDao.HasDeleteHistory(productId);

                if (hasHistory) {
                    throw new IllegalStateException(
                            "이력이 있는 상품은 삭제할 수 없습니다. "
                                    + "판매 상태를 STOPPED로 변경해 주세요."
                    );
                }


                // 3. 실제 DELETE
                boolean result =
                        productDao.DeleteById(productId);

                if (!result) {
                    session.rollback();
                    return false;
                }


                // 삭제 성공
                session.commit();

                return true;


            } catch (Exception e) {

                // 중간에 실패하면 원상복구
                session.rollback();

                throw e;
            }
        }
    }


    // ============================================================
    // 공통 Helper
    // ============================================================

    /**
     * MyBatis SqlSession을 생성합니다.
     */
    private SqlSession OpenSession() {

        if (DbConnectionFactory.GetFactory() == null) {
            throw new IllegalStateException(
                    "DB 연결 설정이 초기화되지 않았습니다."
            );
        }

        return DbConnectionFactory
                .GetFactory()
                .openSession();
    }


    /**
     * ProductDao Mapper를 가져옵니다.
     */
    private ProductDao GetProductDao(SqlSession session) {

        return session.getMapper(ProductDao.class);
    }
}