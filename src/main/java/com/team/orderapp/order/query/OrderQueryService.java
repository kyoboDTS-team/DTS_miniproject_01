package com.team.orderapp.order.query;

import com.team.orderapp.common.BusinessException;

import java.util.List;

/**
 * 주문 정보 조회(Query) 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
public class OrderQueryService {

    private final OrderQueryDao orderQueryDao;

    public OrderQueryService() {
        this.orderQueryDao = new OrderQueryDao();
    }

    public OrderQueryService(OrderQueryDao InOrderQueryDao) {
        this.orderQueryDao = InOrderQueryDao;
    }

    /**
     * 전체 주문 목록(요약 정보)을 조회합니다.
     *
     * @return 주문 요약 View 리스트
     */
    public List<OrderSummaryView> GetOrderSummaryList() {
        return orderQueryDao.FindSummaries();
    }

    /**
     * 주문 식별자로 주문 상세 정보를 조회합니다.
     *
     * @param InOrderId 주문 식별자
     * @return 주문 상세 정보 View 객체
     */
    public OrderDetailView GetOrderDetailById(Long InOrderId) {
        ValidateOrderId(InOrderId);
        return orderQueryDao.FindDetailById(InOrderId)
                .orElseThrow(() -> new BusinessException("주문 정보를 찾을 수 없습니다: ID " + InOrderId));
    }

    /**
     * 특정 고객의 주문 목록(요약 정보)을 조회합니다.
     *
     * @param InCustomerId 고객 식별자
     * @return 해당 고객의 주문 요약 View 리스트
     */
    public List<OrderSummaryView> SearchOrdersByCustomer(Long InCustomerId) {
        if (InCustomerId == null) {
            throw new BusinessException("고객 ID는 필수입니다.");
        }
        return orderQueryDao.FindSummariesByCustomerId(InCustomerId);
    }

    /**
     * 주문 식별자의 유효성을 검증하는 헬퍼 메서드입니다.
     *
     * @param InOrderId 검증할 주문 식별자
     */
    private void ValidateOrderId(Long InOrderId) {
        if (InOrderId == null || InOrderId <= 0) {
            throw new BusinessException("유효한 주문 ID를 입력해야 합니다.");
        }
    }
}
