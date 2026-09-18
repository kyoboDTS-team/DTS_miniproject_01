# 상품 CSV 저장 / 불러오기 사용 가이드

## 1. CSV 기능이란?

관리자가 상품 데이터를 **파일로 저장하거나 여러 상품을 한 번에 등록하기 위한 기능**입니다.

현재 프로젝트에서는 Excel 전용 파일인 `.xlsx`를 생성하는 것이 아니라  
**CSV(Comma-Separated Values) 형식의 `.csv` 파일**을 사용합니다.

CSV 파일은 일반 텍스트 파일이지만 Excel에서 바로 열 수 있기 때문에  
상품 목록을 표 형태로 확인하거나 수정하기 편합니다.

---

## 2. 주요 기능

관리자 메뉴의 아래 항목에서 사용합니다.

```text
========================================
             TERMINAL MARKET
========================================
1. 상품 / 카테고리 관리
2. 재고 / 시리얼 관리
3. 회원 관리
4. 주문 / 반품 관리
5. 통계
6. CSV 저장 / 불러오기
7. 로그아웃
0. 종료
----------------------------------------
선택 >
```

`6. CSV 저장 / 불러오기`를 선택하면 다음 메뉴가 표시됩니다.

```text
========================================
          CSV 저장 / 불러오기
========================================
1. 상품 CSV 저장
2. 상품 CSV 불러오기
0. 이전
----------------------------------------
선택 >
```

---

# 3. 상품 CSV 저장

## 기능

현재 DB의 `product` 테이블에 등록된 상품들을 CSV 파일로 저장합니다.

흐름은 다음과 같습니다.

```text
DB product 테이블
        ↓
상품 전체 조회
        ↓
CSV 형식으로 변환
        ↓
csv/products.csv 생성
```

---

## 사용 방법

관리자 메뉴에서:

```text
6. CSV 저장 / 불러오기
```

선택 후:

```text
1. 상품 CSV 저장
```

을 선택합니다.

예:

```text
파일명 (엔터: products.csv) > 
```

그냥 Enter를 누르면:

```text
products.csv
```

라는 이름으로 저장됩니다.

`backup`이라고 입력하면:

```text
backup.csv
```

로 저장됩니다.

---

## 저장 위치

현재 구현에서는 프로젝트 실행 위치를 기준으로 `csv` 폴더에 저장합니다.

예:

```text
프로젝트/
├─ src/
├─ pom.xml
└─ csv/
   └─ products.csv
```

`csv` 폴더가 없으면 자동으로 생성됩니다.

---

## 저장되는 컬럼

```csv
product_code,category_id,product_name,price,reorder_level,requires_serial
```

각 컬럼의 의미는 다음과 같습니다.

| 컬럼 | 설명 |
|---|---|
| `product_code` | 상품 코드 |
| `category_id` | 상품이 속한 카테고리 ID |
| `product_name` | 상품명 |
| `price` | 상품 가격 |
| `reorder_level` | 안전 재고 기준 |
| `requires_serial` | 시리얼 관리 여부 |

---

## 저장 예시

```csv
product_code,category_id,product_name,price,reorder_level,requires_serial
PEN-001,2,검정 볼펜,1000,10,false
NOTE-001,3,A4 노트,3500,5,false
PHONE-001,10,갤럭시 S26,1200000,2,true
```

이 파일은 Excel에서 열어서 확인할 수 있습니다.

---

# 4. 상품 CSV 불러오기

## 기능

CSV 파일에 작성된 여러 상품을 DB에 한 번에 등록합니다.

흐름은 다음과 같습니다.

```text
CSV 파일
   ↓
전체 행 읽기
   ↓
전체 데이터 검증
   ↓
모두 정상인가?
   ├─ YES → 상품 일괄 INSERT → commit
   └─ NO  → 전체 취소 → rollback
```

즉, CSV 안에 상품이 10개 있어도  
**하나라도 잘못되어 있으면 10개 모두 등록하지 않습니다.**

---

## 사용 방법

관리자 메뉴에서:

```text
6. CSV 저장 / 불러오기
```

선택 후:

```text
2. 상품 CSV 불러오기
```

를 선택합니다.

예:

```text
불러올 파일명 (엔터: products.csv) >
```

Enter를 누르면:

```text
csv/products.csv
```

파일을 읽습니다.

