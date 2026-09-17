package com.team.orderapp.order.command;

import com.team.orderapp.order.model.Order;
import com.team.orderapp.order.model.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 주문 생성 및 변경(CUD) 명령 처리를 위한 데이터베이스 접근 객체(DAO) 클래스입니다.
 */
public class OrderCommandDao {

    /**
     * 신규 주문 마스터 레코드를 저장합니다.
     *
     * @param order 저장할 Order 객체
     * @return 생성된 주문 ID (실패 시 null)
     */
    @Insert("INSERT INTO orders (order_no, customer_id, status) VALUES (#{orderNo}, #{customerId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    public Long InsertOrder(Order order) {
        // TODO: INSERT INTO orders ... (생성된 PK 반환)
        return null;
    }

    /**
     * 단일 주문 상세 항목을 저장합니다.
     *
     * @param item 저장할 OrderItem 객체
     * @return 저장 성공 여부
     */
    @Insert("INSERT INTO order_item (order_id, product_id, quantity, unit_price) VALUES (#{orderId}, #{productId}, #{quantity}, #{unitPrice})")
    public boolean InsertOrderItem(OrderItem item) {
        // TODO: INSERT INTO order_item ...
        return false;
    }

    /**
     * 다건의 주문 상세 항목들을 일괄 저장합니다.
     *
     * @param items 저장할 OrderItem 목록
     * @return 일괄 저장 성공 여부
     */
    public boolean InsertOrderItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (OrderItem item : items) {
            boolean success = InsertOrderItem(item);
            if (!success) {
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
    public boolean UpdateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status) {
        // TODO: UPDATE orders SET status = ? WHERE order_id = ?
        return false;
    }
}
