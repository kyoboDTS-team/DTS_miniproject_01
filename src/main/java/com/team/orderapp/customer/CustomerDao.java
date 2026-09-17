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
    @Select("SELECT customer_id, user_id, customer_name, phone, created_at FROM customer WHERE customer_id = #{customerId}")
    Optional<Customer> FindById(@Param("customerId") Long customerId);

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
    @Select("SELECT customer_id, user_id, customer_name, phone, created_at FROM customer ORDER BY customer_id")
    List<Customer> FindAll();

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
