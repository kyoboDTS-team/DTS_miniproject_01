package com.team.orderapp.order.command;

import com.team.orderapp.order.model.Order;
import com.team.orderapp.order.model.OrderItem;
import com.team.orderapp.order.model.OrderItemUnit;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 주문 생성 및 변경(CUD) 명령 처리를 위한 데이터베이스 접근 객체(DAO) 인터페이스입니다.
 */
public interface OrderCommandDao {

    /**
     * 신규 주문을 저장합니다. 성공하면 생성된 order_id가 넘겨받은 order 객체에 채워집니다
     * (order_item.order_id로 연결하려면 이 값이 필요합니다).
     *
     * ordered_at(CURRENT_TIMESTAMP)과 status('CONFIRMED')는 DB 기본값이 있어 넣지 않습니다.
     * customer_id는 비회원 주문이면 null입니다.
     *
     * @param order 저장할 Order 객체 (orderNo, customerId만 사용)
     * @return 저장된 행 수 (성공하면 1)
     */
    @Insert("""
        INSERT INTO orders (order_no, customer_id)
        VALUES (#{orderNo}, #{customerId})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "orderId", keyColumn = "order_id")
    int InsertOrder(Order order);

    /**
     * 주문 품목 한 건을 저장합니다. 성공하면 생성된 order_item_id가 item 객체에 채워집니다
     * (시리얼 상품이면 order_item_unit.order_item_id로 연결해야 하기 때문입니다).
     *
     * unit_price에는 주문 시점의 판매가를 복사해 넣습니다. 나중에 상품 가격이 바뀌어도
     * 과거 주문 금액은 그대로 남아야 하기 때문입니다.
     *
     * @param item 저장할 OrderItem 객체
     * @return 저장된 행 수 (성공하면 1)
     */
    @Insert("""
        INSERT INTO order_item (order_id, product_id, quantity, unit_price)
        VALUES (#{orderId}, #{productId}, #{quantity}, #{unitPrice})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "orderItemId", keyColumn = "order_item_id")
    int InsertOrderItem(OrderItem item);

    /**
     * 다건의 주문 상세 항목들을 일괄 저장합니다.
     *
     * @param items 저장할 OrderItem 목록
     * @return 일괄 저장 성공 여부
     */
    default boolean InsertOrderItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (OrderItem item : items) {
            if (InsertOrderItem(item) != 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 주문을 반품 상태로 바꿉니다. status와 returned_at을 한 문장에서 함께 바꿉니다
     * (DB CHECK 제약이 RETURNED면 returned_at은 NOT NULL이어야 한다고 요구합니다).
     *
     * WHERE에 status = 'CONFIRMED' 조건이 있어, 이미 반품된 주문에는 아무 행도 바뀌지 않습니다.
     * 반환값이 0이면 "반품할 수 없는 주문"으로 처리해야 합니다(조회 후 변경 사이에 끼어드는
     * 중복 반품을 DB가 한 문장에서 막아 줍니다).
     *
     * @param orderId 반품할 주문의 PK
     * @return 바뀐 행 수. 1이면 반품 성공, 0이면 없는 주문이거나 이미 반품된 주문
     */
    @Update("""
        UPDATE orders
        SET status = 'RETURNED',
            returned_at = CURRENT_TIMESTAMP
        WHERE order_id = #{orderId}
          AND status = 'CONFIRMED'
        """)
    int ReturnOrder(@Param("orderId") Long orderId);

    /**
     * 주문번호로 주문 한 건을 조회합니다. 반품 전에 주문의 존재 여부와 상태,
     * 주문자(customer_id)를 확인할 때 씁니다.
     *
     * orders.order_no에는 UNIQUE 제약이 있어 결과는 최대 한 건입니다.
     *
     * @param orderNo 주문번호 (사용자가 입력하는 값)
     * @return 조회된 주문. 없으면 빈 Optional
     */
    @Select("""
        SELECT order_id, order_no, customer_id, ordered_at, status, returned_at
        FROM orders
        WHERE order_no = #{orderNo}
        """)
    Optional<Order> FindByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 주문에 담긴 품목을 조회합니다. 반품 시 재고 복구(product_id, quantity)와
     * 시리얼 복구(order_item_id)에 필요합니다.
     *
     * 박형준님 OrderQueryDao에도 조회가 있지만 화면 표시용 View 타입이라,
     * 여기서는 모델(OrderItem)로 받는 별도 조회를 둡니다.
     *
     * @param orderId 주문의 PK
     * @return 그 주문의 품목 목록. 없으면 빈 목록
     */
    @Select("""
        SELECT order_item_id, order_id, product_id, quantity, unit_price
        FROM order_item
        WHERE order_id = #{orderId}
        ORDER BY order_item_id
        """)
    List<OrderItem> FindItemsByOrderId(@Param("orderId") Long orderId);

    /**
     * 주문 품목에 배정한 시리얼 한 건을 저장합니다. 시리얼 관리 상품(requires_serial = true)만
     * 이 행을 가지며, 수량이 2면 2행이 생깁니다.
     *
     * assigned_at은 DB 기본값(CURRENT_TIMESTAMP)이 있어 넣지 않습니다.
     * returned_at은 반품 전까지 null입니다.
     *
     * @param unit 저장할 OrderItemUnit 객체 (orderItemId, productUnitId만 사용)
     * @return 저장된 행 수 (성공하면 1)
     */
    @Insert("""
        INSERT INTO order_item_unit (order_item_id, product_unit_id)
        VALUES (#{orderItemId}, #{productUnitId})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "orderItemUnitId", keyColumn = "order_item_unit_id")
    int InsertOrderItemUnit(OrderItemUnit unit);

    /**
     * 한 주문에 배정된 시리얼을 모두 조회합니다. 반품할 때 어떤 개체를 되돌려야 하는지
     * 알아내는 데 씁니다.
     *
     * order_item_unit은 주문(order_id)이 아니라 주문 품목(order_item_id)에 매달려 있어,
     * order_item을 거쳐 조인합니다.
     *
     * @param orderId 주문의 PK
     * @return 그 주문에 배정된 시리얼 목록. 시리얼 상품이 없으면 빈 목록
     */
    @Select("""
        SELECT
            oiu.order_item_unit_id,
            oiu.order_item_id,
            oiu.product_unit_id,
            oiu.assigned_at,
            oiu.returned_at
        FROM order_item_unit oiu
        JOIN order_item oi ON oiu.order_item_id = oi.order_item_id
        WHERE oi.order_id = #{orderId}
        ORDER BY oiu.order_item_unit_id
        """)
    List<OrderItemUnit> FindUnitsByOrderId(@Param("orderId") Long orderId);

    /**
     * 배정 이력에 반품 시각을 기록합니다.
     *
     * WHERE에 returned_at IS NULL 조건이 있어, 이미 반품 처리된 행은 바뀌지 않습니다.
     * 반환값이 0이면 중복 반품으로 보고 거절해야 합니다.
     *
     * @param orderItemUnitId 배정 이력의 PK
     * @return 바뀐 행 수. 1이면 기록 성공, 0이면 이미 반품된 행
     */
    @Update("""
        UPDATE order_item_unit
        SET returned_at = CURRENT_TIMESTAMP
        WHERE order_item_unit_id = #{orderItemUnitId}
          AND returned_at IS NULL
        """)
    int MarkUnitReturned(@Param("orderItemUnitId") Long orderItemUnitId);
}

