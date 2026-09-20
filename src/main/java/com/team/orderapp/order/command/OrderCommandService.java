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
