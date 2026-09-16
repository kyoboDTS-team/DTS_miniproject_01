package com.team.orderapp.common;

/**
 * 애플리케이션 내 비즈니스 로직 예외를 표현하는 커스텀 예외 클래스입니다.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String InMessage) {
        super(InMessage);
    }

    public BusinessException(String InMessage, Throwable InCause) {
        super(InMessage, InCause);
    }
}
