package com.team.orderapp.auth;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

/**
 * 사용자(로그인 계정) 정보 데이터베이스 접근 객체(DAO/Mapper) 인터페이스입니다.
 * 실제 DB의 로그인 식별 컬럼명은 sql/schema.sql 문서상의 login_id가 아니라 email이다
 * (2026-09-18 실제 DB 조회로 확인, 문서와 실제 DB가 어긋나 있는 상태).
 */
public interface AppUserDao {

    /**
     * 이메일(로그인 아이디)을 기준으로 사용자를 단건 조회합니다.
     *
     * @param email 조회할 이메일
     * @return 조회된 AppUser Optional 객체
     */
    @Select("SELECT user_id, email, password_hash, role_code, is_active, created_at FROM app_user WHERE email = #{email}")
    Optional<AppUser> FindByEmail(@Param("email") String email);

    /**
     * 사용자 식별자(ID)를 기준으로 사용자를 단건 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 조회된 AppUser Optional 객체
     */
    @Select("SELECT user_id, email, password_hash, role_code, is_active, created_at FROM app_user WHERE user_id = #{userId}")
    Optional<AppUser> FindById(@Param("userId") Long userId);

    /**
     * 신규 사용자를 등록합니다. 성공하면 새로 생성된 user_id가 넘겨받은 user 객체에 채워집니다
     * (customer.user_id로 연결하려면 이 값이 필요해서 useGeneratedKeys를 켜뒀습니다).
     *
     * @param user 저장할 AppUser 객체
     * @return 저장 성공 여부
     */
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    @Insert("INSERT INTO app_user (email, password_hash, role_code, is_active) VALUES (#{email}, #{passwordHash}, #{roleCode}, #{isActive})")
    boolean Save(AppUser user);

    /**
     * 사용자 정보를 수정합니다.
     *
     * @param user 수정할 AppUser 객체
     * @return 수정 성공 여부
     */
    @Update("UPDATE app_user SET password_hash = #{passwordHash}, role_code = #{roleCode}, is_active = #{isActive} WHERE user_id = #{userId}")
    boolean Update(AppUser user);

    /**
     * 로그인 이메일(아이디)만 수정합니다. email 컬럼은 uq_app_user_email 유니크 제약이 있어,
     * 이미 쓰이는 이메일로 바꾸려 하면 DB가 예외를 던집니다.
     *
     * @param userId 수정할 사용자 ID
     * @param email  새 이메일
     * @return 수정 성공 여부
     */
    @Update("UPDATE app_user SET email = #{email} WHERE user_id = #{userId}")
    boolean UpdateEmail(@Param("userId") Long userId, @Param("email") String email);

    /**
     * 사용자 식별자로 사용자를 삭제합니다.
     *
     * @param userId 삭제할 사용자 ID
     * @return 삭제 성공 여부
     */
    @Delete("DELETE FROM app_user WHERE user_id = #{userId}")
    boolean DeleteById(@Param("userId") Long userId);
}
