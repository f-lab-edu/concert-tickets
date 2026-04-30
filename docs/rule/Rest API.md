# RESTful API 프로젝트 규칙

---

## 1. 기본 원칙

모든 API는 다음 원칙을 준수해야 합니다.

- **플랫폼 독립성**: 표준 HTTP 프로토콜 사용, JSON을 기본 데이터 형식으로 채택
- **느슨한 결합**: 클라이언트와 서버는 독립적으로 발전 가능해야 함
- **상태 비저장(Stateless)**: 모든 요청은 독립적이며, 서버는 클라이언트 세션 상태를 저장하지 않음
- **리소스 중심 설계**: 동사가 아닌 명사(리소스)를 중심으로 API 설계
- **균일한 인터페이스**: 표준 HTTP 메서드(GET, POST, PUT, PATCH, DELETE) 사용

---

## 2. URI 설계 규칙

### 2.1 명명 규칙

| 규칙 | 권장 (Good) | 비권장 (Avoid) |
|------|------------|---------------|
| 명사 사용 | `/orders` | `/create-order`, `/getOrder` |
| 복수형 사용 | `/customers`, `/products` | `/customer`, `/product` |
| 소문자 사용 | `/order-items` | `/OrderItems`, `/orderItems` |
| 단어 구분 | 하이픈(`-`) 사용 `/order-items` | 언더스코어(`_`) `/order_items` |
| 파일 확장자 | 사용하지 않음 `/orders/1` | `/orders/1.json` |

### 2.2 계층 구조

리소스 간 관계는 계층적으로 표현하되, **collection/item/collection** 패턴을 넘지 않습니다.

```
✅ 권장
GET /customers              # 고객 컬렉션
GET /customers/5            # 특정 고객
GET /customers/5/orders     # 특정 고객의 주문 목록

❌ 비권장 (너무 깊은 중첩)
GET /customers/1/orders/99/products/5/reviews
```

복잡한 관계는 단순화하여 별도 엔드포인트로 분리합니다.

```
GET /customers/1/orders     # 고객 1의 주문 조회
GET /orders/99/products     # 주문 99의 제품 조회
```

### 2.3 데이터베이스 구조 노출 금지

내부 DB 테이블 구조를 그대로 API로 노출하지 않습니다. API는 **비즈니스 엔터티의 추상화**여야 하며, DB 스키마 변경이 클라이언트에 영향을 주지 않도록 매핑 계층을 둡니다.

### 2.4 리소스 비대칭 방지

너무 많은 작은 리소스(chatty API)를 노출하지 않습니다. 클라이언트가 한 작업을 위해 여러 번 요청해야 한다면 리소스 통합을 고려합니다. 단, 과도한 비정규화로 불필요한 데이터를 가져오는 것도 피해야 합니다.

---

## 3. HTTP 메서드 사용 규칙

| 메서드 | 용도 | 멱등성(Idempotent) | 안전성(Safe) |
|--------|------|------------------|------------|
| GET | 리소스 조회 | ✅ | ✅ |
| POST | 리소스 생성, 비-CRUD 작업 | ❌ | ❌ |
| PUT | 리소스 전체 교체/생성 | ✅ | ❌ |
| PATCH | 리소스 부분 업데이트 | ❌ | ❌ |
| DELETE | 리소스 삭제 | ✅ | ❌ |

### 3.1 메서드별 동작 정의

`/customers` 컬렉션을 예시로 한 표준 동작입니다.

| Resource | POST | GET | PUT | DELETE |
|----------|------|-----|-----|--------|
| `/customers` | 신규 고객 생성 | 전체 고객 조회 | 대량 업데이트 | 전체 삭제 (주의) |
| `/customers/1` | 405 Error | 고객 1 조회 | 고객 1 교체 | 고객 1 삭제 |
| `/customers/1/orders` | 고객 1의 신규 주문 생성 | 고객 1의 주문 조회 | 고객 1 주문 대량 업데이트 | 고객 1 주문 전체 삭제 |

### 3.2 PUT vs PATCH

- **PUT**: 리소스 전체를 교체. 요청 본문에는 리소스의 **모든 필드**가 포함되어야 함
- **PATCH**: 변경된 필드만 전송. `application/merge-patch+json` 또는 `application/json-patch+json` 미디어 타입 사용

PATCH 예시 (Merge Patch):
```json
PATCH /products/10
Content-Type: application/merge-patch+json

{
    "price": 12,
    "color": null,
    "size": "small"
}
```

### 3.3 POST 시 URI 할당 규칙

- 클라이언트는 자체 URI를 만들지 않습니다.
- 서버가 새 리소스에 URI를 할당하고 응답의 `Location` 헤더에 포함합니다.

