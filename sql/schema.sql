-- 1. app_user: 로그인 계정
CREATE TABLE app_user (
                          user_id BIGSERIAL PRIMARY KEY,
                          login_id VARCHAR(30) UNIQUE NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          role_code VARCHAR(20) NOT NULL CHECK(role_code IN('ADMIN', 'CUSTOMER')),
                          is_active BOOLEAN NOT NULL DEFAULT true,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. customer: 고객 업무 정보
CREATE TABLE customer (
                          customer_id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT UNIQUE NOT NULL REFERENCES app_user(user_id) ON DELETE RESTRICT,
                          customer_name VARCHAR(50) NOT NULL CHECK(TRIM(customer_name) != ''),
    phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. category: 상품 분류
CREATE TABLE category (
                          category_id BIGSERIAL PRIMARY KEY,
                          category_code VARCHAR(20) UNIQUE NOT NULL,
                          category_name VARCHAR(50) UNIQUE NOT NULL
);

-- 4. product: 상품과 현재 재고
CREATE TABLE product (
                         product_id BIGSERIAL PRIMARY KEY,
                         product_code VARCHAR(30) UNIQUE NOT NULL,
                         category_id BIGINT NOT NULL REFERENCES category(category_id) ON DELETE RESTRICT,
                         product_name VARCHAR(100) NOT NULL,
                         price DECIMAL(12,0) NOT NULL CHECK(price >= 0),
                         stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK(stock_quantity >= 0),
                         reorder_level INTEGER NOT NULL DEFAULT 0 CHECK(reorder_level >= 0),
                         sale_status VARCHAR(20) NOT NULL DEFAULT 'SELLING',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. orders: 주문의 공통 정보
CREATE TABLE orders (
                        order_id BIGSERIAL PRIMARY KEY,
                        customer_id BIGINT NOT NULL REFERENCES customer(customer_id) ON DELETE RESTRICT,
                        ordered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(20) NOT NULL DEFAULT 'PLACED'
);

-- 6. order_item: 주문 상품 상세
CREATE TABLE order_item (
                            order_id BIGINT NOT NULL REFERENCES orders(order_id) ON DELETE RESTRICT,
                            product_id BIGINT NOT NULL REFERENCES product(product_id) ON DELETE RESTRICT,
                            quantity INTEGER NOT NULL CHECK(quantity >= 1),
                            unit_price DECIMAL(12,0) NOT NULL CHECK(unit_price >= 0),
                            PRIMARY KEY(order_id, product_id)
);

-- 7. stock_adjustment: 주문 외 재고 변화
CREATE TABLE stock_adjustment (
                                  adjustment_id BIGSERIAL PRIMARY KEY,
                                  product_id BIGINT NOT NULL REFERENCES product(product_id) ON DELETE RESTRICT,
                                  quantity_delta INTEGER NOT NULL CHECK(quantity_delta != 0),
    reason VARCHAR(200) NOT NULL CHECK(TRIM(reason) != ''),
    adjusted_by_user_id BIGINT NOT NULL REFERENCES app_user(user_id) ON DELETE RESTRICT,
    adjusted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);