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
 * DB 연결 관련 객체를 생성하고 관리하는 공통 클래스
 *
 * 현재 프로젝트에서는 주로 MyBatis의 SqlSession을 생성하는 용도로 사용한다.
 *
 * 사용 예:
 *
 * try (SqlSession session = DbConnectionFactory.OpenSession()) {
 *     ProductDao dao = session.getMapper(ProductDao.class);
 * }
 */
public class DbConnectionFactory {

    /**
     * DB 접속 정보가 들어있는 properties 파일 경로
     *
     * 예:
     * db.driver=org.postgresql.Driver
     * db.url=jdbc:postgresql://localhost:5432/orderapp
     * db.username=postgres
     * db.password=1234
     */
    private static final String DEFAULT_CONFIG_PATH = "config/db.properties";

    /**
     * MyBatis 설정 파일
     *
     * resources 폴더 안의 mybatis-config.xml 파일을 의미한다.
     */
    private static final String MYBATIS_CONFIG = "mybatis-config.xml";


    /**
     * MyBatis에서 DB 연결을 생성하는 핵심 객체
     *
     * SqlSessionFactory는 프로그램 실행 중 계속 재사용한다.
     *
     * 쉽게 말하면:
     *
     * SqlSessionFactory
     *      ↓
     * SqlSession 생성
     *      ↓
     * Mapper(ProductDao 등) 사용
     *      ↓
     * SQL 실행
     */
    private static SqlSessionFactory sqlSessionFactory;


    /**
     * static 초기화 블록
     *
     * DbConnectionFactory 클래스가 처음 메모리에 올라올 때
     * 자동으로 딱 한 번 실행된다.
     *
     * 따라서 다른 클래스에서
     *
     * DbConnectionFactory.OpenSession();
     *
     * 을 처음 사용하는 순간 InitializeFactory()가 실행된다.
     */
    static {
        InitializeFactory();
    }


    /**
     * MyBatis SqlSessionFactory를 생성하는 메서드
     *
     * 실행 순서:
     *
     * 1. db.properties 읽기
     * 2. mybatis-config.xml 읽기
     * 3. 두 설정을 이용하여 SqlSessionFactory 생성
     */
    private static void InitializeFactory() {

        try {

            // --------------------------------------------
            // 1. DB 설정 파일 읽기
            // --------------------------------------------
            //
            // config/db.properties 파일의 내용을
            // Properties 객체로 가져온다.
            //
            // 예:
            //
            // db.url=jdbc:postgresql://localhost:5432/orderapp
            // db.username=postgres
            // db.password=1234
            //
            Properties props =
                    LoadProperties(DEFAULT_CONFIG_PATH);


            // --------------------------------------------
            // 2. MyBatis 설정 파일 읽기
            // --------------------------------------------
            //
            // Resources.getResourceAsStream()은
            // 일반적으로 src/main/resources 기준으로 파일을 찾는다.
            //
            InputStream inputStream =
                    Resources.getResourceAsStream(
                            MYBATIS_CONFIG
                    );


            // --------------------------------------------
            // 3. SqlSessionFactory 생성
            // --------------------------------------------
            //
            // mybatis-config.xml의 설정과
            // db.properties 값을 조합하여
            // SqlSessionFactory를 만든다.
            //
            // 여기서 만들어진 객체를 이후 계속 재사용한다.
            //
            sqlSessionFactory =
                    new SqlSessionFactoryBuilder()
                            .build(inputStream, props);


        } catch (Exception e) {

            // 초기화에 실패하면
            // sqlSessionFactory는 null 상태가 될 수 있다.
            System.out.println(
                    "MyBatis 초기화 실패: "
                            + e.getMessage()
            );
        }
    }


    /**
     * properties 파일을 읽어서 Properties 객체로 반환한다.
     *
     * @param filePath properties 파일 경로
     * @return properties 파일 내용
     */
    private static Properties LoadProperties(
            String filePath
    ) throws IOException {

        // key-value 형식의 설정값을 담는 객체
        Properties props = new Properties();


        // FileInputStream으로 파일을 연다.
        //
        // try-with-resources를 사용했기 때문에
        // 메서드가 끝나면 fis.close()가 자동 호출된다.
        //
        try (FileInputStream fis =
                     new FileInputStream(filePath)) {

            // properties 파일 내용을 읽어서
            // props 객체 안에 저장한다.
            props.load(fis);
        }


        return props;
    }


