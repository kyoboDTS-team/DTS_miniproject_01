package com.team.orderapp.cart;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 장바구니(cart, cart_item) 테이블 접근 Mapper입니다.
 *
 * commit / rollback은 CartService에서 처리합니다.
 */
public interface CartDao {

    // ============================================================
    // cart (장바구니 머리)
    // ============================================================

    /**
     * 새 장바구니를 생성합니다. 생성된 cart_id는 cart.cartId에 채워집니다.
     *
     * @param cart customerId만 채운 Cart (비회원이면 null)
     */
    @Insert("""
        INSERT INTO cart (customer_id)
        VALUES (#{customerId})
        """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "cartId",
            keyColumn = "cart_id"
    )
    void InsertCart(Cart cart);


    /**
     * 회원의 장바구니를 조회합니다.
     *
     * @param customerId 회원 고객 번호
     * @return 장바구니가 없으면 Optional.empty()
     */
    @Select("""
        SELECT
            cart_id,
            customer_id,
            created_at
        FROM cart
        WHERE customer_id = #{customerId}
        """)
    Optional<Cart> FindCartByCustomerId(
            @Param("customerId") Long customerId
    );


    /**
     * 장바구니를 삭제합니다. 담긴 품목도 ON DELETE CASCADE로 함께 삭제됩니다.
     *
     * @param cartId 장바구니 번호
     * @return 삭제된 행 수 (없는 장바구니면 0)
     */
    @Delete("""
        DELETE FROM cart
        WHERE cart_id = #{cartId}
        """)
    int DeleteCart(
            @Param("cartId") Long cartId
    );


    // ============================================================
    // cart_item (장바구니 품목)
    // ============================================================

    /**
     * 장바구니 품목 목록을 상품명·단가와 함께 조회합니다.
     *
     * @param cartId 장바구니 번호
     * @return 담은 순서대로 정렬된 품목 목록 (비어 있으면 빈 리스트)
     */
    @Select("""
        SELECT
            ci.cart_item_id,
            ci.cart_id,
            ci.product_id,
            ci.quantity,
            ci.added_at,
            p.product_name,
            p.price
        FROM cart_item ci
        JOIN product p
            ON p.product_id = ci.product_id
        WHERE ci.cart_id = #{cartId}
        ORDER BY ci.cart_item_id
        """)
    List<CartItem> FindItemsByCartId(
            @Param("cartId") Long cartId
    );


    /**
     * 장바구니에 담긴 상품의 총 수량을 조회합니다.
     *
     * @param cartId 장바구니 번호
     * @return 수량 합계 (비어 있으면 0)
     */
    @Select("""
        SELECT COALESCE(SUM(quantity), 0)
        FROM cart_item
        WHERE cart_id = #{cartId}
        """)
    int GetTotalQuantity(
            @Param("cartId") Long cartId
    );


    /**
     * 품목을 담습니다. 같은 상품이 이미 있으면 새 행을 만들지 않고 수량을 합산합니다.
     *
     * @param item cartId, productId, quantity를 채운 CartItem
     * @return 추가 또는 합산된 행 수
     */
    @Insert("""
        INSERT INTO cart_item (cart_id, product_id, quantity)
        VALUES (#{cartId}, #{productId}, #{quantity})
        ON CONFLICT (cart_id, product_id)
        DO UPDATE SET
            quantity = cart_item.quantity + EXCLUDED.quantity
        """)
    int AddItem(CartItem item);


    /**
     * 품목의 수량을 새 값으로 변경합니다.
     *
     * @param cartId     장바구니 번호 (다른 장바구니 품목 수정 방지)
     * @param cartItemId 품목 번호
     * @param quantity   새 수량 (1 이상)
     * @return 변경된 행 수 (대상이 없으면 0)
     */
    @Update("""
        UPDATE cart_item
        SET quantity = #{quantity}
        WHERE cart_item_id = #{cartItemId}
          AND cart_id = #{cartId}
        """)
    int UpdateQuantity(
            @Param("cartId") Long cartId,
            @Param("cartItemId") Long cartItemId,
            @Param("quantity") int quantity
    );


    /**
     * 품목 한 줄을 삭제합니다.
     *
     * @param cartId     장바구니 번호 (다른 장바구니 품목 삭제 방지)
     * @param cartItemId 품목 번호
     * @return 삭제된 행 수 (대상이 없으면 0)
     */
    @Delete("""
        DELETE FROM cart_item
        WHERE cart_item_id = #{cartItemId}
          AND cart_id = #{cartId}
        """)
    int DeleteItem(
            @Param("cartId") Long cartId,
            @Param("cartItemId") Long cartItemId
    );


    /**
     * 장바구니의 품목을 모두 삭제합니다. cart 행은 남깁니다.
     *
     * @param cartId 장바구니 번호
     * @return 삭제된 행 수
     */
    @Delete("""
        DELETE FROM cart_item
        WHERE cart_id = #{cartId}
        """)
    int DeleteAllItems(
            @Param("cartId") Long cartId
    );
}
