package com.team.orderapp.customer;

import com.team.orderapp.common.BusinessException;

import java.util.List;

/**
 * 고객 관리 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService() {
        this.customerDao = new CustomerDao();
    }

    public CustomerService(CustomerDao InCustomerDao) {
        this.customerDao = InCustomerDao;
    }

    /**
     * 신규 고객을 등록합니다.
     *
     * @param InCustomer 등록할 고객 객체
     * @return 등록된 고객 객체
     */
    public Customer CreateCustomer(Customer InCustomer) {
        ValidateCustomer(InCustomer);

        boolean success = customerDao.Insert(InCustomer);
        if (!success) {
            throw new BusinessException("고객 등록에 실패하였습니다.");
        }
        return InCustomer;
    }

    /**
     * ID로 고객을 단건 조회합니다.
     *
     * @param InCustomerId 고객 식별자
     * @return 조회된 Customer 객체
     */
    public Customer GetCustomerById(Long InCustomerId) {
        return customerDao.FindById(InCustomerId)
                .orElseThrow(() -> new BusinessException("고객을 찾을 수 없습니다: ID " + InCustomerId));
    }

    /**
     * 전체 고객 목록을 조회합니다.
     *
     * @return 고객 목록 리스트
     */
    public List<Customer> GetAllCustomers() {
        return customerDao.FindAll();
    }

    /**
     * 고객 정보를 수정합니다.
     *
     * @param InCustomer 수정할 고객 정보
     */
    public void UpdateCustomer(Customer InCustomer) {
        ValidateCustomer(InCustomer);
        GetCustomerById(InCustomer.GetCustomerId()); // 존재 여부 확인

        boolean success = customerDao.Update(InCustomer);
        if (!success) {
            throw new BusinessException("고객 정보 수정에 실패하였습니다.");
        }
    }

    /**
     * 고객 식별자로 고객 정보를 삭제합니다.
     *
     * @param InCustomerId 삭제할 고객 식별자
     */
    public void DeleteCustomer(Long InCustomerId) {
        GetCustomerById(InCustomerId); // 존재 여부 확인
        boolean success = customerDao.DeleteById(InCustomerId);
        if (!success) {
            throw new BusinessException("고객 정보 삭제에 실패하였습니다.");
        }
    }

    /**
     * 고객 정보 유효성을 검증하는 헬퍼 메서드입니다.
     *
     * @param InCustomer 검증 대상 고객 객체
     */
    private void ValidateCustomer(Customer InCustomer) {
        if (InCustomer == null) {
            throw new BusinessException("고객 정보가 누락되었습니다.");
        }
        if (InCustomer.GetName() == null || InCustomer.GetName().trim().isEmpty()) {
            throw new BusinessException("고객 이름은 필수 항목입니다.");
        }
        if (InCustomer.GetPhone() == null || InCustomer.GetPhone().trim().isEmpty()) {
            throw new BusinessException("고객 연락처는 필수 항목입니다.");
        }
    }
}