---

## 4. HTTP 상태 코드 규칙

### 4.1 GET

| 코드 | 의미 |
|------|------|
| 200 OK | 성공적으로 리소스 반환 |
| 204 No Content | 일치하는 리소스 없음 (본문 비어있음) |
| 404 Not Found | 리소스를 찾을 수 없음 |

### 4.2 POST

| 코드 | 의미 |
|------|------|
| 200 OK | 처리 성공 (새 리소스 생성 없음) |
| 201 Created | 리소스 생성 성공 (Location 헤더 필수) |
| 204 No Content | 성공했으나 본문 없음 |
| 400 Bad Request | 잘못된 요청 데이터 |
| 405 Method Not Allowed | 해당 URI에서 POST 미지원 |

### 4.3 PUT

| 코드 | 의미 |
|------|------|
| 200 OK | 업데이트 성공 |
| 201 Created | 신규 생성 성공 |
| 204 No Content | 성공했으나 본문 없음 |
| 409 Conflict | 현재 리소스 상태와 충돌 |

### 4.4 PATCH

| 코드 | 의미 |
|------|------|
| 200 OK | 업데이트 성공 |
| 400 Bad Request | 잘못된 패치 문서 형식 |
| 409 Conflict | 패치 적용 불가 |
| 415 Unsupported Media Type | 패치 문서 형식 미지원 |

### 4.5 DELETE

| 코드 | 의미 |
|------|------|
| 204 No Content | 삭제 성공 |
| 404 Not Found | 리소스 없음 |

---

## 5. 데이터 형식 (MIME Type)

### 5.1 표준 형식

- **기본 형식**: `application/json` (UTF-8)
- 요청과 응답 모두 `Content-Type` 헤더 명시
- 클라이언트는 `Accept` 헤더로 응답 형식 지정 가능

### 5.2 형식 미지원 시 응답

- 서버가 `Content-Type` 미지원 → `415 Unsupported Media Type`
- 서버가 `Accept` 헤더의 형식 제공 불가 → `406 Not Acceptable`

### 5.3 요청 예시

```http
POST /orders HTTP/1.1
Content-Type: application/json; charset=utf-8
Accept: application/json

{"productId": 1, "quantity": 2}
```

---

## 6. 페이지네이션, 필터링, 정렬

### 6.1 페이지네이션

쿼리 파라미터 `limit`과 `offset`을 사용합니다.

```
GET /orders?limit=25&offset=50
```

- `limit` 기본값: 25
- `offset` 기본값: 0
- **`limit` 최대값 제한 필수** (DoS 방지). 예: 최대 100개

### 6.2 필터링

쿼리 문자열로 조건 전달:

```
GET /orders?minCost=100&status=shipped
```

### 6.3 정렬

`sort` 파라미터 사용. 내림차순은 `-` 접두사:

```
GET /orders?sort=-createdAt,price
```

> ⚠️ 정렬/필터/페이징 파라미터는 캐싱에 영향을 줄 수 있으므로 캐시 키 전략 수립 필요

### 6.4 필드 선택 (Projection)

클라이언트가 필요한 필드만 받을 수 있도록 지원합니다.

```
GET /orders?fields=id,productId,quantity
```

서버는 요청된 필드를 검증하여 허용되지 않은 필드 노출을 방지해야 합니다.

---

## 7. 부분 응답 지원 (대용량 리소스)

이미지, 파일 등 대용량 바이너리 리소스는 부분 검색을 지원합니다.

```http
HEAD /products/10?fields=productImage HTTP/1.1

HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 4580

GET /products/10?fields=productImage HTTP/1.1
Range: bytes=0-2499

HTTP/1.1 206 Partial Content
Content-Range: bytes 0-2499/4580
Content-Length: 2500
```

---

## 8. 비동기 처리

오래 걸리는 작업은 비동기로 처리합니다.

### 8.1 요청 수락

```http
HTTP/1.1 202 Accepted
Location: /api/status/12345
```

### 8.2 상태 폴링

```http
GET /api/status/12345

HTTP/1.1 200 OK
Content-Type: application/json

{
    "status": "In progress",
    "link": { "rel": "cancel", "method": "delete", "href": "/api/status/12345" }
}
```

### 8.3 작업 완료

```http
HTTP/1.1 303 See Other
Location: /api/orders/12345
```

---

## 9. API 버전 관리

### 9.1 채택 방식: **URI 버전 관리**

가장 명확하고 캐싱에 친화적이므로 본 프로젝트는 **URI 버전 관리**를 채택합니다.

```
https://api.contoso.com/v1/customers/3
https://api.contoso.com/v2/customers/3
```

### 9.2 버전 변경 기준

