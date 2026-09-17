package com.team.orderapp.customer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * customer 테이블과 매핑되는 고객 정보 도메인 모델입니다.
 */
@Getter
@Setter
public class Customer {

    private Long customerId;
    private Long userId;
    private String customerName;
    private String phone;
    private LocalDateTime createdAt;
}
