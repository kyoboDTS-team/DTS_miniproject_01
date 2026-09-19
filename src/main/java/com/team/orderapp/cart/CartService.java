package com.team.orderapp.cart;

import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductDao;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.util.List;

/**
 * 장바구니 업무 규칙과 트랜잭션을 처리하는 서비스입니다.
 *
 * 현재 사용자의 장바구니 번호(currentCartId)는 static이라,
 * 어느 메뉴에서 new CartService()를 해도 같은 장바구니를 봅니다.
 * 규칙 위반은 예외로 던지고, 메시지 출력은 메뉴에서 합니다.
 */
public class CartService {

    private static final String SALE_STATUS_SELLING = "SELLING";

    // 모든 CartService 객체가 공유. 첫 상품을 담기 전까지는 null.
    // 로그아웃·사용자 전환 시 null로 되돌려야 다음 사용자에게 이전 장바구니가 보이지 않는다.
    private static Long currentCartId;


    // ============================================================
    // 담기
    // ============================================================

    /**
     * 상품을 장바구니에 담습니다. 같은 상품이 이미 있으면 수량을 합산합니다.
     *
     * @param product  담을 상품 (productId만 사용하며, 판매 상태·재고는 DB에서 다시 확인)
     * @param quantity 담을 수량 (1 이상)
     * @throws IllegalArgumentException 판매 중이 아니거나 재고가 부족한 경우
     */
    public void AddProduct(Product product, int quantity) {

        if (product == null || product.getProductId() == null) {
            throw new IllegalArgumentException("상품 정보가 없습니다.");
        }

        ValidatePositiveQuantity(quantity);

        try (SqlSession session = OpenSession()) {

            CartDao cartDao = session.getMapper(CartDao.class);

            Product latest = FindLatestProduct(session, product.getProductId());
            ValidateSellable(latest);

            int quantityInCart = GetQuantityInCart(cartDao, latest.getProductId());
            ValidateStock(latest, quantityInCart + quantity);

            Long cartId = currentCartId;

            if (cartId == null) {
                Cart cart = new Cart();
                cartDao.InsertCart(cart);
                cartId = cart.getCartId();
            }

            CartItem item = new CartItem();
            item.setCartId(cartId);
            item.setProductId(latest.getProductId());
            item.setQuantity(quantity);

            cartDao.AddItem(item);

            session.commit();

            // commit이 성공한 뒤에만 기억한다. 실패하면 롤백된 번호를 들고 있게 된다.
            currentCartId = cartId;
        }
    }


    // ============================================================
    // 조회
    // ============================================================

    /**
     * 장바구니에 담긴 상품의 총 수량을 반환합니다. 헤더의 장바구니(n)에 사용합니다.
     *
     * @return 총 수량 (장바구니가 없으면 0)
     */
    public int GetCartCount() {

        if (currentCartId == null) {
            return 0;
        }

        try (SqlSession session = OpenSession()) {
            return session.getMapper(CartDao.class)
                    .GetTotalQuantity(currentCartId);
        }
    }


    /**
     * 장바구니 품목 목록을 상품명·단가와 함께 반환합니다.
     *
     * @return 담은 순서대로 정렬된 품목 목록 (장바구니가 없으면 빈 리스트)
     */
    public List<CartItem> GetItems() {

        if (currentCartId == null) {
            return List.of();
        }

        try (SqlSession session = OpenSession()) {
            return session.getMapper(CartDao.class)
                    .FindItemsByCartId(currentCartId);
        }
    }


    /**
     * 품목 목록의 소계를 모두 더한 합계 금액을 계산합니다.
     *
     * GetItems()로 이미 받아 둔 목록을 넘겨서, 목록과 합계가 같은 데이터로 계산되게 합니다.
     *
     * @param items GetItems()로 조회한 품목 목록
     * @return 합계 금액 (비어 있으면 0)
     */
    public BigDecimal CalculateTotalAmount(List<CartItem> items) {

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : items) {
            total = total.add(item.getSubTotal());
        }

