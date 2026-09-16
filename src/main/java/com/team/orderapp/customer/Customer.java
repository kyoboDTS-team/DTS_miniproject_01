package com.team.orderapp.customer;

import java.time.LocalDateTime;

/**
 * 고객 엔티티/도메인 모델 클래스입니다.
 */
public class Customer {

    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;

    public Customer() {
    }

    public Customer(Long InCustomerId, String InName, String InEmail, String InPhone, String InAddress) {
        this.customerId = InCustomerId;
        this.name = InName;
        this.email = InEmail;
        this.phone = InPhone;
        this.address = InAddress;
        this.createdAt = LocalDateTime.now();
    }

    public Customer(Long InCustomerId, String InName, String InEmail, String InPhone, String InAddress, LocalDateTime InCreatedAt) {
        this.customerId = InCustomerId;
        this.name = InName;
        this.email = InEmail;
        this.phone = InPhone;
        this.address = InAddress;
        this.createdAt = InCreatedAt;
    }

    public Long GetCustomerId() {
        return customerId;
    }

    public void SetCustomerId(Long InCustomerId) {
        this.customerId = InCustomerId;
    }

    public String GetName() {
        return name;
    }

    public void SetName(String InName) {
        this.name = InName;
    }

    public String GetEmail() {
        return email;
    }

    public void SetEmail(String InEmail) {
        this.email = InEmail;
    }

    public String GetPhone() {
        return phone;
    }

    public void SetPhone(String InPhone) {
        this.phone = InPhone;
    }

    public String GetAddress() {
        return address;
    }

    public void SetAddress(String InAddress) {
        this.address = InAddress;
    }

    public LocalDateTime GetCreatedAt() {
        return createdAt;
    }

    public void SetCreatedAt(LocalDateTime InCreatedAt) {
        this.createdAt = InCreatedAt;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