다른 파일을 사용하려면:

```text
new_products.csv
```

처럼 파일명을 입력합니다.

---

## 실행 확인

불러오기 전에 다음과 같은 확인 메시지가 표시됩니다.

```text
CSV의 모든 상품을 신규 등록합니다.
한 행이라도 오류가 있으면 전체 등록이 취소됩니다.

계속하시겠습니까? (Y/N) >
```

계속하려면:

```text
Y
```

를 입력합니다.

취소하려면:

```text
N
```

을 입력합니다.

---

# 5. CSV 작성 규칙

CSV 첫 번째 줄은 반드시 아래 헤더와 같아야 합니다.

```csv
product_code,category_id,product_name,price,reorder_level,requires_serial
```

컬럼 순서도 동일해야 합니다.

---

## 정상 예시

```csv
product_code,category_id,product_name,price,reorder_level,requires_serial
BOOK-001,5,자바 입문서,25000,3,false
MONITOR-001,8,27인치 모니터,250000,2,true
KEYBOARD-001,8,기계식 키보드,80000,5,false
```

---

# 6. CSV 불러오기 검증 항목

CSV 데이터를 DB에 넣기 전에 전체 내용을 검사합니다.

### 상품 코드

비어 있으면 안 됩니다.

```text
X
product_code = ""
```

또한 CSV 파일 안에서 중복되면 안 됩니다.

```text
TEST-001
TEST-001
```

DB에 이미 같은 상품 코드가 있어도 등록되지 않습니다.

---

### 카테고리

`category_id`는 실제 DB에 존재하는 카테고리여야 합니다.

예:

```text
category_id = 99999
```

DB에 99999번 카테고리가 없다면 전체 등록이 취소됩니다.

또한 현재 프로젝트에서는 상품을 **하위 카테고리**에 등록하도록 처리합니다.

---

### 상품명

상품명은 비어 있으면 안 됩니다.

---

### 가격

가격은 숫자여야 하며 `0` 이상이어야 합니다.

정상:

```text
1000
25000
0
```

오류:

```text
ABC
-1000
```

---

### 안전 재고

`reorder_level`은 `0` 이상의 정수여야 합니다.

정상:

```text
0
5
10
```

오류:

```text
-1
ABC
```

---

### 시리얼 관리 여부

다음 값만 사용할 수 있습니다.

```text
true
false
Y
N
```

예:

```csv
PHONE-001,10,스마트폰,1200000,3,true
```

---

# 7. CSV로 등록되는 상품의 기본값

CSV Import는 **신규 상품 등록** 용도로 사용합니다.

따라서 CSV에 아래 값은 직접 입력하지 않습니다.

```text
stock_quantity
sale_status
```

신규 상품은 자동으로:

```text
stock_quantity = 0
sale_status = SELLING
```

으로 등록됩니다.

---

# 8. 시리얼 상품 처리

`requires_serial=true` 상품도 CSV로 상품 자체는 등록할 수 있습니다.

예:

```csv
PHONE-001,10,갤럭시 S26,1200000,2,true
```

이 상품을 CSV로 등록하면:

```text
상품 등록 완료
stock_quantity = 0
requires_serial = true
```

상태가 됩니다.

실제 제품의 시리얼 번호는 CSV에서 등록하지 않습니다.

관리자 메뉴의:

```text
2. 재고 / 시리얼 관리
```

에서 별도로 등록합니다.

예:

```text
S26-0001
S26-0002
S26-0003
```

시리얼 3개를 등록하면:

```text
product_unit 3건 생성
stock_quantity = 3
```

형태로 관리합니다.

---

# 9. Excel에서 사용하는 방법

CSV는 Excel에서 바로 열 수 있습니다.

일반적인 사용 흐름은 다음과 같습니다.

```text
관리자
 ↓
상품 CSV 저장
 ↓
products.csv 생성
 ↓
Excel로 열기
 ↓
상품 데이터 확인
```

신규 상품을 여러 개 등록할 때는:

```text
Excel에서 상품 작성
 ↓
CSV 형식으로 저장
 ↓
프로젝트의 csv 폴더에 넣기
 ↓
상품 CSV 불러오기
 ↓
DB 일괄 등록
```

방식으로 사용할 수 있습니다.

> Excel에서 저장할 때는 가능하면 `CSV UTF-8` 형식을 사용합니다.

