package com.team.orderapp.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 데이터를 CSV 파일로 내보내는 유틸리티 클래스입니다.
 */
public class CsvExporter {

    /**
     * 헤더와 행 데이터를 지정된 경로의 CSV 파일로 저장합니다.
     *
     * @param InFilePath 저장할 파일 경로
     * @param InHeaders 열 헤더 목록
     * @param InRows 각 행의 데이터 목록
     * @return 내보내기 성공 여부
     */
    public static boolean ExportToCsv(String InFilePath, List<String> InHeaders, List<List<String>> InRows) {
        EnsureParentDirectoryExists(InFilePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(InFilePath, StandardCharsets.UTF_8))) {
            if (InHeaders != null && !InHeaders.isEmpty()) {
                writer.write(FormatCsvLine(InHeaders));
                writer.newLine();
            }

            if (InRows != null) {
                for (List<String> row : InRows) {
                    writer.write(FormatCsvLine(row));
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException InException) {
            System.err.println("CSV 내보내기 중 오류 발생: " + InException.getMessage());
            return false;
        }
    }

    /**
     * 필드 리스트를 콤마로 구분된 단일 CSV 라인 문자열로 변환하는 헬퍼 메서드입니다.
     *
     * @param InFields 필드 값 리스트
     * @return 포맷팅된 CSV 라인 문자열
     */
    private static String FormatCsvLine(List<String> InFields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < InFields.size(); i++) {
            sb.append(EscapeSpecialCharacters(InFields.get(i)));
            if (i < InFields.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * CSV 특수문자(쉼표, 큰따옴표, 개행 등)를 이스케이프 처리하는 헬퍼 메서드입니다.
     *
     * @param InValue 원본 문자열 값
     * @return 이스케이프 처리된 문자열
     */
    private static String EscapeSpecialCharacters(String InValue) {
        if (InValue == null) {
            return "";
        }
        String escaped = InValue.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            escaped = "\"" + escaped + "\"";
        }
        return escaped;
    }

    /**
     * 파일 저장을 위한 상위 디렉터리가 존재하지 않을 경우 자동 생성하는 헬퍼 메서드입니다.
     *
     * @param InFilePath 대상 파일 경로
     */
    private static void EnsureParentDirectoryExists(String InFilePath) {
        File file = new File(InFilePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }
}
