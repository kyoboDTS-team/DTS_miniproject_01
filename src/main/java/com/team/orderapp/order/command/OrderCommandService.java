package com.team.orderapp.order.command;

import com.team.orderapp.cart.CartItem;
import com.team.orderapp.cart.CartService;
import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.common.OrderNoGenerator;
import com.team.orderapp.order.model.Order;
import com.team.orderapp.order.model.OrderItem;
import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductDao;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * 주문 생성과 반품의 업무 규칙·트랜잭션을 처리하는 서비스입니다.
 *
 * 주문 한 건은 orders·order_item·product(재고)를 함께 바꾸므로, 한 SqlSession에서
 * 모두 처리하고 마지막에 commit합니다. 중간에 실패하면 commit 없이 세션이 닫혀
 * 앞서 저장한 내용까지 모두 롤백됩니다.
 *
 * 콘솔 출력은 하지 않고, 규칙 위반은 예외로 던져 메뉴가 화면에 표시합니다.
 */
public class OrderCommandService {

    /*
     * 작업: 주문 생성·반품 트랜잭션
     *
     * 작업자: 김상진
     */

    private static final String SALE_STATUS_SELLING = "SELLING";

    /**
     * 장바구니에 담긴 상품을 주문합니다.
     *
     * 담을 때의 가격·판매 상태는 오래됐을 수 있어, 품목마다 상품을 다시 조회해
     * 판매 상태를 확인하고 그때의 가격을 unit_price로 복사합니다.
     * 재고 차감은 조건부 UPDATE(ProductDao.UpdateStock)라, 재고가 모자라면
     * false가 돌아오고 주문 전체가 취소됩니다.
     *
     * @param customerId 주문자의 고객 번호. 비회원 주문이면 null
     * @return 생성된 주문번호 (예: TM-20260920-A3F9K2)
     * @throws IllegalArgumentException 장바구니가 비었거나, 판매 중이 아니거나, 재고가 부족한 경우
     * @throws IllegalStateException    DB 연결에 실패한 경우
     */
    public String Checkout(Long customerId) {

        CartService cartService = new CartService();
        List<CartItem> cartItems = cartService.GetItems();

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어 있습니다.");
        }

        String orderNo;

        try (SqlSession session = OpenSession()) {

            OrderCommandDao orderDao = session.getMapper(OrderCommandDao.class);
            ProductDao productDao = session.getMapper(ProductDao.class);

            Order order = new Order();
            orderNo = OrderNoGenerator.Generate();
            order.setOrderNo(orderNo);
            order.setCustomerId(customerId);

            orderDao.InsertOrder(order);

            // INSERT의 반환값은 행 수라, 생성된 PK는 @Options가 채워 준 객체에서 꺼낸다.
            Long orderId = order.getOrderId();

            for (CartItem cartItem : cartItems) {

                Product product = FindSellingProduct(productDao, cartItem.getProductId());

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setProductId(product.getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                // 주문 시점의 가격을 복사해 둔다. 나중에 상품 가격이 바뀌어도 과거 주문 금액은 그대로여야 한다.
                orderItem.setUnitPrice(product.getPrice());

                orderDao.InsertOrderItem(orderItem);

                // 재고 확인과 차감을 한 문장에서 한다. false면 그 사이 재고가 모자라진 것이다.
                boolean stockUpdated = productDao.UpdateStock(
                        product.getProductId(), -cartItem.getQuantity()
                );

                if (!stockUpdated) {
                    throw new IllegalArgumentException(
                            "재고가 부족합니다: " + product.getProductName()
                    );
                }

                // TODO: 시리얼 상품(requiresSerial)의 시리얼 배정 — 쿼리 위치 결정 후 추가 (2026-09-21 회의)
            }

            session.commit();
        }

        // 주문이 확정된 뒤에 장바구니를 비운다. 실패하면 장바구니가 남아 다시 시도할 수 있다.
        cartService.ClearCart();

        return orderNo;
    }


