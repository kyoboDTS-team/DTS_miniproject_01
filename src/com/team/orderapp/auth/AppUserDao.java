package com.team.orderapp.auth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 사용자 정보 데이터베이스 접근 객체(DAO) 클래스입니다.
 */
public class AppUserDao {

    /**
     * 사용자명을 기준으로 사용자를 단건 조회합니다.
     *
     * @param InUsername 조회할 사용자명
     * @return 조회된 AppUser Optional 객체
     */
    public Optional<AppUser> FindByUsername(String InUsername) {
        // TODO: SQL 실행 및 사용자 조회 구현
        return Optional.empty();
    }

    /**
     * 사용자 식별자(ID)를 기준으로 사용자를 단건 조회합니다.
     *
     * @param InUserId 조회할 사용자 ID
     * @return 조회된 AppUser Optional 객체
     */
    public Optional<AppUser> FindById(Long InUserId) {
        // TODO: SQL 실행 및 사용자 조회 구현
        return Optional.empty();
    }

    /**
     * 신규 사용자를 등록합니다.
     *
     * @param InUser 저장할 AppUser 객체
     * @return 저장 성공 여부
     */
    public boolean Save(AppUser InUser) {
        // TODO: 사용자 INSERT 쿼리 구현
        return false;
    }

    /**
     * 사용자 정보를 수정합니다.
     *
     * @param InUser 수정할 AppUser 객체
     * @return 수정 성공 여부
     */
    public boolean Update(AppUser InUser) {
        // TODO: 사용자 UPDATE 쿼리 구현
        return false;
    }

    /**
     * 사용자 식별자로 사용자를 삭제합니다.
     *
     * @param InUserId 삭제할 사용자 ID
     * @return 삭제 성공 여부
     */
    public boolean DeleteById(Long InUserId) {
        // TODO: 사용자 DELETE 쿼리 구현
        return false;
    }

    /**
     * ResultSet 결과 행을 AppUser 객체로 매핑하는 헬퍼 메서드입니다.
     *
     * @param InResultSet SQL 쿼리 결과셋
     * @return 매핑된 AppUser 객체
     * @throws SQLException 매핑 실패 시 발생
     */
    private AppUser MapResultSetToUser(ResultSet InResultSet) throws SQLException {
        AppUser user = new AppUser();
        user.SetUserId(InResultSet.getLong("user_id"));
        user.SetUsername(InResultSet.getString("username"));
        user.SetPasswordHash(InResultSet.getString("password_hash"));
        user.SetRole(UserRole.FromString(InResultSet.getString("role")));
        return user;
    }
}
