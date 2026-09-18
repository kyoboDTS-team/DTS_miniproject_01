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

    /**
     * app_user 테이블의 이메일(로그인 아이디)입니다. customer 테이블 자체에는 없는 값이라,
     * CustomerDao에서 app_user와 조인해서 조회할 때만 값이 채워집니다(Insert/Update 대상 아님).
     */
    private String email;
}
