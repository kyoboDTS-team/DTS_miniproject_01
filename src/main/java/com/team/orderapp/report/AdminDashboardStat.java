package com.team.orderapp.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 관리자 현황 요약 조회 결과
 */
@Getter
@Setter
public class AdminDashboardStat {

    private Long totalProductCount;
    private Long sellingProductCount;
    private Long stoppedProductCount;

    private Long lowStockProductCount;
    private Long outOfStockProductCount;

    private Long serialProductCount;

    private BigDecimal totalSales;
}