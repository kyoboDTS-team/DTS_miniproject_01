package com.team.orderapp.order.query;

import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.order.model.Order;
import com.team.orderapp.order.model.OrderStatus;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderQueryService {
    // TODO: 전체 주문 목록 요약 조회, 주문 상세 단건 조회, 고객별 주문 조회 및 비회원 주문번호 조회 비즈니스 로직 구현
    // =====================================================
    // 1. 주문 ID로 기본 Order 조회
    // =====================================================

    /**
     * 주문 ID로 orders 테이블의 기본 정보를 조회합니다.
     */
    public Optional<Order> FindOrderById(
            Long orderId
    ) {

        ValidateOrderId(orderId);

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            return orderQueryDao.FindById(orderId);
        }
    }


    // =====================================================
    // 2. 관리자 전체 주문 목록
    // =====================================================

    /**
     * 전체 주문 요약 목록을 조회합니다.
     */
    public List<OrderSummaryView> FindAllOrders() {

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            return orderQueryDao.FindSummaries();
        }
    }


    // =====================================================
    // 3. 로그인 회원의 주문 목록
    // =====================================================

    /**
     * 특정 회원의 주문 목록을 조회합니다.
     *
     * MemberMenu의 "내 주문 목록"에서 사용합니다.
     */
    public List<OrderSummaryView> FindMyOrders(
            Long customerId
    ) {

        ValidateCustomerId(customerId);

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            return orderQueryDao
                    .FindSummariesByCustomerId(customerId);
        }
    }


    // =====================================================
    // 4. 주문 상태별 조회
    // =====================================================

    /**
     * 주문 상태에 따라 목록을 조회합니다.
     *
     * CONFIRMED 또는 RETURNED를 전달합니다.
     */
    public List<OrderSummaryView> FindOrdersByStatus(
            OrderStatus status
    ) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "조회할 주문 상태가 없습니다."
            );
        }

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            return orderQueryDao
                    .FindSummariesByStatus(status);
        }
    }


    // =====================================================
    // 5. 기간별 주문 조회
    // =====================================================

    /**
     * 시작 날짜부터 종료 날짜까지의 주문을 조회합니다.
     *
     * 예:
     * startDate = 2026-09-01
     * endDate   = 2026-09-19
     *
     * 실제 DAO 전달값:
     * 2026-09-01 00:00 이상
     * 2026-09-20 00:00 미만
     */
    public List<OrderSummaryView> FindOrdersByDate(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "시작 날짜와 종료 날짜를 입력해주세요."
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "시작 날짜는 종료 날짜보다 늦을 수 없습니다."
            );
        }

        // 시작 날짜 당일 00:00
        LocalDateTime startDateTime =
                startDate.atStartOfDay();

        /*
         * 종료 날짜 다음 날 00:00
         *
         * DAO에서 < endDateTime 조건을 사용하므로
         * 사용자가 입력한 종료 날짜 전체가 포함됩니다.
         */
        LocalDateTime endDateTime =
                endDate.plusDays(1)
                        .atStartOfDay();

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            return orderQueryDao.FindSummariesByDate(
                    startDateTime, endDateTime);
        }
    }


    // =====================================================
    // 6. 관리자용 회원 주문 조회
    // =====================================================

    /**
     * customer_id가 있는 회원 주문만 조회합니다.
     */
    public List<OrderSummaryView> FindMemberOrders() {

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            return orderQueryDao
                    .FindMemberSummaries();
        }
    }


    // =====================================================
    // 7. 관리자용 비회원 주문 조회
    // =====================================================

    /**
     * customer_id가 없는 비회원 주문만 조회합니다.
     */
    public List<OrderSummaryView> FindGuestOrders() {

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            return orderQueryDao
                    .FindGuestSummaries();
        }
    }


    // =====================================================
    // 8. 주문 ID로 상세 조회
    // =====================================================

    /**
     * 주문 ID로 주문 상세 정보를 조회합니다.
     *
     * 관리자 주문 상세 조회에 사용할 수 있습니다.
     */
    public Optional<OrderDetailView> FindDetailById(
            Long orderId
    ) {

        ValidateOrderId(orderId);

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            // 주문 기본 정보와 집계 결과 조회
            Optional<OrderDetailView> result =
                    orderQueryDao
                            .FindDetailHeaderById(
                                    orderId
                            );

            if (result.isEmpty()) {
                return Optional.empty();
            }

            OrderDetailView detail =
                    result.get();

            // 해당 주문에 들어 있는 상품 목록 조회
            List<OrderItemDetailView> items =
                    orderQueryDao.FindItemsByOrderId(
                            orderId
                    );

            // 헤더에 주문 상품 목록 결합
            detail.setItems(items);

            return Optional.of(detail);
        }
    }


    // =====================================================
    // 9. 주문번호로 상세 조회
    // =====================================================

    /**
     * 사용자에게 공개되는 주문번호로
     * 주문 상세 정보를 조회합니다.
     *
     * 관리자 또는 비회원 주문 조회에 사용할 수 있습니다.
     */
    public Optional<OrderDetailView> FindDetailByOrderNo(String orderNo) {

        ValidateOrderNo(orderNo);

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            Optional<OrderDetailView> result =
                    orderQueryDao
                            .FindDetailHeaderByOrderNo(
                                    orderNo.trim()
                            );

            if (result.isEmpty()) {
                return Optional.empty();
            }

            OrderDetailView detail =
                    result.get();

            /*
             * 주문번호로 헤더를 조회한 뒤
             * 반환된 orderId로 상품 목록을 조회합니다.
             */
            List<OrderItemDetailView> items =
                    orderQueryDao.FindItemsByOrderId(
                            detail.getOrderId()
                    );

            detail.setItems(items);

            return Optional.of(detail);
        }
    }


    // =====================================================
    // 10. 회원 본인의 주문 상세 조회
    // =====================================================

    /**
     * 로그인한 회원 본인의 주문인지 확인하면서
     * 주문 상세를 조회합니다.
     *
     * orderId만 조회하지 않고 customerId도 조건에 넣어
     * 다른 회원의 주문을 조회하지 못하게 합니다.
     */
    public Optional<OrderDetailView> FindMyOrderDetail(
            Long orderId,
            Long customerId
    ) {

        ValidateOrderId(orderId);
        ValidateCustomerId(customerId);

        try (SqlSession session = OpenSession()) {

            OrderQueryDao orderQueryDao =
                    GetOrderQueryDao(session);

            Optional<OrderDetailView> result =
                    orderQueryDao
                            .FindDetailHeaderByIdAndCustomerId(
                                    orderId,
                                    customerId
                            );

            /*
             * 주문이 없거나 다른 회원의 주문이면
             * Optional.empty()가 반환됩니다.
             */
            if (result.isEmpty()) {
                return Optional.empty();
            }

            OrderDetailView detail =
                    result.get();

            List<OrderItemDetailView> items =
                    orderQueryDao.FindItemsByOrderId(
                            orderId
                    );

            detail.setItems(items);

            return Optional.of(detail);
        }
    }


    // =====================================================
    // 입력값 검증
    // =====================================================

    /**
     * 주문 ID 검증
     */
    private void ValidateOrderId(Long orderId) {

        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException(
                    "올바른 주문 ID를 입력해주세요."
            );
        }
    }


    /**
     * 주문번호 검증
     */
    private void ValidateOrderNo(String orderNo) {

        if (orderNo == null ||
                orderNo.isBlank()) {

            throw new IllegalArgumentException(
                    "주문번호를 입력해주세요."
            );
        }
    }


    /**
     * 회원 ID 검증
     */
    private void ValidateCustomerId(
            Long customerId
    ) {

        if (customerId == null ||
                customerId <= 0) {

            throw new IllegalArgumentException(
                    "올바른 고객 ID를 입력해주세요."
            );
        }
    }


    // =====================================================
    // MyBatis 공통 기능
    // =====================================================

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
     * SqlSession으로 OrderQueryDao Mapper를 가져옵니다.
     */
    private OrderQueryDao GetOrderQueryDao(
            SqlSession session
    ) {

        return session.getMapper(
                OrderQueryDao.class
        );
    }
}