- **Major 버전 업(v1 → v2)**: 호환성을 깨는 변경 (필드 삭제, 이름 변경, 관계 변경)
- **버전 변경 없음**: 새 필드 추가, 새 엔드포인트 추가 (하위 호환 유지)

### 9.3 구버전 지원 정책

- 신규 버전 출시 후 구버전은 **최소 6개월** 이상 유지
- Deprecation 시 응답 헤더에 `Deprecation: true`와 `Sunset` 헤더로 종료일 안내

```http
Deprecation: true
Sunset: Sat, 31 Dec 2026 23:59:59 GMT
```

### 9.4 다른 버전 관리 옵션 (참고)

- 쿼리 문자열: `?version=2` (캐싱 영향 가능)
- 커스텀 헤더: `Custom-Header: api-version=2`
- 미디어 타입: `Accept: application/vnd.contoso.v1+json`

---

## 10. HATEOAS (선택적)

리소스 응답에 관련 작업 링크를 포함하여 클라이언트가 API를 탐색할 수 있도록 합니다.

```json
{
  "orderID": 3,
  "productID": 2,
  "quantity": 4,
  "orderValue": 16.60,
  "links": [
    {
      "rel": "customer",
      "href": "https://api.contoso.com/customers/3",
      "action": "GET",
      "types": ["application/json"]
    },
    {
      "rel": "self",
      "href": "https://api.contoso.com/orders/3",
      "action": "GET",
      "types": ["application/json"]
    }
  ]
}
```

---

## 11. 멀티 테넌트 (필요 시)

본 프로젝트가 멀티 테넌트 SaaS인 경우 다음 중 하나를 채택합니다.

| 방식 | 예시 | 장점 | 단점 |
|------|------|------|------|
| 서브도메인 | `https://acme.api.contoso.com/orders` | 격리 명확, 브랜딩 | DNS/인증서 관리 복잡 |
| 헤더 | `X-Tenant-ID: acme` | 깔끔한 URI | 캐싱 복잡, L7 처리 필요 |
| URI 경로 | `/tenants/acme/orders` | 단순 구현 | RESTful 위배 가능 |

**JWT 클레임에서 테넌트 추출 방식이 가장 안전하고 확장성 있는 방식이므로 권장합니다.**

---

## 12. 분산 추적 및 관찰성

모든 API는 추적 컨텍스트 헤더를 전파해야 합니다.

| 헤더 | 용도 |
|------|------|
| `Correlation-ID` 또는 `X-Request-ID` | 요청 단위 추적 ID |
| `X-Trace-ID` | 분산 트레이싱 ID |
| `traceparent` (W3C 표준) | W3C Trace Context 표준 |

```http
GET /orders/3
Correlation-ID: aaaa0000-bb11-2222-33cc-444444dddddd

HTTP/1.1 200 OK
Correlation-ID: aaaa0000-bb11-2222-33cc-444444dddddd
```

---

## 13. 에러 응답 형식

[RFC 7807 Problem Details](https://datatracker.ietf.org/doc/html/rfc7807) 표준을 따릅니다.

```http
HTTP/1.1 400 Bad Request
Content-Type: application/problem+json

{
  "type": "https://api.contoso.com/errors/validation",
  "title": "유효성 검증 실패",
  "status": 400,
  "detail": "quantity 필드는 1 이상이어야 합니다.",
  "instance": "/orders/3",
  "errors": [
    { "field": "quantity", "message": "1 이상이어야 합니다." }
  ]
}
```

- 에러 메시지는 디버깅 정보를 노출하지 않습니다 (스택 트레이스, SQL 쿼리 등 금지)
- 사용자에게 표시 가능한 메시지는 `title`과 `detail`에 작성

---

## 14. 보안 규칙

- **모든 API는 HTTPS 필수** (HTTP 요청은 301로 리다이렉트하지 않고 차단)
- 인증: `Authorization: Bearer <JWT>` 표준 사용
- CORS 정책 명시적 설정
- Rate Limiting 적용 (헤더로 알림)

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 850
X-RateLimit-Reset: 1735689600
```

- 429 응답 시 `Retry-After` 헤더 포함

---

## 15. 문서화

### 15.1 OpenAPI(Swagger) 사용

- 모든 API는 [OpenAPI 3.x 스펙](https://www.openapis.org)으로 문서화합니다.
- **계약 우선(Contract-First) 접근**: 코드보다 OpenAPI 스펙을 먼저 작성합니다.
- API 변경은 OpenAPI 스펙 변경 PR로 시작합니다.

### 15.2 필수 문서 항목

- 엔드포인트 설명, 요청/응답 스키마
- 모든 가능한 상태 코드와 에러 응답
- 인증 방식 명시
- 예제 요청/응답 포함
