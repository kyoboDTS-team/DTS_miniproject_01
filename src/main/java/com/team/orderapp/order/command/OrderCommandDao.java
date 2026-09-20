package com.team.orderapp.order.command;

import com.team.orderapp.order.model.Order;
import com.team.orderapp.order.model.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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
     * 주문의 진행 상태를 변경합니다.
     *
     * @param orderId 주문 식별자
     * @param status 변경할 주문 상태 (예: CONFIRMED, RETURNED, CANCELLED)
     * @return 상태 변경 성공 여부
     */
    @Update("UPDATE orders SET status = #{status} WHERE order_id = #{orderId}")
    boolean UpdateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
}
