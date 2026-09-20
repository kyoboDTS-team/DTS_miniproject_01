package com.team.orderapp.common;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 주문번호를 만드는 공통 유틸리티입니다.
 *
 * 형식: TM-20260920-A3F9K2 (TM- + 주문일 + 랜덤 6자리, 총 18자)
 * orders.order_no는 varchar(30) UNIQUE라, 같은 날 주문끼리 겹치지 않도록 뒤에 랜덤을 붙입니다.
 *
 * 일련번호(G001 같은 방식) 대신 랜덤을 쓰는 이유는, 일련번호는 "지금까지 몇 번인지"를
 * DB에서 조회해야 하고 동시에 주문하면 같은 번호가 나올 수 있기 때문입니다.
 */
public class OrderNoGenerator {

    /*
     * 작업: 주문번호 생성 공통 유틸리티
     *
     * 작업자: 김상진
     */

    private static final String PREFIX = "TM-";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 0/O, 1/I처럼 눈으로 헷갈리는 글자는 뺐습니다.
    // 반품할 때 사용자가 주문번호를 보고 그대로 입력해야 하기 때문입니다.
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;

    // 주문번호를 알면 비회원도 반품할 수 있어, 예측하기 어려운 난수를 씁니다.
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 상태 없이 메서드만 제공하는 도구라 객체를 만들 필요가 없습니다.
     */
    private OrderNoGenerator() {
    }

    /**
     * 새 주문번호를 만듭니다.
     *
     * @return 예: TM-20260920-A3F9K2
     */
    public static String Generate() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }

        return PREFIX + LocalDate.now().format(DATE_FORMAT) + "-" + code;
    }
}
