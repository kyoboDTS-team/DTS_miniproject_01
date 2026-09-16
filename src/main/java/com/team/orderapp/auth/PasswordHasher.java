package com.team.orderapp.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 비밀번호 해싱 및 검증을 담당하는 유틸리티 클래스입니다.
 */
public class PasswordHasher {

    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * 원본 비밀번호를 안전한 해시 문자열로 변환합니다.
     *
     * @param InRawPassword 평문 비밀번호
     * @return 해시된 비밀번호 문자열
     */
    public static String HashPassword(String InRawPassword) {
        if (InRawPassword == null || InRawPassword.isEmpty()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] encodedhash = digest.digest(InRawPassword.getBytes(StandardCharsets.UTF_8));
            return BytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException InException) {
            throw new RuntimeException("해시 알고리즘을 찾을 수 없습니다: " + HASH_ALGORITHM, InException);
        }
    }

    /**
     * 입력된 평문 비밀번호와 저장된 해시 비밀번호가 일치하는지 검증합니다.
     *
     * @param InRawPassword 평문 비밀번호
     * @param InHashedPassword 기존에 저장된 해시 비밀번호
     * @return 일치 여부
     */
    public static boolean VerifyPassword(String InRawPassword, String InHashedPassword) {
        if (InRawPassword == null || InHashedPassword == null) {
            return false;
        }
        String calculatedHash = HashPassword(InRawPassword);
        return calculatedHash.equalsIgnoreCase(InHashedPassword);
    }

    /**
     * 바이트 배열을 16진수 문자열로 변환하는 헬퍼 메서드입니다.
     *
     * @param InBytes 해시 바이트 배열
     * @return 16진수 문자열
     */
    private static String BytesToHex(byte[] InBytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : InBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
