package com.team.orderapp.auth;

/**
 * 시스템 사용자 권한(역할)을 정의하는 Enum입니다.
 */
public enum UserRole {
    ADMIN("관리자"),
    STAFF("직원"),
    CUSTOMER("고객");

    private final String description;

    UserRole(String InDescription) {
        this.description = InDescription;
    }

    public String GetDescription() {
        return description;
    }

    /**
     * 문자열로부터 일치하는 UserRole을 찾는 헬퍼 메서드입니다.
     *
     * @param InRoleName 역할 이름 문자열
     * @return 매칭되는 UserRole, 없을 경우 기본값 CUSTOMER
     */
    public static UserRole FromString(String InRoleName) {
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(InRoleName)) {
                return role;
            }
        }
        return CUSTOMER;
    }
}
