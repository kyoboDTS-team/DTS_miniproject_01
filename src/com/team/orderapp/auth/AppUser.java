package com.team.orderapp.auth;

import java.time.LocalDateTime;

/**
 * 시스템 사용자 엔티티/도메인 모델 클래스입니다.
 */
public class AppUser {

    private Long userId;
    private String username;
    private String passwordHash;
    private UserRole role;
    private LocalDateTime createdAt;

    public AppUser() {
    }

    public AppUser(Long InUserId, String InUsername, String InPasswordHash, UserRole InRole) {
        this.userId = InUserId;
        this.username = InUsername;
        this.passwordHash = InPasswordHash;
        this.role = InRole;
        this.createdAt = LocalDateTime.now();
    }

    public AppUser(Long InUserId, String InUsername, String InPasswordHash, UserRole InRole, LocalDateTime InCreatedAt) {
        this.userId = InUserId;
        this.username = InUsername;
        this.passwordHash = InPasswordHash;
        this.role = InRole;
        this.createdAt = InCreatedAt;
    }

    public Long GetUserId() {
        return userId;
    }

    public void SetUserId(Long InUserId) {
        this.userId = InUserId;
    }

    public String GetUsername() {
        return username;
    }

    public void SetUsername(String InUsername) {
        this.username = InUsername;
    }

    public String GetPasswordHash() {
        return passwordHash;
    }

    public void SetPasswordHash(String InPasswordHash) {
        this.passwordHash = InPasswordHash;
    }

    public UserRole GetRole() {
        return role;
    }

    public void SetRole(UserRole InRole) {
        this.role = InRole;
    }

    public LocalDateTime GetCreatedAt() {
        return createdAt;
    }

    public void SetCreatedAt(LocalDateTime InCreatedAt) {
        this.createdAt = InCreatedAt;
    }

    /**
     * 사용자가 관리자 권한인지 확인하는 헬퍼 메서드입니다.
     *
     * @return 관리자 권한 여부
     */
    public boolean IsAdmin() {
        return this.role == UserRole.ADMIN;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
