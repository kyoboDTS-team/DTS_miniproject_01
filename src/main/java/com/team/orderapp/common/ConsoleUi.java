package com.team.orderapp.common;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

/**
 * 콘솔 화면 공통 출력 도구입니다.
 *
 * 화면마다 따로 그리던 테두리·색상·표 정렬을 한곳에 모아, 모든 메뉴가 같은 모양으로 보이게 합니다.
 * 색상 규칙은 docs/planning/터미널마켓_ANSI_콘솔_색상_가이드.md,
 * 화면 구조는 docs/planning/터미널마켓_콘솔_UI_화면흐름_계획서_v1.0.md를 따릅니다.
 *
 * <pre>
 * CYAN   : UI / 메뉴 번호 / 입력 프롬프트 / 이동
 * GREEN  : 성공 / 정상 상태(SELLING, AVAILABLE, CONFIRMED)
 * YELLOW : 주의 / 장바구니 / 금액 / 반품 완료(RETURNED)
 * RED    : 오류 / 실패 / 사용 불가(STOPPED, SOLD, 품절)
 * 기본색 : 상품명, 설명, 일반 정보, 테두리
 * </pre>
 *
 * 사용 예:
 * <pre>
 * ConsoleUi.ClearScreen();
 * ConsoleUi.MainHeader(ConsoleUi.InfoLine("회원", email), ConsoleUi.CartLine(3));
 * ConsoleUi.Section("상품");
 * ConsoleUi.MenuItem("01", "전체 상품 조회");
 * ConsoleUi.Prompt("선택");
 * </pre>
 */
public final class ConsoleUi {

    /*
     * 작업: 콘솔 UI 공통 출력 도구(색상·테두리·표 정렬)
     *
     * 작업자: 김상진(Dorazee0209)
     */

    // ============================================================
    // 상수
    // ============================================================

    /** 화면 상단 박스의 안쪽 너비입니다. 테두리 문자는 포함하지 않습니다. */
    public static final int BOX_WIDTH = 46;

    /** 섹션 구분선의 너비입니다. */
    public static final int SECTION_WIDTH = 42;

    /** 목록 표의 기본 너비입니다. */
    public static final int TABLE_WIDTH = 50;

    /** 메뉴 항목과 섹션 구분선의 들여쓰기입니다. */
    public static final String INDENT = "  ";

    /** 화면 오른쪽 위에 표시하는 버전입니다. */
    private static final String VERSION = "v1.0";

    private static final String BRAND = "TERMINAL MARKET";


    // ============================================================
    // ANSI 색상 코드
    // ============================================================

    private static final String ESC = "\u001B[";

    private static final String RESET = ESC + "0m";
    private static final String CYAN_CODE = ESC + "36m";
    private static final String GREEN_CODE = ESC + "32m";
    private static final String YELLOW_CODE = ESC + "33m";
    private static final String RED_CODE = ESC + "31m";

    /**
     * 색상 출력 사용 여부입니다.
     *
     * ANSI를 해석하지 못하는 콘솔에서는 색상 코드가 글자로 보이므로 끌 수 있게 했습니다.
     * 실행 옵션 {@code -Dmarket.color=false} 또는 환경변수 {@code NO_COLOR}로도 끌 수 있습니다.
     */
    private static boolean colorEnabled = DetectColorSupport();

    /** 화면 지우기 사용 여부입니다. */
    private static boolean clearEnabled = true;

    /** ANSI 지우기를 쓸 수 없을 때 대신 출력할 빈 줄 수입니다. */
    private static final int CLEAR_LINES = 30;

    /** 금액을 1,000 형식으로 바꾸는 포맷터입니다. */
    private static final NumberFormat MONEY_FORMAT =
            NumberFormat.getNumberInstance(Locale.KOREA);


    /** 도구 클래스이므로 객체를 만들지 않습니다. */
    private ConsoleUi() {
    }


    // ============================================================
    // 색상 설정
    // ============================================================

    /**
     * 실행 환경에서 색상을 쓸지 판단하는 헬퍼 메서드입니다.
     *
     * @return 색상을 사용하면 true
     */
    private static boolean DetectColorSupport() {

        if (System.getenv("NO_COLOR") != null) {
            return false;
        }

        return !"false".equalsIgnoreCase(
                System.getProperty("market.color", "true")
        );
    }


