# [산출물 03] TERMINAL MARKET 클래스구조도 및 아키텍처 정의서

---

## 1. 시스템 아키텍처 개요

터미널 마켓은 관심사의 분리(Separation of Concerns)와 코드의 유지보수성을 극대화하기 위해 **도메인 주도 4계층 레이어드 아키텍처(Layered Architecture)**를 채택했습니다.

```text
┌─────────────────────────────────────────────────────────────┐
│              Presentation Layer (화면 및 입출력)             │
│        *Menu, ConsoleUi, ConsoleInput, Main, Guest/Member   │
└──────────────────────────────┬──────────────────────────────┘
                               │ 사용자 입력 전달 / 뷰 모델 렌더링
┌──────────────────────────────▼──────────────────────────────┐
│             Business / Service Layer (업무 로직)            │
│  *Service, LoginSession, OrderNoGenerator, PasswordHasher   │
└──────────────────────────────┬──────────────────────────────┘
                               │ 비즈니스 검증 완료 후 SQL 실행 요청
┌──────────────────────────────▼──────────────────────────────┐
│            Persistence / DAO Layer (데이터 접근)            │
│       *Dao (MyBatis 매퍼 인터페이스) + XML 매퍼 파일       │
└──────────────────────────────┬──────────────────────────────┘
                               │ ORM 매핑 / SQL 실행
┌──────────────────────────────▼──────────────────────────────┐
│            Domain Model Layer & PostgreSQL Database         │
│  Entities, DTOs, Enums, PostgreSQL 16 (9개 관계형 테이블)   │
└─────────────────────────────────────────────────────────────┘
```

| 계층 (Layer) | 역할 및 책임 | 주요 특징 |
| :--- | :--- | :--- |
| **Presentation** | 사용자 콘솔 메뉴 출력, 사용자 번호 입력 파싱, 예외 메시지 렌더링 | 비즈니스 로직을 일체 포함하지 않고 Service 계층에 작업 위임 |
| **Business** | 비즈니스 규칙 검증, 권한 검사, 재고 수량 계산, 트랜잭션(`commit/rollback`) 제어 | UI 환경(콘솔/웹)에 독립적으로 순수 자바 코드로 동작 |
| **Persistence** | MyBatis `SqlSession`을 활용하여 SQL 질의 실행 및 데이터 객체 매핑 | 비즈니스 예외를 던지지 않고 순수 CRUD 작업만 수행 |
| **Domain Model** | 데이터베이스 테이블과 1:1 또는 1:N으로 대응되는 엔티티 및 상태 Enum 객체 | 롬복(`@Getter`, `@Builder` 등)을 통한 간결한 데이터 홀더 |

---

## 2. 패키지 및 전체 클래스 구조도

