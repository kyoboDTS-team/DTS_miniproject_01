package com.team.orderapp.report;

/**
 * 기간별 주문 매출 및 통계 집계 결과를 담는 DTO 클래스입니다.
 */
public class OrderAmountSummary {

    private String period;
    private int totalOrderCount;
    private double totalRevenue;
    private double averageOrderAmount;

    public OrderAmountSummary() {
    }

    public OrderAmountSummary(String InPeriod, int InTotalOrderCount, double InTotalRevenue, double InAverageOrderAmount) {
        this.period = InPeriod;
        this.totalOrderCount = InTotalOrderCount;
        this.totalRevenue = InTotalRevenue;
        this.averageOrderAmount = InAverageOrderAmount;
    }

    public String GetPeriod() {
        return period;
    }

    public void SetPeriod(String InPeriod) {
        this.period = InPeriod;
    }

    public int GetTotalOrderCount() {
        return totalOrderCount;
    }

    public void SetTotalOrderCount(int InTotalOrderCount) {
        this.totalOrderCount = InTotalOrderCount;
    }

    public double GetTotalRevenue() {
        return totalRevenue;
    }

    public void SetTotalRevenue(double InTotalRevenue) {
        this.totalRevenue = InTotalRevenue;
    }

    public double GetAverageOrderAmount() {
        return averageOrderAmount;
    }

    public void SetAverageOrderAmount(double InAverageOrderAmount) {
        this.averageOrderAmount = InAverageOrderAmount;
    }

    @Override
    public String toString() {
        return "OrderAmountSummary{" +
                "period='" + period + '\'' +
                ", totalOrderCount=" + totalOrderCount +
                ", totalRevenue=" + totalRevenue +
                ", averageOrderAmount=" + averageOrderAmount +
                '}';
    }
}
