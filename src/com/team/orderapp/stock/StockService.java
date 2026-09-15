package com.team.orderapp.stock;

import com.team.orderapp.common.BusinessException;
import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductDao;

import java.util.ArrayList;
import java.util.List;

/**
 * 재고 조정 및 재고 변동 이력 관리를 담당하는 서비스 클래스입니다.
 */
public class StockService {

    private final ProductDao productDao;
    private final StockAdjustmentDao stockAdjustmentDao;

    public StockService() {
        this.productDao = new ProductDao();
        this.stockAdjustmentDao = new StockAdjustmentDao();
    }

    public StockService(ProductDao InProductDao, StockAdjustmentDao InStockAdjustmentDao) {
        this.productDao = InProductDao;
        this.stockAdjustmentDao = InStockAdjustmentDao;
    }

    /**
     * 특정 상품의 재고를 증감 조정하고 이력을 기록합니다.
     *
     * @param InProductId 상품 식별자
     * @param InQuantityDelta 변경할 수량 (양수: 입고, 음수: 출고/폐기)
     * @param InReason 조정 사유
     * @param InAdjustedBy 작업자 아이디 또는 이름
     * @return 등록된 재고 조정 내역 객체
     */
    public StockAdjustment AdjustProductStock(Long InProductId, int InQuantityDelta, String InReason, String InAdjustedBy) {
        ValidateAdjustmentInput(InProductId, InQuantityDelta, InReason);

        Product product = productDao.FindById(InProductId)
                .orElseThrow(() -> new BusinessException("해당 상품을 찾을 수 없습니다: ID " + InProductId));

        if (product.GetCurrentStock() + InQuantityDelta < 0) {
            throw new BusinessException("현재 재고보다 많은 수량을 차감할 수 없습니다. (현재 재고: " + product.GetCurrentStock() + ")");
        }

        // 재고 증감 반영
        boolean stockUpdated = productDao.UpdateStock(InProductId, InQuantityDelta);
        if (!stockUpdated) {
            throw new BusinessException("재고 수량 변경에 실패하였습니다.");
        }

        // 재고 이력 저장
        StockAdjustment adjustment = new StockAdjustment(null, InProductId, InQuantityDelta, InReason, InAdjustedBy);
        boolean historySaved = stockAdjustmentDao.Insert(adjustment);
        if (!historySaved) {
            throw new BusinessException("재고 조정 이력 저장에 실패하였습니다.");
        }

        return adjustment;
    }

    /**
     * 특정 상품의 재고 변동 이력을 조회합니다.
     *
     * @param InProductId 상품 식별자
     * @return 재고 조정 이력 목록
     */
    public List<StockAdjustment> GetAdjustmentHistory(Long InProductId) {
        return stockAdjustmentDao.FindByProductId(InProductId);
    }

    /**
     * 전체 재고 변동 이력을 조회합니다.
     *
     * @return 전체 재고 조정 이력 목록
     */
    public List<StockAdjustment> GetAllAdjustments() {
        return stockAdjustmentDao.FindAll();
    }

    /**
     * 임계치 이하의 재고 부족 상품 목록을 조회하는 헬퍼 메서드입니다.
     *
     * @param InThreshold 재고 부족 기준 임계치
     * @return 재고 부족 상품 목록
     */
    public List<Product> GetLowStockProducts(int InThreshold) {
        List<Product> allProducts = productDao.FindAll();
        List<Product> lowStockList = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.GetCurrentStock() <= InThreshold) {
                lowStockList.add(product);
            }
        }
        return lowStockList;
    }

    /**
     * 재고 조정 입력값의 유효성을 검증하는 헬퍼 메서드입니다.
     *
     * @param InProductId 상품 식별자
     * @param InQuantityDelta 변경할 수량
     * @param InReason 조정 사유
     */
    private void ValidateAdjustmentInput(Long InProductId, int InQuantityDelta, String InReason) {
        if (InProductId == null) {
            throw new BusinessException("상품 ID는 필수입니다.");
        }
        if (InQuantityDelta == 0) {
            throw new BusinessException("변경할 수량은 0이 될 수 없습니다.");
        }
        if (InReason == null || InReason.trim().isEmpty()) {
            throw new BusinessException("조정 사유를 입력해 주세요.");
        }
    }
}