```text
com.team.orderapp
│
├── app                          # 애플리케이션 진입점 및 권한별 메인 메뉴
│   ├── Main.java                # 프로그램 진입점 (단일 Scanner 생성 및 DI 주입)
│   ├── GuestMenu.java           # 비회원 전용 메인 메뉴
│   ├── MemberMenu.java          # 로그인 회원 전용 메인 메뉴
│   └── AdminMenu.java           # 최고 관리자 전용 대시보드 메뉴
│
├── common                       # 시스템 공통 유틸리티 및 인프라
│   ├── DbConnectionFactory.java # [팩토리 패턴] MyBatis SqlSession 생성 팩토리
│   ├── ConsoleUi.java           # 콘솔 박스, 헤더, 공통 스타일 출력 유틸리티
│   ├── ConsoleInput.java        # Scanner 기반 안전한 입력 검증 헬퍼
│   ├── OrderNoGenerator.java    # 외부 노출용 난수 주문번호 생성기
│   └── BusinessException.java   # 업무 규칙 위반 시 발생하는 표준 런타임 예외
│
├── auth                         # 계정 인증 및 세션 관리
│   ├── AppUser.java             # 계정 엔티티 (user_id, email, password_hash, role)
│   ├── UserRole.java            # 권한 열거형 (ADMIN, CUSTOMER)
│   ├── LoginSession.java        # [싱글톤] 현재 로그인 사용자 세션 상태 홀더
│   ├── LoginMenu.java           # 로그인 화면
│   ├── SignupMenu.java          # 회원가입 화면
│   ├── AuthService.java         # 회원가입 및 로그인 검증 서비스
│   ├── AppUserDao.java          # app_user 테이블 매퍼 인터페이스
│   └── PasswordHasher.java      # SHA-256 단방향 암호화 해시 유틸리티
│
├── customer                     # 회원 프로필 및 마이페이지
│   ├── Customer.java            # 회원 프로필 엔티티 (customer_id, user_id, name, phone)
│   ├── CustomerMenu.java        # 회원 관리 메뉴 (관리자용)
│   ├── MyInfoMenu.java          # 마이페이지 프로필 조회 및 수정 화면
│   ├── CustomerService.java     # 회원 정보 조회 및 수정 비즈니스 로직
│   └── CustomerDao.java         # customer 테이블 매퍼 인터페이스
│
├── product                      # 상품 및 계층형 카테고리
│   ├── Product.java             # 상품 마스터 엔티티
│   ├── Category.java            # 계층형 카테고리 엔티티 (Self-referencing)
│   ├── ProductUnit.java         # 단품 고유 시리얼 엔티티
│   ├── ProductMenu.java         # 상품 메인 탐색 메뉴
│   ├── ProductListMenu.java     # 상품 전체 목록 페이징 화면
│   ├── ProductDetailMenu.java   # 상품 상세 조회 및 장바구니 담기 화면
│   ├── ProductConditionMenu.java# 카테고리/가격 조건 검색 화면
│   ├── ProductCommandMenu.java  # 관리자 상품 등록/수정 화면
│   ├── CategoryMenu.java        # 관리자 카테고리 관리 화면
│   ├── ProductService.java      # 상품 비즈니스 로직
│   ├── CategoryService.java     # 카테고리 비즈니스 로직
│   ├── ProductDao.java          # product 테이블 매퍼 인터페이스
│   ├── ProductUnitDao.java      # product_unit 테이블 매퍼 인터페이스
│   └── CategoryDao.java         # category 테이블 매퍼 인터페이스
│
├── cart                         # 세션 기반 장바구니
│   ├── Cart.java                # 장바구니 엔티티
│   ├── CartItem.java            # 장바구니 개별 품목 (product, quantity)
│   ├── CartMenu.java            # 장바구니 콘솔 UI
│   ├── CartService.java         # 사용자별 장바구니 관리 서비스
│   └── CartDao.java             # 장바구니 영구 저장 매퍼
│
├── order                        # 주문 및 반품 트랜잭션 (CQRS 분리)
│   ├── model/                   # 주문 도메인 모델
│   │   ├── Order.java           # 주문 헤더 엔티티
│   │   ├── OrderItem.java       # 주문 상세 품목 엔티티
│   │   ├── OrderItemUnit.java   # 주문 출고 시리얼 매핑 엔티티
│   │   └── OrderStatus.java     # 주문 상태 (CONFIRMED, RETURNED)
│   ├── command/                 # 주문 생성 및 반품 (상태 변경 로직)
│   │   ├── OrderCommandMenu.java    # 주문서 작성 및 결제 화면
│   │   ├── OrderCommandService.java # 단일 트랜잭션 주문/반품 핵심 서비스
│   │   └── OrderCommandDao.java     # 주문 CUD 매퍼 인터페이스
│   └── query/                   # 주문 내역 조회 (조회 전용 로직)
│       ├── OrderQueryMenu.java      # 회원/비회원 주문 내역 조회 메뉴
│       ├── OrderAdminMenu.java      # 관리자 전체 주문 조회 메뉴
│       ├── OrderQueryService.java   # 주문 조회 전용 서비스
│       ├── OrderQueryDao.java       # 주문 R(조회) 매퍼 인터페이스
│       ├── OrderDetailView.java     # 주문 상세 뷰 모델
│       └── OrderSummaryView.java    # 주문 요약 목록 뷰 모델
│
├── stock                        # 재고 입출고 및 감사 이력
│   ├── StockAdjustment.java     # 재고 변경 이력 엔티티
│   ├── StockAdjustmentHistory.java # 이력 조회용 DTO
│   ├── SerialStockConsistency.java # 재고 정합성 자동 검증기
│   ├── StockMenu.java           # 관리자 재고 입고 및 시리얼 관리 화면
│   ├── StockService.java        # 재고 수량 조정 및 감사 로그 기록 서비스
│   └── StockAdjustmentDao.java  # stock_adjustment 테이블 매퍼 인터페이스
│
├── report                       # 통계 및 대시보드
│   ├── AdminDashboardStat.java  # 관리자 대시보드 종합 통계 DTO
│   ├── DailySalesStat.java      # 일별 매출 통계 DTO
│   ├── ProductSalesStat.java    # 상품별 판매 랭킹 DTO
│   ├── ReportMenu.java          # 통계 화면 콘솔 UI
│   ├── ReportService.java       # 집계 쿼리 실행 서비스
│   └── ReportDao.java           # report 매퍼 인터페이스
│
└── export                       # 대용량 CSV 임포트 / 익스포트
    ├── CsvExporter.java         # CSV 파일 생성기
    ├── CsvImporter.java         # CSV 파일 파서
    ├── ProductCsvMenu.java      # CSV 입출력 관리 메뉴
    ├── ProductCsvService.java   # CSV 비즈니스 서비스
    └── SerialCsvRow.java        # 시리얼 CSV 행 매핑 DTO
```

