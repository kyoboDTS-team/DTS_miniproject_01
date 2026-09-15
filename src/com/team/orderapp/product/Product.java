package com.team.orderapp.product;

/**
 * 상품 엔티티/도메인 모델 클래스입니다.
 */
public class Product {

    private Long productId;
    private String name;
    private double price;
    private int currentStock;
    private String description;
    private String category;

    public Product() {
    }

    public Product(Long InProductId, String InName, double InPrice, int InCurrentStock) {
        this.productId = InProductId;
        this.name = InName;
        this.price = InPrice;
        this.currentStock = InCurrentStock;
    }

    public Product(Long InProductId, String InName, double InPrice, int InCurrentStock, String InDescription, String InCategory) {
        this.productId = InProductId;
        this.name = InName;
        this.price = InPrice;
        this.currentStock = InCurrentStock;
        this.description = InDescription;
        this.category = InCategory;
    }

    public Long GetProductId() {
        return productId;
    }

    public void SetProductId(Long InProductId) {
        this.productId = InProductId;
    }

    public String GetName() {
        return name;
    }

    public void SetName(String InName) {
        this.name = InName;
    }

    public double GetPrice() {
        return price;
    }

    public void SetPrice(double InPrice) {
        this.price = InPrice;
    }

    public int GetCurrentStock() {
        return currentStock;
    }

    public void SetCurrentStock(int InCurrentStock) {
        this.currentStock = InCurrentStock;
    }

    public String GetDescription() {
        return description;
    }

    public void SetDescription(String InDescription) {
        this.description = InDescription;
    }

    public String GetCategory() {
        return category;
    }

    public void SetCategory(String InCategory) {
        this.category = InCategory;
    }

    /**
     * 재고가 충분한지 여부를 확인하는 헬퍼 메서드입니다.
     *
     * @param InRequiredQuantity 필요한 재고 수량
     * @return 재고 충족 여부
     */
    public boolean HasEnoughStock(int InRequiredQuantity) {
        return this.currentStock >= InRequiredQuantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", currentStock=" + currentStock +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
