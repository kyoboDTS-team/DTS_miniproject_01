package com.team.orderapp.common;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyBatisFactory {
    private static SqlSessionFactory sqlSessionFactory;
    private static final String CONFIG_PATH = "config/db.properties";
    private static final String MYBATIS_CONFIG = "mybatis-config.xml";

    static {
        InitializeFactory();
    }

    private static void InitializeFactory() {
        try {
            Properties props = LoadProperties(CONFIG_PATH);
            InputStream inputStream = Resources.getResourceAsStream(MYBATIS_CONFIG);
            // 읽어온 프로퍼티를 마이바티스 빌더에 쏙 넣어줌
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props);
        } catch (Exception e) {
            System.out.println("MyBatis 초기화 실패: " + e.getMessage());
        }
    }

    private static Properties LoadProperties(String InFilePath) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(InFilePath)) {
            props.load(fis);
        }
        return props;
    }

    public static SqlSessionFactory GetFactory() {
        return sqlSessionFactory;
    }
}