    /**
     * 색상 출력을 켜거나 끕니다. 색상 코드가 글자로 보이는 콘솔에서 false로 호출하세요.
     */
    public static void SetColorEnabled(boolean enabled) {
        colorEnabled = enabled;
    }


    /**
     * 색상 출력 사용 여부를 반환합니다.
     */
    public static boolean IsColorEnabled() {
        return colorEnabled;
    }


    /**
     * 색상 코드로 글자를 감싸는 헬퍼 메서드입니다. 색상이 꺼져 있으면 원문을 그대로 돌려줍니다.
     */
    private static String Paint(String colorCode, String text) {

        if (!colorEnabled || text == null || text.isEmpty()) {
            return text == null ? "" : text;
        }

        return colorCode + text + RESET;
    }


    /** 글자를 CYAN(UI / 메뉴 / 이동 / 입력)으로 감쌉니다. */
    public static String Cyan(String text) {
        return Paint(CYAN_CODE, text);
    }


    /** 글자를 GREEN(성공 / 정상 상태)으로 감쌉니다. */
    public static String Green(String text) {
        return Paint(GREEN_CODE, text);
    }


    /** 글자를 YELLOW(주의 / 장바구니 / 금액)로 감쌉니다. */
    public static String Yellow(String text) {
        return Paint(YELLOW_CODE, text);
    }


    /** 글자를 RED(오류 / 실패 / 사용 불가)로 감쌉니다. */
    public static String Red(String text) {
        return Paint(RED_CODE, text);
    }


    // ============================================================
    // 화면 전환
    // ============================================================

    /**
     * 화면을 지웁니다. 페이지가 바뀔 때만 호출하고, 같은 화면에서 연속 입력 중에는 호출하지 않습니다.
     *
     * 실제 터미널에서는 ANSI 지우기를, IntelliJ 실행창처럼 ANSI 지우기를 해석하지 못하는
     * 콘솔에서는 빈 줄을 출력해 화면을 밀어 올립니다.
     */
    public static void ClearScreen() {

        if (!clearEnabled) {
            return;
        }

        if (System.getenv("TERM") != null && colorEnabled) {
            System.out.print(ESC + "H" + ESC + "2J");
            System.out.flush();
            return;
        }

        System.out.println("\n".repeat(CLEAR_LINES));
    }


    /**
     * 화면 지우기를 켜거나 끕니다. 이전 출력을 계속 보면서 디버깅할 때 false로 호출하세요.
     */
    public static void SetClearEnabled(boolean enabled) {
        clearEnabled = enabled;
    }


    // ============================================================
    // 메뉴 입력
    // ============================================================

    /**
     * 메뉴 입력을 switch에서 비교할 수 있는 형태로 다듬습니다.
     *
     * 화면에는 {@code 01}로 보여 주지만 사용자는 {@code 1}로 입력할 수도 있으므로,
     * 앞의 0을 떼어 두 가지를 같은 값으로 취급합니다. ({@code "01"}, {@code "1"} → {@code "1"})
     *
     * @param input 사용자가 입력한 원본 문자열
     * @return 앞의 0을 뗀 문자열. 숫자가 아니면 공백만 제거해 그대로 반환
     */
    public static String Choice(String input) {

        if (input == null) {
            return "";
        }

        String value = input.trim();

        if (value.isEmpty() || !value.chars().allMatch(Character::isDigit)) {
            return value;
        }

        // "007" → "7", "00" → "0"
        int start = 0;
        while (start < value.length() - 1 && value.charAt(start) == '0') {
            start++;
        }

        return value.substring(start);
    }


    // ============================================================
    // 상단 헤더
    // ============================================================

    /**
     * 일반 사용자 화면의 상단 헤더를 출력합니다.
     *
     * @param infoLines 테두리 없이 출력할 정보 줄. {@link #InfoLine(String, String)},
     *                  {@link #CartLine(int)}로 만들면 됩니다.
     */
    public static void MainHeader(String... infoLines) {
        PrintHeader(Cyan(BRAND), infoLines);
    }


