package com.team.orderapp.stock;

import java.time.LocalDateTime;

/**
 * 재고 조정 내역 엔티티/도메인 모델 클래스입니다.
 */
public class StockAdjustment {

    private Long adjustmentId;
    private Long productId;
    private int quantityDelta;
    private String reason;
    private LocalDateTime adjustedAt;
    private String adjustedBy;

    public StockAdjustment() {
    }

    public StockAdjustment(Long InAdjustmentId, Long InProductId, int InQuantityDelta, String InReason, String InAdjustedBy) {
        this.adjustmentId = InAdjustmentId;
        this.productId = InProductId;
        this.quantityDelta = InQuantityDelta;
        this.reason = InReason;
        this.adjustedBy = InAdjustedBy;
        this.adjustedAt = LocalDateTime.now();
    }

    public StockAdjustment(Long InAdjustmentId, Long InProductId, int InQuantityDelta, String InReason, String InAdjustedBy, LocalDateTime InAdjustedAt) {
        this.adjustmentId = InAdjustmentId;
        this.productId = InProductId;
        this.quantityDelta = InQuantityDelta;
        this.reason = InReason;
        this.adjustedBy = InAdjustedBy;
        this.adjustedAt = InAdjustedAt;
    }

    public Long GetAdjustmentId() {
        return adjustmentId;
    }

    public void SetAdjustmentId(Long InAdjustmentId) {
        this.adjustmentId = InAdjustmentId;
    }

    public Long GetProductId() {
        return productId;
    }

    public void SetProductId(Long InProductId) {
        this.productId = InProductId;
    }

    public int GetQuantityDelta() {
        return quantityDelta;
    }

    public void SetQuantityDelta(int InQuantityDelta) {
        this.quantityDelta = InQuantityDelta;
    }

    public String GetReason() {
        return reason;
    }

    public void SetReason(String InReason) {
        this.reason = InReason;
    }

    public LocalDateTime GetAdjustedAt() {
        return adjustedAt;
    }

    public void SetAdjustedAt(LocalDateTime InAdjustedAt) {
        this.adjustedAt = InAdjustedAt;
    }

    public String GetAdjustedBy() {
        return adjustedBy;
    }

    public void SetAdjustedBy(String InAdjustedBy) {
        this.adjustedBy = InAdjustedBy;
    }

    @Override
    public String toString() {
        return "StockAdjustment{" +
                "adjustmentId=" + adjustmentId +
                ", productId=" + productId +
                ", quantityDelta=" + quantityDelta +
                ", reason='" + reason + '\'' +
                ", adjustedAt=" + adjustedAt +
                ", adjustedBy='" + adjustedBy + '\'' +
                '}';
    }
}