    /**
     * 주문을 반품합니다. 주문 전체가 한 번에 반품되며, 부분 반품은 지원하지 않습니다.
     *
     * 상태 변경은 조건부 UPDATE(OrderCommandDao.ReturnOrder)로 합니다. 조회해서 확인한 뒤
     * 바꾸는 방식이 아니라 WHERE에 status = 'CONFIRMED' 조건을 넣어, 바뀐 행이 0이면
     * 이미 반품된 주문으로 보고 거절합니다.
     *
     * @param orderNo    반품할 주문번호
     * @param customerId 요청자의 고객 번호. 비회원이면 null
     * @param isAdmin    관리자면 true (다른 사람의 주문도 반품할 수 있음)
     * @throws IllegalArgumentException 주문이 없거나, 남의 주문이거나, 이미 반품된 경우
     * @throws IllegalStateException    DB 연결에 실패하거나 재고 복구에 실패한 경우
     */
    public void ReturnOrder(String orderNo, Long customerId, boolean isAdmin) {

        if (orderNo == null || orderNo.isBlank()) {
            throw new IllegalArgumentException("주문번호를 입력해 주세요.");
        }

        String normalizedOrderNo = orderNo.trim().toUpperCase();

        try (SqlSession session = OpenSession()) {

            OrderCommandDao orderDao = session.getMapper(OrderCommandDao.class);
            ProductDao productDao = session.getMapper(ProductDao.class);

            Order order = orderDao.FindByOrderNo(normalizedOrderNo)
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            CheckReturnPermission(order, customerId, isAdmin);

            // 이미 반품된 주문이면 바뀐 행이 0이다. 조회와 변경 사이에 끼어드는 중복 반품을 DB가 막아 준다.
            if (orderDao.ReturnOrder(order.getOrderId()) == 0) {
                throw new IllegalArgumentException("이미 반품된 주문입니다.");
            }

            for (OrderItem item : orderDao.FindItemsByOrderId(order.getOrderId())) {

                // 주문 때 차감한 수량을 그대로 되돌린다. 같은 조건부 UPDATE에 양수를 넘기면 된다.
                boolean stockRestored = productDao.UpdateStock(
                        item.getProductId(), item.getQuantity()
                );

                if (!stockRestored) {
                    throw new IllegalStateException("재고 복구에 실패했습니다. 관리자에게 문의해 주세요.");
                }

                // TODO: 시리얼 복구(SOLD → AVAILABLE, order_item_unit.returned_at 기록)
                //       — 쿼리 위치 결정 후 추가 (2026-09-21 회의)
            }

            session.commit();
        }
    }


    /**
     * 반품 권한을 확인하는 헬퍼 메서드입니다.
     *
     * 관리자는 모든 주문을, 회원은 자기 주문만 반품할 수 있습니다.
     * 비회원 주문(customer_id가 null)은 주문번호를 아는 사람만 반품할 수 있다고 보고 통과시킵니다.
     */
    private void CheckReturnPermission(Order order, Long customerId, boolean isAdmin) {

        if (isAdmin) {
            return;
        }

        if (order.getCustomerId() == null) {
            return;
        }

        if (!order.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("본인의 주문만 반품할 수 있습니다.");
        }
    }


    /**
     * 주문할 상품을 DB에서 다시 조회하고, 판매 중인지 확인하는 헬퍼 메서드입니다.
     */
    private Product FindSellingProduct(ProductDao productDao, Long productId) {

        Product product = productDao.FindById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        if (!SALE_STATUS_SELLING.equals(product.getSaleStatus())) {
            throw new IllegalArgumentException(
                    "판매 중인 상품이 아닙니다: " + product.getProductName()
            );
        }

        return product;
    }


    /**
     * MyBatis SqlSession을 여는 헬퍼 메서드입니다. autoCommit은 꺼져 있습니다.
     */
    private SqlSession OpenSession() {

        if (DbConnectionFactory.GetFactory() == null) {
            throw new IllegalStateException("DB 연결 설정이 초기화되지 않았습니다.");
        }

        return DbConnectionFactory.GetFactory().openSession();
    }
}
