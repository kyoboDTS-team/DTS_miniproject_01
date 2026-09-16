package com.team.orderapp.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 데이터베이스 연결 생성 및 자원 해제를 관리하는 팩토리 클래스입니다.
 */
public class DbConnectionFactory {

    private static final String DEFAULT_CONFIG_PATH = "config/db.properties";

    /**
     * 데이터베이스 연결(Connection) 객체를 생성하여 반환합니다.
     *
     * @return DB Connection 객체
     * @throws SQLException 연결 실패 시 발생
     */
    public static Connection GetConnection() throws SQLException {
        // 추후 db.properties 로드 및 실제 JDBC 드라이버 연결 로직 구현 예정
        return null;
    }

    /**
     * 특정 데이터베이스 연결 자원을 안전하게 종료합니다.
     *
     * @param InConnection 닫을 Connection 객체
     */
    public static void CloseConnection(Connection InConnection) {
        if (InConnection != null) {
            try {
                InConnection.close();
            } catch (SQLException InException) {
                HandleCloseException(InException);
            }
        }
    }

    /**
     * 다중 AutoCloseable 리소스를 순차적으로 안전하게 종료하는 헬퍼 메서드입니다.
     *
     * @param InResources 닫을 자원 목록
     */
    public static void CloseResources(AutoCloseable... InResources) {
        for (AutoCloseable resource : InResources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception InException) {
                    HandleCloseException(InException);
                }
            }
        }
    }

    /**
     * 설정 파일을 읽어오는 헬퍼 메서드입니다.
     *
     * @param InConfigFilePath 설정 파일 경로
     * @return 로드된 Properties 객체
     */
    private static Properties LoadConfiguration(String InConfigFilePath) {
        // TODO: 파일 시스템에서 DB 설정 프로퍼티 파일 읽기
        return new Properties();
    }

    /**
     * JDBC 연결 URL 문자열을 조합하는 헬퍼 메서드입니다.
     *
     * @param InHost 호스트 주소
     * @param InPort 포트 번호
     * @param InDatabase 데이터베이스명
     * @return JDBC URL 문자열
     */
    private static String BuildConnectionUrl(String InHost, int InPort, String InDatabase) {
        return "jdbc:mysql://" + InHost + ":" + InPort + "/" + InDatabase;
    }

    /**
     * 자원 해제 중 발생한 예외를 로깅하거나 처리하는 헬퍼 메서드입니다.
     *
     * @param InException 발생한 예외 객체
     */
    private static void HandleCloseException(Exception InException) {
        System.err.println("자원 해제 중 오류 발생: " + InException.getMessage());
    }
}