    /**
     * 관리자 화면의 상단 헤더를 출력합니다. ADMIN은 YELLOW로 구분합니다.
     */
    public static void AdminHeader(String... infoLines) {
        PrintHeader(Cyan(BRAND) + " / " + Yellow("ADMIN"), infoLines);
    }


    /**
     * 헤더 박스를 실제로 그리는 헬퍼 메서드입니다.
     */
    private static void PrintHeader(String brand, String... infoLines) {

        System.out.println();
        System.out.println("┌" + "─".repeat(BOX_WIDTH) + "┐");
        System.out.println(BoxLine(brand, VERSION));

        if (infoLines != null && infoLines.length > 0) {

            System.out.println("├" + "─".repeat(BOX_WIDTH) + "┤");

            // 이메일·장바구니 수량처럼 길이가 변하는 줄은 좌우 테두리를 열어 둔다 (계획서 2-2)
            for (String line : infoLines) {
                if (line != null) {
                    System.out.println(line);
                }
            }
        }

        System.out.println("└" + "─".repeat(BOX_WIDTH) + "┘");
    }


    /**
     * 헤더 아래에 넣을 정보 줄을 만드는 헬퍼 메서드입니다.
     *
     * @param label 항목 이름 (예: 회원, 비회원, 관리자)
     * @param value 값 (예: 이메일). 없으면 null
     */
    public static String InfoLine(String label, String value) {

        // 값이 없으면 라벨만 출력해 뒤쪽 공백이 남지 않게 한다
        if (value == null || value.isBlank()) {
            return INDENT + Cyan(label);
        }

        return INDENT + Cyan(PadRight(label, 10)) + value;
    }


    /**
     * 헤더의 장바구니 수량 줄을 만드는 헬퍼 메서드입니다. 수량은 YELLOW로 표시합니다.
     */
    public static String CartLine(int count) {
        return INDENT + Cyan(PadRight("장바구니", 10)) + Yellow("(" + count + ")");
    }


    // ============================================================
    // 화면 제목
    // ============================================================

    /**
     * 개별 화면의 제목 박스를 출력합니다.
     *
     * <pre>
     * ┌─ CART ───────────────────────────────────────┐
     * │  장바구니                                    │
     * └──────────────────────────────────────────────┘
     * </pre>
     *
     * @param code  화면 코드 (예: CART, PRODUCT / ALL)
     * @param title 화면 이름 (예: 장바구니)
     */
    public static void ScreenHeader(String code, String title) {
        ScreenHeader(code, title, "");
    }


    /**
     * 오른쪽에 요약 값을 함께 보여 주는 화면 제목 박스를 출력합니다.
     *
     * @param code  화면 코드
     * @param left  왼쪽 제목
     * @param right 오른쪽 요약 (예: 합계 금액). 없으면 빈 문자열
     */
    public static void ScreenHeader(String code, String left, String right) {

        String label = " " + code + " ";

        System.out.println();
        System.out.println(
                "┌─" + Cyan(label)
                        + "─".repeat(Math.max(0, BOX_WIDTH - 1 - Width(label)))
                        + "┐"
        );
        System.out.println(BoxLine(left, right));
        System.out.println("└" + "─".repeat(BOX_WIDTH) + "┘");
    }


    /**
     * 작업이 끝났음을 알리는 큰 박스를 출력합니다. 제목은 GREEN으로 가운데 정렬합니다.
     *
     * <pre>
     * ┌─ ORDER COMPLETE ─────────────────────────────┐
     * │                                              │
     * │                  주문 완료                   │
     * │                                              │
     * ├──────────────────────────────────────────────┤
     *   주문번호  TM-20260920-A8K3Q7P2
     *   주문금액  14,000원
     * └──────────────────────────────────────────────┘
     * </pre>
     *
     * @param code      화면 코드 (예: ORDER COMPLETE)
     * @param title     가운데에 표시할 문구 (예: 주문 완료)
     * @param infoLines 아래에 덧붙일 정보 줄. {@link #InfoLine(String, String)}로 만들면 됩니다.
     */
    public static void CompleteBox(String code, String title, String... infoLines) {

        String label = " " + code + " ";
        String blank = "│" + " ".repeat(BOX_WIDTH) + "│";

        System.out.println();
        System.out.println(
                "┌─" + Cyan(label)
                        + "─".repeat(Math.max(0, BOX_WIDTH - 1 - Width(label)))
                        + "┐"
        );
        System.out.println(blank);
        System.out.println("│" + Center(Green(title), BOX_WIDTH) + "│");
        System.out.println(blank);

        if (infoLines != null && infoLines.length > 0) {

            System.out.println("├" + "─".repeat(BOX_WIDTH) + "┤");

            for (String line : infoLines) {
                if (line != null) {
                    System.out.println(line);
                }
            }
        }

        System.out.println("└" + "─".repeat(BOX_WIDTH) + "┘");
    }


