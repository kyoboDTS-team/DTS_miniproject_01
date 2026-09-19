package com.team.orderapp.cart;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CartItem {
    //======================
    // 장바구니에 담긴 개별 상품 정보(상품 ID, 상품명, 가격, 수량, 소계 등) 모델 구현
    //
    // 작성자: 김상진
    //======================

    // "cart_item" 테이블 칼럼
    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime addedAt;

    // "product" 테이블
    private String productName;
    private BigDecimal price;

    // price x quantity 반환 함수
    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

}