        return total;
    }


    // ============================================================
    // 변경 / 삭제
    // ============================================================

    /**
     * 품목의 수량을 새 값으로 변경합니다. 0이면 품목을 삭제합니다.
     *
     * 0 입력 시 삭제 여부 확인은 메뉴에서 먼저 받습니다.
     *
     * @param cartItemId 품목 번호
     * @param quantity   새 수량 (0 이상)
     * @throws IllegalArgumentException 음수이거나, 재고보다 많거나, 없는 품목인 경우
     */
    public void ChangeQuantity(Long cartItemId, int quantity) {

        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }

        if (quantity == 0) {
            RemoveItem(cartItemId);
            return;
        }

        Long cartId = RequireCart();

        try (SqlSession session = OpenSession()) {

            CartDao cartDao = session.getMapper(CartDao.class);

            CartItem target = FindItem(cartDao, cartId, cartItemId);

            Product latest = FindLatestProduct(session, target.getProductId());
            ValidateStock(latest, quantity);

            int updated = cartDao.UpdateQuantity(cartId, cartItemId, quantity);

            if (updated == 0) {
                throw new IllegalArgumentException("장바구니에 없는 품목입니다.");
            }

            session.commit();
        }
    }


    /**
     * 품목 한 줄을 장바구니에서 삭제합니다.
     *
     * @param cartItemId 품목 번호
     * @throws IllegalArgumentException 장바구니가 비어 있거나 없는 품목인 경우
     */
    public void RemoveItem(Long cartItemId) {

        Long cartId = RequireCart();

        try (SqlSession session = OpenSession()) {

            int deleted = session.getMapper(CartDao.class)
                    .DeleteItem(cartId, cartItemId);

            if (deleted == 0) {
                throw new IllegalArgumentException("장바구니에 없는 품목입니다.");
            }

            session.commit();
        }
    }


    /**
     * 장바구니의 품목을 모두 비웁니다. 장바구니 번호는 그대로 유지합니다.
     */
    public void ClearCart() {

        if (currentCartId == null) {
            return;
        }

        try (SqlSession session = OpenSession()) {

            session.getMapper(CartDao.class)
                    .DeleteAllItems(currentCartId);

            session.commit();
        }
    }


    // ============================================================
    // 공통 Helper
    // ============================================================

    /**
     * MyBatis SqlSession을 여는 헬퍼 메서드입니다. autoCommit은 꺼져 있습니다.
     */
    private SqlSession OpenSession() {

        if (DbConnectionFactory.GetFactory() == null) {
            throw new IllegalStateException(
                    "DB 연결 설정이 초기화되지 않았습니다."
            );
        }

        return DbConnectionFactory.GetFactory().openSession();
    }


    /**
     * 현재 장바구니 번호를 반환하고, 없으면 예외를 던지는 헬퍼 메서드입니다.
     */
    private Long RequireCart() {

        if (currentCartId == null) {
            throw new IllegalArgumentException("장바구니가 비어 있습니다.");
        }

        return currentCartId;
    }


    /**
     * 상품을 DB에서 다시 조회하는 헬퍼 메서드입니다. 화면에 표시된 값이 오래됐을 수 있기 때문입니다.
     */
    private Product FindLatestProduct(SqlSession session, Long productId) {

        return session.getMapper(ProductDao.class)
                .FindById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 상품입니다.")
                );
    }


    /**
     * 장바구니 품목 중 cartItemId가 일치하는 품목을 찾는 헬퍼 메서드입니다.
     */
    private CartItem FindItem(CartDao cartDao, Long cartId, Long cartItemId) {

        for (CartItem item : cartDao.FindItemsByCartId(cartId)) {
            if (item.getCartItemId().equals(cartItemId)) {
                return item;
            }
        }

        throw new IllegalArgumentException("장바구니에 없는 품목입니다.");
    }


    /**
     * 장바구니에 이미 담긴 해당 상품의 수량을 구하는 헬퍼 메서드입니다.
     */
    private int GetQuantityInCart(CartDao cartDao, Long productId) {

        if (currentCartId == null) {
            return 0;
        }

        for (CartItem item : cartDao.FindItemsByCartId(currentCartId)) {
            if (item.getProductId().equals(productId)) {
                return item.getQuantity();
            }
        }

        return 0;
    }


    /**
     * 수량이 1 이상인지 확인하는 헬퍼 메서드입니다.
     */
    private void ValidatePositiveQuantity(int quantity) {

        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
    }


    /**
     * 판매 중인 상품인지 확인하는 헬퍼 메서드입니다.
     */
    private void ValidateSellable(Product product) {

        if (!SALE_STATUS_SELLING.equals(product.getSaleStatus())) {
            throw new IllegalArgumentException("판매가 중지된 상품입니다.");
        }
    }


    /**
     * 요청 수량이 현재 재고 이하인지 확인하는 헬퍼 메서드입니다.
     */
    private void ValidateStock(Product product, int requestedQuantity) {

        int stock = product.getStockQuantity() == null
                ? 0
                : product.getStockQuantity();

        if (stock == 0) {
            throw new IllegalArgumentException("품절된 상품입니다.");
        }

        if (requestedQuantity > stock) {
            throw new IllegalArgumentException(
                    "재고가 부족합니다. (현재 재고: " + stock + "개)"
            );
        }
    }
}