---

# 10. CSV 저장 기능의 활용 예

## 상품 데이터 백업

```text
DB 상품
 ↓
products.csv
```

현재 상품 목록을 파일로 보관할 수 있습니다.

---

## 상품 목록 전달

CSV 파일은 Excel에서 열 수 있으므로 관리자, 운영 담당자 등이 상품 목록을 확인하기 쉽습니다.

---

## 대량 상품 등록

상품 100개를 콘솔에서 하나씩 등록하는 대신:

```text
Excel / CSV 작성
 ↓
CSV Import
 ↓
100개 일괄 등록
```

방식으로 작업할 수 있습니다.

따라서 이 기능은 단순한 기획자용 기능보다는  
**관리자 또는 운영자가 상품 데이터를 편하게 관리하기 위한 기능**에 가깝습니다.

---

# 11. 테스트 방법

## 테스트 1 - CSV 저장

```text
관리자 메뉴
→ 6. CSV 저장 / 불러오기
→ 1. 상품 CSV 저장
```

파일명:

```text
test
```

입력 후:

```text
csv/test.csv
```

파일이 생성되면 성공입니다.

---

## 테스트 2 - 정상 불러오기

CSV:

```csv
product_code,category_id,product_name,price,reorder_level,requires_serial
CSV-TEST-001,2,CSV 테스트 상품1,1000,5,false
CSV-TEST-002,2,CSV 테스트 상품2,2000,5,false
```

불러오기 성공 시:

```text
CSV 상품 등록이 완료되었습니다.
등록 상품 수: 2개
```

가 출력되고 DB에 두 상품이 등록되어야 합니다.

---

## 테스트 3 - 상품 코드 중복

```csv
product_code,category_id,product_name,price,reorder_level,requires_serial
TEST-001,2,상품1,1000,5,false
TEST-001,2,상품2,2000,5,false
```

두 상품의 코드가 같으므로 전체 등록이 취소되어야 합니다.

```text
등록된 상품 수 = 0
```

---

## 테스트 4 - 존재하지 않는 카테고리

```csv
product_code,category_id,product_name,price,reorder_level,requires_serial
TEST-A,2,상품A,1000,5,false
TEST-B,99999,상품B,2000,5,false
TEST-C,2,상품C,3000,5,false
```

중간에 존재하지 않는 카테고리가 있으므로:

```text
TEST-A 등록 X
TEST-B 등록 X
TEST-C 등록 X
```

전부 등록되지 않아야 정상입니다.

---

# 12. 주의사항

- CSV 파일의 헤더 이름과 순서를 변경하지 않습니다.
- 상품 코드는 DB와 CSV 내부에서 중복되면 안 됩니다.
- 카테고리 ID는 실제 DB에 존재해야 합니다.
- 상품 재고를 CSV에서 직접 수정하지 않습니다.
- 시리얼 번호는 CSV가 아니라 재고 / 시리얼 관리 메뉴에서 등록합니다.
- CSV Import 중 하나라도 오류가 발생하면 전체 등록이 취소됩니다.
- Excel에서 수정 후 저장할 경우 `CSV UTF-8` 형식을 권장합니다.

---

# 13. 구현 클래스 역할

```text
ProductCsvMenu
    ↓
사용자 메뉴 입력 처리

ProductCsvService
    ↓
CSV 저장 / 불러오기 전체 흐름
검증
트랜잭션 관리

CsvExporter
    ↓
Product → CSV 파일 저장

CsvImporter
    ↓
CSV 파일 → Product 객체 변환

ProductDao / CategoryDao
    ↓
DB 조회 및 상품 등록
```

역할을 분리했기 때문에 메뉴에서 직접 파일 처리나 SQL 처리를 하지 않습니다.

---

# 14. 최종 정리

```text
[CSV 저장]

DB 상품 데이터
      ↓
CSV Export
      ↓
.csv 파일 생성
      ↓
Excel에서 확인 가능


[CSV 불러오기]

Excel 등에서 CSV 작성
      ↓
CSV Import
      ↓
전체 데이터 검증
      ↓
상품 일괄 등록
```

현재 프로젝트의 CSV 기능은 **상품 데이터를 파일로 내보내고, 여러 상품을 한 번에 등록하기 위한 관리자용 데이터 관리 기능**입니다.
