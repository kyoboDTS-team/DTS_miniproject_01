package com.team.orderapp.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 일별 주문 / 매출 통계 결과를 담는 객체
 */
@Getter
@Setter
public class DailySalesStat {

    // 주문 날짜
    private LocalDate orderDate;

    // 실제 주문 건수
    // order_item 행 수가 아니라 DISTINCT order_id 기준
    private Long orderCount;

    // 판매된 전체 상품 수량
    private Long totalQuantity;

    // 해당 날짜 매출 합계
    private BigDecimal totalSales;
}