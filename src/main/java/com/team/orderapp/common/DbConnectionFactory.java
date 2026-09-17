package com.team.orderapp.common;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 데이터베이스 연결 생성 및 자원 해제를 관리하는 팩토리 클래스입니다.
 */
public class DbConnectionFactory {

    private static final String DEFAULT_CONFIG_PATH = "config/db.properties";
    private static final String MYBATIS_CONFIG = "mybatis-config.xml";
    private static SqlSessionFactory sqlSessionFactory;

    static {
        InitializeFactory();
    }

    private static void InitializeFactory() {
        try {
            Properties props = LoadProperties(DEFAULT_CONFIG_PATH);
            InputStream inputStream = Resources.getResourceAsStream(MYBATIS_CONFIG);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props);
        } catch (Exception e) {
            System.out.println("MyBatis 초기화 실패: " + e.getMessage());
        }
    }

    private static Properties LoadProperties(String filePath) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            props.load(fis);
        }
        return props;
    }

    public static SqlSessionFactory GetFactory() {
        return sqlSessionFactory;
    }

    public static SqlSession OpenSession() {
        return sqlSessionFactory != null ? sqlSessionFactory.openSession() : null;
    }

    public static SqlSession OpenSession(boolean autoCommit) {
        return sqlSessionFactory != null ? sqlSessionFactory.openSession(autoCommit) : null;
    }

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
     * @param connection 닫을 Connection 객체
     */
    public static void CloseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                HandleCloseException(e);
            }
        }
    }

    /**
     * 다중 AutoCloseable 리소스를 순차적으로 안전하게 종료하는 헬퍼 메서드입니다.
     *
     * @param resources 닫을 자원 목록
     */
    public static void CloseResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    HandleCloseException(e);
                }
            }
        }
    }

    /**
     * 설정 파일을 읽어오는 헬퍼 메서드입니다.
     *
     * @param configFilePath 설정 파일 경로
     * @return 로드된 Properties 객체
     */
    private static Properties LoadConfiguration(String configFilePath) {
        return new Properties();
    }

    /**
     * JDBC 연결 URL 문자열을 조합하는 헬퍼 메서드입니다.
     *
     * @param host 호스트 주소
     * @param port 포트 번호
     * @param database 데이터베이스명
     * @return JDBC URL 문자열
     */
    private static String BuildConnectionUrl(String host, int port, String database) {
        return "jdbc:mysql://" + host + ":" + port + "/" + database;
    }

    /**
     * 자원 해제 중 발생한 예외를 로깅하거나 처리하는 헬퍼 메서드입니다.
     *
     * @param e 발생한 예외 객체
     */
    private static void HandleCloseException(Exception e) {
        System.err.println("자원 해제 중 오류 발생: " + e.getMessage());
    }
}
