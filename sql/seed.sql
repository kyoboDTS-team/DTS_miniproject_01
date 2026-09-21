INSERT INTO category (category_id, category_code, category_name) VALUES
(1, 'C001', '필기구'),
(2, 'C002', '노트·종이류'),
(3, 'C003', '학습·보조 문구'),
(4, 'C004', '미술·만들기'),
(5, 'C005', '사무·정리용품'),
(6, 'C006', '포장·꾸미기'),
(7, 'C007', '완구·놀이');

-- 장바구니 기능 구현에 필요한 더미 데이터
-- 작성자: 김상진 (Sep 18)
INSERT INTO cart (customer_id)
VALUES (NULL), (74);

INSERT INTO cart_item (cart_id, product_id, quantity)
VALUES
    ((SELECT max(cart_id) FROM cart WHERE customer_id IS NULL), 1, 2),
    ((SELECT max(cart_id) FROM cart WHERE customer_id IS NULL), 3, 1),
    ((SELECT cart_id FROM cart WHERE customer_id = 74), 2, 3),
    ((SELECT cart_id FROM cart WHERE customer_id = 74), 4, 1);

SELECT ci.cart_id, p.product_name, p.price, ci.quantity,
       p.price * ci.quantity AS amount
FROM cart_item ci
         JOIN product p ON p.product_id = ci.product_id
ORDER BY ci.cart_id, ci.cart_item_id;

