package com.team.orderapp.auth;

import lombok.Getter;

@Getter
public enum UserRole {

    /*
     작업: 사용자 권한/역할(ADMIN, CUSTOMER 등) 정의 및 매핑 로직 구현

     작업자: 김상진(Dorazee0209)
     */

    ADMIN("관리자"),
    CUSTOMER("회원");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 권한 코드를 입력받고 변환
     * @param code 비교대상 문자열
     * @return 성공 시, 해당하는 role 반환
     * @return 실패 시, IllegalStateException 예외 발생
     */
    public static UserRole FromCode(String code) {
        for(UserRole role : values()) {
            if(role.name().equals(code))
                return role;
        }
        throw new IllegalStateException("알 수 없는 권한 코드입니다: " + code);
    }

}
