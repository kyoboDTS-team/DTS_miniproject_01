package com.team.orderapp.stock;

import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductDao;
import com.team.orderapp.product.ProductUnit;
import com.team.orderapp.product.ProductUnitDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Optional;

/**
 * 재고 / 시리얼 관리 Service
 *
 * 담당:
 * - 일반 상품 재고 조정
 * - 재고 조정 이력 저장
 * - 시리얼 상품 등록
 * - 시리얼 조회
 */
public class StockService {


    // ============================================================
    // 일반 상품 재고 입고 / 조정
    // 담당 : 백종민
    // ============================================================

    public boolean AdjustStock(
            Long productId,
            int delta,
            String reason,
            Long adminUserId
    ) {

        // 기본 입력값 검사
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "올바른 상품 ID를 입력해 주세요."
            );
        }

        if (delta == 0) {
            throw new IllegalArgumentException(
                    "재고 변경 수량은 0일 수 없습니다."
            );
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "재고 조정 사유를 입력해 주세요."
            );
        }

        if (adminUserId == null || adminUserId <= 0) {
            throw new IllegalArgumentException(
                    "관리자 정보가 없습니다."
            );
        }


        try (SqlSession session =
                     DbConnectionFactory.OpenSession()) {

            if (session == null) {
                throw new IllegalStateException(
                        "DB 연결 설정이 초기화되지 않았습니다."
                );
            }


            ProductDao productDao =
                    session.getMapper(ProductDao.class);

            StockAdjustmentDao adjustmentDao =
                    session.getMapper(
                            StockAdjustmentDao.class
                    );


            try {

                // 상품 존재 여부 확인
                Optional<Product> optionalProduct =
                        productDao.FindById(productId);

                if (optionalProduct.isEmpty()) {
                    throw new IllegalArgumentException(
                            "존재하지 않는 상품입니다."
                    );
                }


                Product product =
                        optionalProduct.get();


                // 시리얼 상품은 일반 재고 조정 금지
                // 시리얼 등록을 통해 재고를 올려야 함
                if (Boolean.TRUE.equals(
                        product.getRequiresSerial()
                )) {

                    throw new IllegalStateException(
                            "시리얼 관리 상품은 일반 재고 조정을 할 수 없습니다. "
                                    + "시리얼 등록 메뉴를 이용해 주세요."
                    );
                }


                // 재고 변경
                // 음수가 되는 경우 UPDATE가 0건 처리됨
                boolean updated =
                        productDao.UpdateStock(
                                productId,
                                delta
                        );


                if (!updated) {

                    throw new IllegalStateException(
                            "재고가 부족하거나 재고 변경에 실패했습니다."
                    );
                }


                // 재고 조정 이력 생성
                StockAdjustment adjustment =
                        new StockAdjustment();

                adjustment.setProductId(productId);
                adjustment.setQuantityDelta(delta);
                adjustment.setReason(reason);
                adjustment.setAdjustedByUserId(
                        adminUserId
                );


                // stock_adjustment INSERT
                boolean historyInserted =
                        adjustmentDao.Insert(
                                adjustment
                        );


                if (!historyInserted) {

                    throw new IllegalStateException(
                            "재고 조정 이력 저장에 실패했습니다."
                    );
                }


                // 재고 변경 + 이력 저장 모두 성공
                session.commit();

                return true;


            } catch (Exception e) {

                // 둘 중 하나라도 실패하면 전체 취소
                session.rollback();

                throw e;
            }
        }
    }


    // ============================================================
    // 시리얼 상품 입고 / 등록
    // 담당 : 백종민
    // ============================================================

    public boolean RegisterSerial(
            Long productId,
            String serialNumber
    ) {

        if (productId == null || productId <= 0) {

            throw new IllegalArgumentException(
                    "올바른 상품 ID를 입력해 주세요."
            );
        }


        if (serialNumber == null ||
                serialNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "시리얼 번호를 입력해 주세요."
            );
        }


        try (SqlSession session =
                     DbConnectionFactory.OpenSession()) {

            if (session == null) {

                throw new IllegalStateException(
                        "DB 연결 설정이 초기화되지 않았습니다."
                );
            }


            ProductDao productDao =
                    session.getMapper(ProductDao.class);

            ProductUnitDao productUnitDao =
                    session.getMapper(
                            ProductUnitDao.class
                    );


            try {

                // 상품 존재 여부
                Optional<Product> optionalProduct =
                        productDao.FindById(productId);

                if (optionalProduct.isEmpty()) {

                    throw new IllegalArgumentException(
                            "존재하지 않는 상품입니다."
                    );
                }


                Product product =
                        optionalProduct.get();


                // requires_serial=true 상품만 가능
                if (!Boolean.TRUE.equals(
                        product.getRequiresSerial()
                )) {

                    throw new IllegalStateException(
                            "시리얼 관리 상품이 아닙니다."
                    );
                }


                // 시리얼 중복 확인
                Optional<ProductUnit> duplicate =
                        productUnitDao
                                .FindBySerialNumber(
                                        serialNumber
                                );

                if (duplicate.isPresent()) {

                    throw new IllegalArgumentException(
                            "이미 등록된 시리얼 번호입니다."
                    );
                }


                // 새로운 시리얼 단위 생성
                ProductUnit productUnit =
                        new ProductUnit();

                productUnit.setProductId(productId);
                productUnit.setSerialNumber(
                        serialNumber
                );

                productUnit.setUnitStatus(
                        "AVAILABLE"
                );


                // product_unit INSERT
                boolean inserted =
                        productUnitDao.Insert(
                                productUnit
                        );

                if (!inserted) {

                    throw new IllegalStateException(
                            "시리얼 등록에 실패했습니다."
                    );
                }


                // AVAILABLE 실물 1개가 추가됐으므로
                // 상품 재고도 1 증가
                boolean stockUpdated =
                        productDao.UpdateStock(
                                productId,
                                1
                        );

                if (!stockUpdated) {

                    throw new IllegalStateException(
                            "상품 재고 증가에 실패했습니다."
                    );
                }


                // product_unit + product.stock_quantity
                // 둘 다 성공해야 commit
                session.commit();

                return true;


            } catch (Exception e) {

                session.rollback();

                throw e;
            }
        }
    }


    // ============================================================
    // 특정 상품의 시리얼 목록 조회
    // 담당 : 백종민
    // ============================================================

    public List<ProductUnit> FindSerials(
            Long productId
    ) {

        if (productId == null || productId <= 0) {

            throw new IllegalArgumentException(
                    "올바른 상품 ID를 입력해 주세요."
            );
        }


        try (SqlSession session =
                     DbConnectionFactory.OpenSession()) {

            if (session == null) {

                throw new IllegalStateException(
                        "DB 연결 설정이 초기화되지 않았습니다."
                );
            }


            ProductUnitDao productUnitDao =
                    session.getMapper(
                            ProductUnitDao.class
                    );


            return productUnitDao
                    .FindByProductId(productId);
        }
    }
}