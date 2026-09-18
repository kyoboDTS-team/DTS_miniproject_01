package com.team.orderapp.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 비밀번호를 단방향으로 암호화(해싱)하고, 검증하는 유틸리티입니다.
 * SHA-256 해시 + 회원마다 다른 랜덤 소금(salt)을 사용합니다.
 */
public class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * 비밀번호를 해싱합니다. DB에는 이 결과 문자열("salt:hash" 형태)을 그대로 저장하면 됩니다.
     *
     * @param password 평문 비밀번호
     * @return 저장용 해시 문자열
     */
    public static String Hash(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        String hash = HashWithSalt(password, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + hash;
    }

    /**
     * 입력한 비밀번호가 저장된 해시와 일치하는지 확인합니다.
     *
     * @param password   로그인 시 입력한 평문 비밀번호
     * @param storedHash DB에 저장돼 있던 해시 문자열(Hash()의 반환값)
     * @return 일치 여부
     */
    public static boolean Verify(String password, String storedHash) {
        String[] parts = storedHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String hash = HashWithSalt(password, salt);
        return hash.equals(parts[1]);
    }

    private static String HashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] hashedBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("비밀번호 해싱 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}