    /**
     * 박스 안쪽 한 줄을 만드는 헬퍼 메서드입니다. 왼쪽 값과 오른쪽 값을 양 끝으로 붙입니다.
     */
    private static String BoxLine(String left, String right) {

        int inner = BOX_WIDTH - 4;

        String leftText = left == null ? "" : left;
        String rightText = right == null ? "" : right;

        int gap = Math.max(1, inner - Width(leftText) - Width(rightText));

        return "│  " + leftText + " ".repeat(gap) + rightText + "  │";
    }


    // ============================================================
    // 메뉴
    // ============================================================

    /**
     * 메뉴 영역의 제목과 구분선을 출력합니다. (예: 상품 / 마이페이지 / 시스템)
     */
    public static void Section(String name) {
        System.out.println();
        System.out.println(Cyan(name));
        System.out.println(INDENT + "─".repeat(SECTION_WIDTH));
    }


    /**
     * 메뉴 한 줄을 출력합니다. 번호는 CYAN, 이름은 기본색입니다.
     *
     * @param no    메뉴 번호 (예: 01)
     * @param label 메뉴 이름
     */
    public static void MenuItem(String no, String label) {
        System.out.println(INDENT + Cyan(no) + "  " + label);
    }


    /**
     * 오른쪽에 배지가 붙는 메뉴 한 줄을 출력합니다. (예: 장바구니 [ 3 ])
     *
     * @param badge 오른쪽에 붙일 값. 이미 색을 입혀 넘겨도 정렬은 유지됩니다.
     */
    public static void MenuItem(String no, String label, String badge) {

        String head = INDENT + Cyan(no) + "  " + label;

        int width = SECTION_WIDTH + INDENT.length();
        int gap = Math.max(1, width - Width(head) - Width(badge));

        System.out.println(head + " ".repeat(gap) + badge);
    }


    /**
     * 화면 안의 선택지 한 줄을 출력합니다. 메인 메뉴가 아닌 하위 화면에서 씁니다.
     *
     * <pre>
     * [1] 장바구니 담기
     * [0] 이전
     * </pre>
     *
     * @param key   선택 키 (예: 1, 0, Y)
     * @param label 선택지 이름
     */
    public static void Option(String key, String label) {
        System.out.println(INDENT + Cyan("[" + key + "]") + " " + label);
    }


    /**
     * 장바구니 수량 배지를 만드는 헬퍼 메서드입니다.
     */
    public static String CartBadge(int count) {
        return Yellow("[ " + count + " ]");
    }


    /**
     * 기본 너비의 구분선을 출력합니다.
     */
    public static void Divider() {
        Divider(TABLE_WIDTH);
    }


    /**
     * 지정한 너비의 구분선을 출력합니다.
     */
    public static void Divider(int width) {
        System.out.println("─".repeat(Math.max(0, width)));
    }


    // ============================================================
    // 입력 프롬프트
    // ============================================================

    /**
     * 입력 프롬프트를 출력합니다. 줄을 바꾸지 않습니다.
     *
     * <pre>선택 &gt; </pre>
     *
     * @param label 프롬프트 이름 (예: 선택, 수량, 이메일)
     */
    public static void Prompt(String label) {
        System.out.print(INDENT + Cyan(label + " > "));
    }