---

## 3. 핵심 도메인 간 호출 흐름 (Sequence & Collaboration)

### 3.1 장바구니 주문 결제 트랜잭션 시퀀스 (Order Checkout Flow)

```text
[CartMenu]          [OrderCommandService]     [ProductDao]      [ProductUnitDao]    [OrderCommandDao]
    │                         │                     │                   │                   │
    │── 1. checkout(cart) ───>│                     │                   │                   │
    │                         │── 2. 재고 확인 ────>│                   │                   │
    │                         │<── 재고 수량 반환 ──│                   │                   │
    │                         │                                         │                   │
    │                         │── 3. AVAILABLE 시리얼 조회 (단품 관리 상품인 경우) ────────>│
    │                         │<── 시리얼 목록 반환 ────────────────────────────────────────│
    │                         │                                         │                   │
    │                         │── 4. 재고 수량 차감(UPDATE) ───────────>│                   │
    │                         │── 5. 시리얼 상태 변경(AVAILABLE -> SOLD)───────────────────>│
    │                         │                                                             │
    │                         │── 6. orders 헤더 INSERT ───────────────────────────────────>│
    │                         │── 7. order_item 상세 INSERT ───────────────────────────────>│
    │                         │── 8. order_item_unit 시리얼 맵핑 INSERT ───────────────────>│
    │                         │                                                             │
    │                         │── 9. session.commit() [모든 작업 성공 시 영구 반영] ────────>│
    │<── 주문 완료 반환 ──────│                                                             │
```

### 3.2 주문 반품 및 재고 환원 흐름 (Order Return Flow)
1. `OrderCommandService.returnOrder(orderId)` 호출.
2. 주문 상태 검증 (`status == OrderStatus.CONFIRMED` 여부 확인, 이미 `RETURNED`인 경우 `BusinessException` 발생).
3. 주문 상태를 `RETURNED`로 UPDATE 및 `returned_at` 타임스탬프 기록.
4. 해당 주문의 `order_item`을 순회하며 상품 재고 수량 복구 (`stock_quantity + quantity`).
5. 해당 주문에 맵핑되었던 `order_item_unit`의 시리얼들을 조회하여 `product_unit.unit_status = 'AVAILABLE'`로 일괄 원복.
6. 단일 트랜잭션 `commit()` 실행.

---

## 4. 적용 디자인 패턴 상세 분석

### 4.1 팩토리 패턴 (Factory Pattern) - `DbConnectionFactory`
* **적용 목적**: 데이터베이스 연결 생성 책임의 중앙 집중화 및 정보 은닉.
* **구현 방식**:
  * 외부 클래스는 `new SqlSessionFactoryBuilder()`나 복잡한 프로퍼티 파일 로딩 방식을 알 필요가 없습니다.
  * `DbConnectionFactory.OpenSession()` 정적 팩토리 메서드를 호출하기만 하면, 초기화된 `SqlSession` 객체를 안전하게 공급받을 수 있도록 설계했습니다.
* **장점**: DB 설정이나 커넥션 풀 라이브러리가 변경되어도 비즈니스 서비스 코드는 단 한 줄도 수정할 필요가 없습니다.

### 4.2 싱글톤 패턴 변형 (Singleton Pattern) - `LoginSession`
* **적용 목적**: 프로그램 실행 중 전역에서 단 하나의 사용자 인증 상태 유지.
* **구현 방식**:
  * `private LoginSession() {}` 생성자를 통해 외부에서의 무분별한 인스턴스 생성을 원천 차단했습니다.
  * 정적 메모리 공간에 `userId`, `email`, `role`, `customerId`를 보관하며, `Login()`, `Logout()`, `IsAdmin()` 등의 정적 메서드를 통해 세션 상태에 안전하게 접근하도록 보장했습니다.

### 4.3 의존성 주입 (Dependency Injection / DI) - `Scanner` 및 `Service`
* **적용 목적**: 자바 표준 입력 스트림(`System.in`)의 단절 방지 및 결합도 완화.
* **구현 방식**:
  * 각 메뉴가 독자적으로 `new Scanner(System.in)`을 만들고 `close()`하던 구조를 탈피하여, `Main` 클래스에서 단 하나의 `Scanner`를 생성한 뒤 모든 하위 메뉴 클래스의 생성자로 주입(DI)했습니다.
