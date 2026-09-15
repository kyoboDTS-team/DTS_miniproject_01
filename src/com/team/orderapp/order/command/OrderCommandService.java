package com.team.orderapp.order.command;

import com.team.orderapp.common.BusinessException;
import com.team.orderapp.customer.Customer;
import com.team.orderapp.order.model.Order;
import com.team.orderapp.order.model.OrderItem;
import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductDao;

import java.util.List;

/**
 * 주문 접수, 변경, 취소 등 주문 CUD 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
public class OrderCommandService {

    private final OrderCommandDao orderCommandDao;
    private final ProductDao productDao;

    public OrderCommandService() {
        this.orderCommandDao = new OrderCommandDao();
        this.productDao = new ProductDao();
    }

    public OrderCommandService(OrderCommandDao InOrderCommandDao, ProductDao InProductDao) {
        this.orderCommandDao = InOrderCommandDao;
        this.productDao = InProductDao;
    }

    /**
     * 고객 정보와 함께 신규 주문을 저장하고 재고를 차감합니다.
     *
     * @param InOrder 저장할 주문 객체
     * @param InCustomer 주문 고객 객체
     */
    public void SaveOrder(Order InOrder, Customer InCustomer) {
        if (InCustomer == null || InCustomer.GetCustomerId() == null) {
            throw new BusinessException("유효한 고객 정보가 필요합니다.");
        }
        InOrder.SetCustomerId(InCustomer.GetCustomerId());
        PlaceOrder(InOrder);
    }

    /**
     * 신규 주문을 접수하고 재고를 차감합니다.
     *
     * @param InOrder 접수할 주문 객체
     * @return 주문 ID가 설정된 Order 객체
     */
    public Order PlaceOrder(Order InOrder) {
        ValidateOrder(InOrder);
        VerifyAndDeductStock(InOrder.GetItems());

        InOrder.SetStatus("COMPLETED");
        Long orderId = orderCommandDao.InsertOrder(InOrder);
        if (orderId == null) {
            throw new BusinessException("주문 저장에 실패하였습니다.");
        }
        InOrder.SetOrderId(orderId);

        for (OrderItem item : InOrder.GetItems()) {
            item.SetOrderId(orderId);
        }
        boolean itemsSaved = orderCommandDao.InsertOrderItems(InOrder.GetItems());
        if (!itemsSaved) {
            throw new BusinessException("주문 상세 항목 저장에 실패하였습니다.");
        }

        return InOrder;
    }

    /**
     * 기존 주문을 취소하고 재고를 복구합니다.
     *
     * @param InOrderId 취소할 주문 식별자
     * @param InReason 주문 취소 사유
     */
    public void CancelOrder(Long InOrderId, String InReason) {
        if (InOrderId == null) {
            throw new BusinessException("주문 ID는 필수입니다.");
        }
        // TODO: 기존 주문 상세 항목 조회 후 재고 원복 처리 구현
        boolean updated = orderCommandDao.UpdateOrderStatus(InOrderId, "CANCELLED");
        if (!updated) {
            throw new BusinessException("주문 취소 처리에 실패하였습니다.");
        }
    }

    /**
     * 주문 기본 정보 및 항목 목록의 유효성을 검증하는 헬퍼 메서드입니다.
     *
     * @param InOrder 검증할 주문 객체
     */
    private void ValidateOrder(Order InOrder) {
        if (InOrder == null) {
            throw new BusinessException("주문 정보가 누락되었습니다.");
        }
        if (InOrder.GetCustomerId() == null) {
            throw new BusinessException("주문자(고객) 정보가 누락되었습니다.");
        }
        if (InOrder.GetItems() == null || InOrder.GetItems().isEmpty()) {
            throw new BusinessException("최소 1개 이상의 상품을 주문해야 합니다.");
        }
    }

    /**
     * 각 주문 상품의 재고를 검증하고 차감하는 헬퍼 메서드입니다.
     *
     * @param InItems 주문 항목 목록
     */
    private void VerifyAndDeductStock(List<OrderItem> InItems) {
        for (OrderItem item : InItems) {
            Product product = productDao.FindById(item.GetProductId())
                    .orElseThrow(() -> new BusinessException("상품을 찾을 수 없습니다: ID " + item.GetProductId()));

            if (!product.HasEnoughStock(item.GetQuantity())) {
                throw new BusinessException("재고가 부족합니다: " + product.GetName() + " (현재고: " + product.GetCurrentStock() + ")");
            }
            productDao.UpdateStock(item.GetProductId(), -item.GetQuantity());
        }
    }
}