    /**
     * 연속 입력 화면에서 필드 이름을 세로로 맞춰 출력하는 프롬프트입니다.
     *
     * <pre>
     * 이메일           &gt; user@example.com
     * 비밀번호         &gt; ************
     * </pre>
     *
     * @param label 필드 이름
     * @param width 필드 이름 칸의 너비
     */
    public static void Prompt(String label, int width) {
        System.out.print(INDENT + Cyan(PadRight(label, width) + "> "));
    }


    /**
     * 입력이 끝난 뒤 Enter로 다음 화면으로 넘어가게 하는 헬퍼 메서드입니다.
     */
    public static void PressEnter(Scanner scanner) {
        System.out.println();
        System.out.print(Cyan(INDENT + "Enter를 누르면 계속합니다. "));
        scanner.nextLine();
    }


    // ============================================================
    // 값 표시
    // ============================================================

    /**
     * 상세 화면의 "항목 이름 + 값" 한 줄을 출력합니다. 항목 이름은 CYAN입니다.
     */
    public static void Field(String label, String value) {
        Field(label, value, 14);
    }


    /**
     * 항목 이름 칸의 너비를 지정해 "항목 이름 + 값" 한 줄을 출력합니다.
     */
    public static void Field(String label, String value, int width) {
        System.out.println(INDENT + Cyan(PadRight(label, width)) + value);
    }


    // ============================================================
    // 메시지
    // ============================================================

    /**
     * 성공 메시지를 GREEN으로 출력합니다. (예: 주문이 정상적으로 완료되었습니다.)
     */
    public static void Success(String message) {
        System.out.println(Green(message));
    }


    /**
     * 오류 메시지를 RED로 출력합니다. 업무 수행이 불가능하거나 실패한 경우에 씁니다.
     */
    public static void Error(String message) {
        System.out.println(Red("[오류] " + message));
    }


    /**
     * 주의 메시지를 YELLOW로 출력합니다. 실패는 아니지만 사용자가 확인해야 하는 경우에 씁니다.
     */
    public static void Warn(String message) {
        System.out.println(Yellow(message));
    }


    /**
     * 일반 안내 문구를 기본색으로 출력합니다.
     */
    public static void Info(String message) {
        System.out.println(message);
    }


    /**
     * 메뉴 번호를 잘못 입력했을 때 쓰는 공통 문구입니다.
     */
    public static void InvalidMenu() {
        Error("올바른 메뉴 번호를 입력해 주세요.");
    }


    /**
     * 작업을 취소했을 때 쓰는 공통 문구입니다.
     */
    public static void Cancelled() {
        Warn("작업이 취소되었습니다.");
    }


    /**
     * Y / N 선택지를 출력하는 헬퍼 메서드입니다. Y는 GREEN, N은 RED입니다.
     *
     * @param yesLabel Y 설명 (예: 주문 확정)
     * @param noLabel  N 설명 (예: 취소)
     */
    public static void YesNoOptions(String yesLabel, String noLabel) {
        System.out.println(INDENT + Green("[Y] ") + yesLabel);
        System.out.println(INDENT + Red("[N] ") + noLabel);
    }


    // ============================================================
    // 상태값 색상
    // ============================================================

    /**
     * DB 상태값을 규칙에 맞는 색으로 바꿉니다. 화면에서도 DB와 같은 용어를 그대로 씁니다.
     *
     * <pre>
     * SELLING / AVAILABLE / CONFIRMED → GREEN
     * RETURNED                        → YELLOW
     * STOPPED / SOLD                  → RED
     * </pre>
     */
    public static String Status(String status) {

        if (status == null) {
            return "";
        }

        return switch (status) {
            case "SELLING", "AVAILABLE", "CONFIRMED" -> Green(status);
            case "RETURNED" -> Yellow(status);
            case "STOPPED", "SOLD" -> Red(status);
            default -> status;
        };
    }


    /**
     * 재고 수량을 상태에 맞는 색으로 바꿉니다.
     *
     * <pre>
     * 0            → RED
     * 안전재고 이하 → YELLOW
     * 그 외         → 기본색
     * </pre>
     *
     * @param stock       현재 재고
     * @param safetyStock 안전 재고. 기준이 없으면 null
     */
    public static String Stock(Integer stock, Integer safetyStock) {

        if (stock == null) {
            return "-";
        }

        String text = String.valueOf(stock);

        if (stock <= 0) {
            return Red(text);
        }

        if (safetyStock != null && stock <= safetyStock) {
            return Yellow(text);
        }

        return text;
    }


