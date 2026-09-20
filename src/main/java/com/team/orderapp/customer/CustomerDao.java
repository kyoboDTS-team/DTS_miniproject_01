package com.team.orderapp.customer;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 고객 정보 데이터베이스 접근 객체(DAO/Mapper) 인터페이스입니다.
 */
public interface CustomerDao {

    /**
     * 고객 정보를 데이터베이스에 삽입합니다.
     *
     * @param customer 저장할 Customer 객체
     * @return 저장 성공 여부
     */
    @Insert("INSERT INTO customer (user_id, customer_name, phone) VALUES (#{userId}, #{customerName}, #{phone})")
    boolean Insert(Customer customer);

    /**
     * 식별자(ID)를 기준으로 고객 정보를 조회합니다.
     *
     * @param customerId 조회할 고객 ID
     * @return 조회된 Customer Optional 객체
     */
    @Select("SELECT c.customer_id, c.user_id, c.customer_name, c.phone, c.created_at, u.email, u.is_active " +
            "FROM customer c JOIN app_user u ON c.user_id = u.user_id " +
            "WHERE c.customer_id = #{customerId}")
    Optional<Customer> FindById(@Param("customerId") Long customerId);

    /**
     * 이메일로 고객을 정확히 한 명 조회합니다. 로그인한 회원 본인 정보를 찾을 때 사용합니다.
     *
     * @param email 조회할 이메일
     * @return 조회된 Customer Optional 객체
     */
    @Select("SELECT c.customer_id, c.user_id, c.customer_name, c.phone, c.created_at, u.email, u.is_active " +
            "FROM customer c JOIN app_user u ON c.user_id = u.user_id " +
            "WHERE u.email = #{email}")
    Optional<Customer> FindByEmail(@Param("email") String email);

    /**
     * 유저 식별자(userId)를 기준으로 고객 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 조회된 Customer Optional 객체
     */
    @Select("SELECT customer_id, user_id, customer_name, phone, created_at FROM customer WHERE user_id = #{userId}")
    Optional<Customer> FindByUserId(@Param("userId") Long userId);

    /**
     * 전체 고객 목록을 조회합니다.
     *
     * @return 고객 목록 리스트
     */
    @Select("SELECT c.customer_id, c.user_id, c.customer_name, c.phone, c.created_at, u.email, u.is_active " +
            "FROM customer c JOIN app_user u ON c.user_id = u.user_id " +
            "ORDER BY c.customer_id")
    List<Customer> FindAll();

    /**
     * 전체 고객 수를 조회합니다. 페이지 개수를 계산할 때 사용합니다.
     *
     * @return 전체 고객 수
     */
    @Select("SELECT COUNT(*) FROM customer")
    long CountAll();

    /**
     * 고객 목록을 페이지 단위로 나누어 조회합니다.
     *
     * @param limit  한 페이지에 보여줄 개수
     * @param offset 건너뛸 개수 (0부터 시작)
     * @return 해당 페이지에 속하는 고객 목록
     */
    @Select("SELECT c.customer_id, c.user_id, c.customer_name, c.phone, c.created_at, u.email, u.is_active " +
            "FROM customer c JOIN app_user u ON c.user_id = u.user_id " +
            "ORDER BY c.customer_id LIMIT #{limit} OFFSET #{offset}")
    List<Customer> FindPage(@Param("limit") int limit, @Param("offset") int offset);

    /**
     * 이름으로 고객을 조회합니다. 동명이인이 있으면 여러 명이 반환될 수 있습니다.
     *
     * @param customerName 검색할 이름
     * @return 검색된 고객 목록
     */
    @Select("SELECT c.customer_id, c.user_id, c.customer_name, c.phone, c.created_at, u.email, u.is_active " +
            "FROM customer c JOIN app_user u ON c.user_id = u.user_id " +
            "WHERE c.customer_name LIKE CONCAT('%', #{customerName}, '%') ORDER BY c.customer_id")
    List<Customer> FindByName(@Param("customerName") String customerName);

    /**
     * 이름 또는 전화번호에 검색어가 포함된 고객을 조회합니다.
     * 전화번호는 하이픈(-)을 무시하고 비교하므로, "010"을 뗀 8자리나 뒷자리 4자리만 입력해도 검색됩니다.
     *
     * @param keyword 검색어
     * @return 검색된 고객 목록
     */
    @Select("SELECT c.customer_id, c.user_id, c.customer_name, c.phone, c.created_at, u.email, u.is_active " +
            "FROM customer c JOIN app_user u ON c.user_id = u.user_id " +
            "WHERE c.customer_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR REPLACE(c.phone, '-', '') LIKE CONCAT('%', REPLACE(#{keyword}, '-', ''), '%') " +
            "ORDER BY c.customer_id")
    List<Customer> SearchByNameOrPhone(@Param("keyword") String keyword);

    /**
     * 이름 또는 이메일(아이디)에 검색어가 포함된 고객을 조회합니다. 정보 수정 대상을 찾을 때 사용합니다.
     *
     * @param keyword 검색어
     * @return 검색된 고객 목록
     */
    @Select("SELECT c.customer_id, c.user_id, c.customer_name, c.phone, c.created_at, u.email, u.is_active " +
            "FROM customer c JOIN app_user u ON c.user_id = u.user_id " +
            "WHERE c.customer_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.email LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY c.customer_id")
    List<Customer> SearchByNameOrEmail(@Param("keyword") String keyword);

    /**
     * 전화번호로 이미 등록된 고객이 있는지 셉니다. 회원가입 시 전화번호 중복 체크에 사용합니다.
     * 전화번호는 phone 컬럼에 유니크 제약이 없어서, 앱에서 직접 확인해야 합니다.
     * 하이픈(-)을 무시하고 비교합니다.
     *
     * @param phone 확인할 전화번호
     * @return 이미 등록된 건수
     */
    @Select("SELECT COUNT(*) FROM customer WHERE REPLACE(phone, '-', '') = REPLACE(#{phone}, '-', '')")
    long CountByPhone(@Param("phone") String phone);

    /**
     * 전화번호로 이미 등록된 다른 고객이 있는지 셉니다. 본인 정보 수정 시, 자기 자신의 기존 번호는
     * 중복으로 잘못 잡지 않도록 excludeCustomerId로 본인을 제외합니다.
     *
     * @param phone            확인할 전화번호
     * @param excludeCustomerId 제외할 고객 ID (본인)
     * @return 이미 등록된 건수
     */
    @Select("SELECT COUNT(*) FROM customer WHERE REPLACE(phone, '-', '') = REPLACE(#{phone}, '-', '') " +
            "AND customer_id <> #{excludeCustomerId}")
    long CountByPhoneExcluding(@Param("phone") String phone, @Param("excludeCustomerId") Long excludeCustomerId);

    /**
     * 고객 정보를 갱신합니다.
     *
     * @param customer 갱신할 Customer 객체
     * @return 갱신 성공 여부
     */
    @Update("UPDATE customer SET customer_name = #{customerName}, phone = #{phone} WHERE customer_id = #{customerId}")
    boolean Update(Customer customer);

    /**
     * 고객 식별자를 기준으로 정보를 삭제합니다.
     *
     * @param customerId 삭제할 고객 ID
     * @return 삭제 성공 여부
     */
    @Delete("DELETE FROM customer WHERE customer_id = #{customerId}")
    boolean DeleteById(@Param("customerId") Long customerId);
}
