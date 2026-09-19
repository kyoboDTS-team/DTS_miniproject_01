package com.team.orderapp.cart;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Cart {

    //===================
    // 인메모리 장바구니 품목 관리(품목 추가, 수량 합산/변경, 삭제, 전체 비우기, 총 수량 및 총 금액 계산 등) 도메인 모델 구
    // 작성자: 김상진
    //===================

    private Long cartId;
    private Long customerId;
    private LocalDateTime createdAt;
}