    /**
     * 생성된 SqlSessionFactory 자체를 반환한다.
     *
     * 보통 직접 사용할 일은 많지 않지만
     * 필요한 경우 Factory를 꺼내 사용할 수 있다.
     */
    public static SqlSessionFactory GetFactory() {

        return sqlSessionFactory;
    }


    /**
     * SqlSession을 생성한다.
     *
     * autoCommit 기본값은 false.
     *
     * 따라서 INSERT / UPDATE / DELETE를 실행했다면
     * 직접 session.commit()을 호출해야 한다.
     *
     * 예:
     *
     * SqlSession session =
     *         DbConnectionFactory.OpenSession();
     *
     * dao.Insert(product);
     *
     * session.commit();
     */
    public static SqlSession OpenSession() {

        // sqlSessionFactory가 정상적으로 생성됐다면
        // SqlSession 생성
        //
        // 생성되지 않았다면 null 반환
        return sqlSessionFactory != null
                ? sqlSessionFactory.openSession()
                : null;
    }


    /**
     * autoCommit 여부를 지정해서 SqlSession을 생성한다.
     *
     * true
     * → SQL 실행 즉시 자동 commit
     *
     * false
     * → session.commit()을 직접 호출해야 함
     *
     * @param autoCommit 자동 커밋 여부
     */
    public static SqlSession OpenSession(
            boolean autoCommit
    ) {

        return sqlSessionFactory != null
                ? sqlSessionFactory.openSession(autoCommit)
                : null;
    }


    /**
     * JDBC Connection 객체를 반환하기 위한 메서드
     *
     * 현재는 구현되어 있지 않다.
     *
     * 지금 프로젝트가 MyBatis 중심이라면
     * 이 메서드는 당장 사용하지 않아도 된다.
     */
    public static Connection GetConnection()
            throws SQLException {

        // TODO:
        // JDBC 방식으로 직접 Connection을 만들 필요가 있을 경우
        // 구현 예정
        //
        // 현재는 null 반환
        return null;
    }


    /**
     * JDBC Connection 객체를 안전하게 닫는다.
     *
     * MyBatis SqlSession을 사용하는 경우에는
     * 보통 이 메서드보다 SqlSession.close()를 사용한다.
     */
    public static void CloseConnection(
            Connection connection
    ) {

        // connection이 null이 아닌 경우에만 close
        if (connection != null) {

            try {

                connection.close();

            } catch (SQLException e) {

                HandleCloseException(e);
            }
        }
    }


    /**
     * 여러 개의 AutoCloseable 객체를 한 번에 닫기 위한
     * 공통 헬퍼 메서드
     *
     * AutoCloseable을 구현한 객체 예:
     *
     * Connection
     * PreparedStatement
     * ResultSet
     * SqlSession
     *
     * 사용 예:
     *
     * CloseResources(
     *     resultSet,
     *     statement,
     *     connection
     * );
     */
    public static void CloseResources(
            AutoCloseable... resources
    ) {

        // 전달받은 모든 리소스를 하나씩 확인
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
     * 설정 파일을 읽기 위한 헬퍼 메서드
     *
     * 현재는 구현되지 않았고
     * 실제 코드에서도 사용되지 않는다.
     *
     * LoadProperties()와 역할이 겹치기 때문에
     * 나중에 삭제하거나 하나로 통합해도 된다.
     */
    private static Properties LoadConfiguration(
            String configFilePath
    ) {

        return new Properties();
    }


    /**
     * JDBC URL을 생성하기 위한 메서드
     *
     * 현재 MySQL 주소 형태로 되어 있다.
     *
     * 현재 프로젝트가 PostgreSQL이라면
     * 이 코드는 프로젝트 DB와 맞지 않는다.
     *
     * PostgreSQL:
     *
     * jdbc:postgresql://localhost:5432/databaseName
     */
    private static String BuildConnectionUrl(
            String host,
            int port,
            String database
    ) {

        // 현재는 MySQL 주소
        return "jdbc:mysql://"
                + host
                + ":"
                + port
                + "/"
                + database;
    }


    /**
     * DB 자원을 닫는 과정에서 오류가 발생했을 때
     * 공통으로 오류 메시지를 출력한다.
     */
    private static void HandleCloseException(
            Exception e
    ) {

        System.err.println(
                "자원 해제 중 오류 발생: "
                        + e.getMessage()
        );
    }
}