    /**
     * 안전 재고 기준 없이 재고 수량에 색을 입히는 헬퍼 메서드입니다.
     */
    public static String Stock(Integer stock) {
        return Stock(stock, null);
    }


    // ============================================================
    // 금액
    // ============================================================

    /**
     * 금액을 1,000 형식으로 바꿉니다.
     */
    public static String Money(BigDecimal amount) {
        return amount == null ? "0" : MONEY_FORMAT.format(amount);
    }


    /**
     * 금액을 1,000 형식으로 바꿉니다.
     */
    public static String Money(long amount) {
        return MONEY_FORMAT.format(amount);
    }


    /**
     * 금액을 "14,000원" 형식으로 바꾸고 YELLOW를 입힙니다. 합계·결제 금액에 씁니다.
     */
    public static String Amount(BigDecimal amount) {
        return Yellow(Money(amount) + "원");
    }


    /**
     * 금액을 "14,000원" 형식으로 바꾸고 YELLOW를 입힙니다.
     */
    public static String Amount(long amount) {
        return Yellow(Money(amount) + "원");
    }


    // ============================================================
    // 표 정렬 Helper
    // ============================================================

    /**
     * 터미널에서 차지하는 칸 수를 계산합니다. 한글은 2칸, 나머지는 1칸이며 ANSI 색상 코드는 세지 않습니다.
     */
    public static int Width(String text) {

        if (text == null) {
            return 0;
        }

        int width = 0;

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            // 색상 코드(ESC [ ... m)는 화면에서 자리를 차지하지 않으므로 건너뛴다
            if (c == '\u001B') {
                while (i < text.length() && text.charAt(i) != 'm') {
                    i++;
                }
                continue;
            }

            width += IsWide(c) ? 2 : 1;
        }

        return width;
    }


    /**
     * 한글처럼 터미널에서 2칸을 차지하는 문자인지 확인하는 헬퍼 메서드입니다.
     */
    private static boolean IsWide(char c) {
        return (c >= '가' && c <= '힣')    // 한글 음절
                || (c >= 'ᄀ' && c <= 'ᇿ')  // 한글 자모
                || (c >= '㄰' && c <= '㆏')  // 한글 호환 자모
                || (c >= '一' && c <= '鿿')  // 한자
                || (c >= '！' && c <= '｠'); // 전각 기호
    }


    /**
     * 표시 너비가 width가 되도록 오른쪽을 공백으로 채웁니다. (왼쪽 정렬)
     */
    public static String PadRight(String text, int width) {

        String value = text == null ? "" : text;

        return value + " ".repeat(Math.max(0, width - Width(value)));
    }


    /**
     * 표시 너비가 width가 되도록 왼쪽을 공백으로 채웁니다. (오른쪽 정렬)
     */
    public static String PadLeft(String text, int width) {

        String value = text == null ? "" : text;

        return " ".repeat(Math.max(0, width - Width(value))) + value;
    }


    /**
     * 표시 너비가 width가 되도록 좌우를 공백으로 채웁니다. (가운데 정렬)
     */
    public static String Center(String text, int width) {

        String value = text == null ? "" : text;

        int blank = Math.max(0, width - Width(value));
        int left = blank / 2;

        return " ".repeat(left) + value + " ".repeat(blank - left);
    }


    /**
     * 표시 너비가 maxWidth를 넘으면 잘라서 ".."을 붙입니다.
     */
    public static String Truncate(String text, int maxWidth) {

        String value = text == null ? "" : text;

        if (Width(value) <= maxWidth) {
            return value;
        }

        StringBuilder result = new StringBuilder();
        int width = 0;

        for (char c : value.toCharArray()) {

            int charWidth = IsWide(c) ? 2 : 1;

            if (width + charWidth > maxWidth - 2) {
                break;
            }

            result.append(c);
            width += charWidth;
        }

        return result + "..";
    }
}
