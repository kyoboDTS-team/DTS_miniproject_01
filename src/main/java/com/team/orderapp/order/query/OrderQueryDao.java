package com.team.orderapp.order.query;

import com.team.orderapp.order.model.OrderStatus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 주문 조회를 담당하는 MyBatis Mapper입니다.
 *
 * 조회 전용이므로 SELECT 기능만 작성합니다.
 */
public interface OrderQueryDao {

    // =====================================================
    // 1. 주문 ID로 주문 기본 정보 조회
    // =====================================================

    @Select("""
        SELECT
            order_id AS orderId,
            order_no AS orderNo,
            customer_id AS customerId,
            ordered_at AS orderedAt,
            status AS status,
            returned_at AS returnedAt
        FROM orders
        WHERE order_id = #{orderId}
        """)
    Optional<com.team.orderapp.order.model.Order> FindById(
            @Param("orderId") Long orderId
    );


    // =====================================================
    // 2. 관리자용 전체 주문 요약 목록
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            c.customer_name AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        LEFT JOIN customer c
            ON c.customer_id = o.customer_id

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        ORDER BY o.ordered_at DESC
        """)
    List<OrderSummaryView> FindSummaries();


    // =====================================================
    // 3. 특정 회원의 주문 요약 목록
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            c.customer_name AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        LEFT JOIN customer c
            ON c.customer_id = o.customer_id

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        WHERE o.customer_id = #{customerId}

        ORDER BY o.ordered_at DESC
        """)
    List<OrderSummaryView> FindSummariesByCustomerId(
            @Param("customerId") Long customerId
    );


    // =====================================================
    // 4. 주문 상태별 요약 목록
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            c.customer_name AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        LEFT JOIN customer c
            ON c.customer_id = o.customer_id

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        WHERE o.status = #{status}

        ORDER BY o.ordered_at DESC
        """)
    List<OrderSummaryView> FindSummariesByStatus(
            @Param("status") OrderStatus status
    );


    // =====================================================
    // 5. 기간별 주문 요약 목록
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            c.customer_name AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        LEFT JOIN customer c
            ON c.customer_id = o.customer_id

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        WHERE o.ordered_at >= #{startDateTime}
          AND o.ordered_at < #{endDateTime}

        ORDER BY o.ordered_at DESC
        """)
    List<OrderSummaryView> FindSummariesByDate(
            @Param("startDateTime")
            LocalDateTime startDateTime,

            @Param("endDateTime")
            LocalDateTime endDateTime
    );


    // =====================================================
    // 6. 전체 회원 주문 조회
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            c.customer_name AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        JOIN customer c
            ON c.customer_id = o.customer_id

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        WHERE o.customer_id IS NOT NULL

        ORDER BY o.ordered_at DESC
        """)
    List<OrderSummaryView> FindMemberSummaries();


    // =====================================================
    // 7. 전체 비회원 주문 조회
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            NULL AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        WHERE o.customer_id IS NULL

        ORDER BY o.ordered_at DESC
        """)
    List<OrderSummaryView> FindGuestSummaries();


    // =====================================================
    // 8. 주문 ID로 상세 헤더 조회
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            c.customer_name AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,
            o.returned_at AS returnedAt,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        LEFT JOIN customer c
            ON c.customer_id = o.customer_id

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        WHERE o.order_id = #{orderId}
        """)
    Optional<OrderDetailView> FindDetailHeaderById(
            @Param("orderId") Long orderId
    );


    // =====================================================
    // 9. 주문번호로 상세 헤더 조회
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            c.customer_name AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,
            o.returned_at AS returnedAt,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        LEFT JOIN customer c
            ON c.customer_id = o.customer_id

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        WHERE o.order_no = #{orderNo}
        """)
    Optional<OrderDetailView> FindDetailHeaderByOrderNo(
            @Param("orderNo") String orderNo
    );


    // =====================================================
    // 10. 회원 본인의 주문 상세 조회
    // =====================================================

    @Select("""
        SELECT
            o.order_id AS orderId,
            o.order_no AS orderNo,
            o.customer_id AS customerId,
            c.customer_name AS customerName,
            o.ordered_at AS orderedAt,
            o.status AS status,
            o.returned_at AS returnedAt,

            COALESCE(
                item_summary.item_count,
                0
            ) AS itemCount,

            COALESCE(
                item_summary.total_quantity,
                0
            ) AS totalQuantity,

            COALESCE(
                item_summary.total_amount,
                0
            ) AS totalAmount

        FROM orders o

        JOIN customer c
            ON c.customer_id = o.customer_id

        LEFT JOIN (
            SELECT
                order_id,
                COUNT(order_item_id) AS item_count,
                SUM(quantity) AS total_quantity,
                SUM(quantity * unit_price) AS total_amount
            FROM order_item
            GROUP BY order_id
        ) item_summary
            ON item_summary.order_id = o.order_id

        WHERE o.order_id = #{orderId}
          AND o.customer_id = #{customerId}
        """)
    Optional<OrderDetailView> FindDetailHeaderByIdAndCustomerId(
            @Param("orderId") Long orderId,
            @Param("customerId") Long customerId
    );


    // =====================================================
    // 11. 주문 상품 상세 목록 조회
    // =====================================================

    @Select("""
        SELECT
            oi.order_item_id AS orderItemId,
            oi.order_id AS orderId,
            oi.product_id AS productId,
            p.product_code AS productCode,
            p.product_name AS productName,
            oi.quantity AS quantity,
            oi.unit_price AS unitPrice,
            (oi.quantity * oi.unit_price) AS subtotal

        FROM order_item oi

        JOIN product p
            ON p.product_id = oi.product_id

        WHERE oi.order_id = #{orderId}

        ORDER BY oi.order_item_id
        """)
    List<OrderItemDetailView> FindItemsByOrderId(
            @Param("orderId") Long orderId
    );
}