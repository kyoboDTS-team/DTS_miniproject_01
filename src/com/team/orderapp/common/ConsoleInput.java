package com.team.orderapp.common;

import java.util.Scanner;

/**
 * 콘솔 환경에서 안전하고 일관된 입력을 처리하기 위한 공통 유틸리티 클래스입니다.
 */
public class ConsoleInput {

    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * 프롬프트를 표시하고 한 줄의 문자열을 입력받습니다.
     *
     * @param InPrompt 사용자에게 보여줄 안내 메시지
     * @return 입력받은 문자열 (공백 트림 처리)
     */
    public static String ReadString(String InPrompt) {
        PrintPrompt(InPrompt);
        return SCANNER.nextLine().trim();
    }

    /**
     * 프롬프트를 표시하고 정수를 입력받습니다. 올바른 숫자가 입력될 때까지 재시도합니다.
     *
     * @param InPrompt 사용자에게 보여줄 안내 메시지
     * @return 입력받은 정수값
     */
    public static int ReadInt(String InPrompt) {
        while (true) {
            String rawInput = ReadString(InPrompt);
            Integer parsedValue = TryParseInt(rawInput);
            if (parsedValue != null) {
                return parsedValue;
            }
            PrintInputErrorMessage("올바른 정수를 입력해 주세요.");
        }
    }

    /**
     * 양의 정수(1 이상)를 입력받습니다.
     *
     * @param InPrompt 사용자에게 보여줄 안내 메시지
     * @return 입력받은 양의 정수
     */
    public static int ReadPositiveInt(String InPrompt) {
        while (true) {
            int value = ReadInt(InPrompt);
            if (value > 0) {
                return value;
            }
            PrintInputErrorMessage("1 이상의 양수를 입력해야 합니다.");
        }
    }

    /**
     * 프롬프트를 표시하고 Long 타입 정수를 입력받습니다.
     *
     * @param InPrompt 사용자에게 보여줄 안내 메시지
     * @return 입력받은 Long 값
     */
    public static long ReadLong(String InPrompt) {
        while (true) {
            String rawInput = ReadString(InPrompt);
            Long parsedValue = TryParseLong(rawInput);
            if (parsedValue != null) {
                return parsedValue;
            }
            PrintInputErrorMessage("올바른 정수(Long)를 입력해 주세요.");
        }
    }

    /**
     * 프롬프트를 표시하고 실수를 입력받습니다.
     *
     * @param InPrompt 사용자에게 보여줄 안내 메시지
     * @return 입력받은 실수값
     */
    public static double ReadDouble(String InPrompt) {
        while (true) {
            String rawInput = ReadString(InPrompt);
            Double parsedValue = TryParseDouble(rawInput);
            if (parsedValue != null) {
                return parsedValue;
            }
            PrintInputErrorMessage("올바른 실수를 입력해 주세요.");
        }
    }

    /**
     * 프롬프트 문구를 콘솔에 출력하는 헬퍼 메서드입니다.
     *
     * @param InPrompt 출력할 프롬프트 문구
     */
    private static void PrintPrompt(String InPrompt) {
        System.out.print(InPrompt);
    }

    /**
     * 오류 메시지를 출력하는 헬퍼 메서드입니다.
     *
     * @param InErrorMessage 출력할 오류 메시지
     */
    private static void PrintInputErrorMessage(String InErrorMessage) {
        System.out.println("[입력 오류] " + InErrorMessage);
    }

    /**
     * 문자열을 Integer로 안전하게 파싱을 시도하는 헬퍼 메서드입니다.
     *
     * @param InRawInput 원본 문자열
     * @return 파싱된 Integer 객체 또는 null
     */
    private static Integer TryParseInt(String InRawInput) {
        try {
            return Integer.parseInt(InRawInput);
        } catch (NumberFormatException InException) {
            return null;
        }
    }

    /**
     * 문자열을 Long으로 안전하게 파싱을 시도하는 헬퍼 메서드입니다.
     *
     * @param InRawInput 원본 문자열
     * @return 파싱된 Long 객체 또는 null
     */
    private static Long TryParseLong(String InRawInput) {
        try {
            return Long.parseLong(InRawInput);
        } catch (NumberFormatException InException) {
            return null;
        }
    }

    /**
     * 문자열을 Double로 안전하게 파싱을 시도하는 헬퍼 메서드입니다.
     *
     * @param InRawInput 원본 문자열
     * @return 파싱된 Double 객체 또는 null
     */
    private static Double TryParseDouble(String InRawInput) {
        try {
            return Double.parseDouble(InRawInput);
        } catch (NumberFormatException InException) {
            return null;
        }
    }
}
