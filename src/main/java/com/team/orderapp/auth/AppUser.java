package com.team.orderapp.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppUser {

    private Long userId;
    private String email;
    private String passwordHash;
    private String roleCode;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // MyBatis가 객체를 만들 때 사용할 기본 생성자
    public AppUser() {
    }

}
