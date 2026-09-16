package com.team.orderapp.product;

import com.team.orderapp.common.BusinessException;

import java.util.List;

/**
 * 상품 관리 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
public class ProductService {

    private final ProductDao productDao;

    public ProductService() {
        this.productDao = new ProductDao();
    }

    public ProductService(ProductDao InProductDao) {
        this.productDao = InProductDao;
    }

    /**
     * 신규 상품을 등록합니다.
     *
     * @param InProduct 등록할 상품 객체
     * @return 등록 완료된 Product 객체
     */
    public Product CreateProduct(Product InProduct) {
        ValidateProduct(InProduct);

        boolean success = productDao.Insert(InProduct);
        if (!success) {
            throw new BusinessException("상품 등록에 실패하였습니다.");
        }
        return InProduct;
    }

    /**
     * 상품 ID(int)로 상품을 조회합니다.
     *
     * @param InProductId 상품 정수형 식별자
     * @return 조회된 Product 객체
     */
    public Product GetProduct(int InProductId) {
        return GetProductById((long) InProductId);
    }

    /**
     * 상품 식별자(Long)로 상품을 조회합니다.
     *
     * @param InProductId 상품 식별자
     * @return 조회된 Product 객체
     */
    public Product GetProductById(Long InProductId) {
        return productDao.FindById(InProductId)
                .orElseThrow(() -> new BusinessException("상품을 찾을 수 없습니다: ID " + InProductId));
    }

    /**
     * 전체 상품 목록을 반환합니다.
     *
     * @return 상품 목록 리스트
     */
    public List<Product> GetAllProducts() {
        return productDao.FindAll();
    }

    /**
     * 상품 정보를 수정합니다.
     *
     * @param InProduct 수정할 상품 정보
     */
    public void UpdateProduct(Product InProduct) {
        ValidateProduct(InProduct);
        GetProductById(InProduct.GetProductId()); // 존재 여부 확인

        boolean success = productDao.Update(InProduct);
        if (!success) {
            throw new BusinessException("상품 정보 수정에 실패하였습니다.");
        }
    }

    /**
     * 상품을 삭제합니다.
     *
     * @param InProductId 삭제할 상품 식별자
     */
    public void DeleteProduct(Long InProductId) {
        GetProductById(InProductId); // 존재 여부 확인
        boolean success = productDao.DeleteById(InProductId);
        if (!success) {
            throw new BusinessException("상품 삭제에 실패하였습니다.");
        }
    }

    /**
     * 상품 정보 유효성을 검사하는 헬퍼 메서드입니다.
     *
     * @param InProduct 검증할 상품 객체
     */
    private void ValidateProduct(Product InProduct) {
        if (InProduct == null) {
            throw new BusinessException("상품 정보가 누락되었습니다.");
        }
        if (InProduct.GetName() == null || InProduct.GetName().trim().isEmpty()) {
            throw new BusinessException("상품 이름은 필수입니다.");
        }
        if (InProduct.GetPrice() < 0) {
            throw new BusinessException("상품 가격은 0원 이상이어야 합니다.");
        }
        if (InProduct.GetCurrentStock() < 0) {
            throw new BusinessException("상품 재고는 0개 이상이어야 합니다.");
        }
    }
}
