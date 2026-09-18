# Main 테스트 모드 사용 가이드

## 목적

로그인 기능이 아직 완성되지 않았거나 특정 메뉴를 바로 테스트하고 싶을 때  
`Main`에서 관리자 메뉴 또는 회원 메뉴로 바로 진입하기 위한 개발용 설정입니다.

---

## 테스트 모드 설정

`Main.java` 상단에 아래 두 값을 둡니다.

```java
private static final boolean ADMIN_TEST_MODE = false;
private static final boolean MEMBER_TEST_MODE = true;
```

### 관리자 메뉴 테스트

```java
private static final boolean ADMIN_TEST_MODE = true;
private static final boolean MEMBER_TEST_MODE = false;
```

실행 흐름:

```text
Main
 ↓
AdminMenu
```

---

### 회원 메뉴 테스트

```java
private static final boolean ADMIN_TEST_MODE = false;
private static final boolean MEMBER_TEST_MODE = true;
```

실행 흐름:

```text
Main
 ↓
MemberMenu
```

---

### 실제 프로그램 흐름 테스트

둘 다 `false`로 설정합니다.

```java
private static final boolean ADMIN_TEST_MODE = false;
private static final boolean MEMBER_TEST_MODE = false;
```

실행 흐름:

```text
Main
 ↓
GuestMenu
 ↓
로그인
 ↓
회원 / 관리자 메뉴
```

---

## 주의사항

두 테스트 모드를 동시에 `true`로 두지 않습니다.

```java
// 사용하지 않기
private static final boolean ADMIN_TEST_MODE = true;
private static final boolean MEMBER_TEST_MODE = true;
```

현재 코드에서는 관리자 테스트 모드를 먼저 검사하므로  
둘 다 `true`이면 `AdminMenu`가 실행됩니다.

---

## 테스트용 사용자 정보

관리자 테스트에서는 실제 DB에 존재하는 관리자 계정 정보를 사용하는 것이 좋습니다.

예:

```java
Long adminUserId = 1L;
String adminEmail = "admin@example.com";
```

회원 테스트에서는 화면 표시 및 회원 기능 테스트에 사용할 이메일을 지정합니다.

예:

```java
String memberEmail = "user@example.com";
```

회원 기능에서 `customerId` 또는 `userId`가 필요한 구조로 변경되면  
테스트용 회원 ID도 실제 DB 값으로 전달하도록 수정합니다.

---

## 팀원 공통 사용 방법

### 관리자 기능 작업 중

```java
ADMIN_TEST_MODE = true;
MEMBER_TEST_MODE = false;
```

### 회원 기능 작업 중

```java
ADMIN_TEST_MODE = false;
MEMBER_TEST_MODE = true;
```

### 전체 흐름 통합 테스트

```java
ADMIN_TEST_MODE = false;
MEMBER_TEST_MODE = false;
```

---

## 최종 배포 전

테스트 모드는 개발 편의를 위한 임시 기능입니다.

로그인 및 권한 분기가 완성된 뒤에는 두 값을 모두 `false`로 두거나  
테스트 모드 코드를 제거하고 실제 로그인 흐름만 사용합니다.
