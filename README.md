# 일로ON (illo-on)

업무관리 플랫폼 + AI 회의록 서비스.
회의·메신저에서 논의된 내용을 AI로 분석해 Action Point를 추출하고, 사용자 검토를 거쳐 실제 Task·Sprint로 연결한다.

```text
회의/대화 텍스트·녹음본
      │  AI 분석 (OpenAI · 실패 시 규칙 기반 폴백)
      ▼
브리핑(요약·결정사항·Action Point)
      │  사용자 검토·수정
      ▼
Task ── Sprint / 담당자 / 마감일시
      ▲
      └── Task 상세에서 "관련 회의"로 역이동
```

## 저장소 구조

```text
illo-on/
├── front/      # Vue 3 + Vite 프론트엔드
├── backend/    # Spring Boot 3.4 (Java 21, Maven) 백엔드
├── docs/       # 기획서 / OpenAPI YAML / ERD·DBML / 개발 순서
└── docker-compose.yml
```

## 요구 사항

| | 버전 |
|---|---|
| Java | 21 |
| Node | 22.18+ 또는 24.12+ |
| (운영) MySQL | 8.x |
| (선택) Docker | Compose v2 |

개발 환경 DB는 H2 파일 모드(`backend/data/`)라 별도 설치가 필요 없다.

---

## 빠른 시작 (로컬)

### 1. 백엔드

```sh
cd backend
./mvnw spring-boot:run
```

- `http://localhost:8080` 기동, `dev` 프로파일 (H2 파일 DB, 스키마 자동 생성)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 콘솔: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/illo-on`, user `sa`, 비밀번호 없음)

### 2. 프론트엔드

```sh
cd front
npm install
npm run dev
```

- `http://localhost:5173`
- API 주소는 `front/.env` 의 `VITE_API_BASE_URL` (기본 `http://localhost:8080/api`)

### 3. 첫 사용

회원가입 → 로그인 → 프로젝트 생성 → 초대 링크로 팀원 합류 → 회의 등록 → **AI 분석하기** → Action Point를 업무로 등록.

---

## AI 분석 설정

기본값은 **`mock`** — OpenAI 키 없이 규칙 기반으로 브리핑을 생성한다(데모 가능, 품질은 제한적).

실제 OpenAI 분석을 켜려면:

```sh
cd backend
cp config/local.properties.example config/local.properties
# config/local.properties 를 열어 아래를 채운다
#   app.ai.provider=openai
#   spring.ai.openai.api-key=sk-...
```

- `config/local.properties` 는 `.gitignore` 에 포함 — **키는 절대 커밋되지 않는다.**
- 저장 후 백엔드 재시작.
- OpenAI 호출이 실패하면(쿼터 초과·네트워크 장애 등) 자동으로 규칙 기반 분석으로 **degrade** 되고, 요청은 여전히 200으로 응답한다. (로그: `OpenAI analyze failed, falling back...`)
- 환경변수로도 주입 가능: `AI_PROVIDER`, `OPENAI_API_KEY`, `OPENAI_CHAT_MODEL`, `OPENAI_STT_MODEL`.

---

## 프로필 / 환경변수

| 변수 | 기본값 | 용도 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev`(H2) / `prod`(MySQL) |
| `JWT_SECRET` | dev 더미 | 운영에서 반드시 교체 (긴 랜덤 문자열) |
| `JWT_EXPIRATION_MS` | `86400000` | 토큰 유효기간 (24h) |
| `AI_PROVIDER` | `mock` | `mock` / `openai` |
| `OPENAI_API_KEY` | `not-configured` | provider=openai 일 때 필요 |
| `INVITE_BASE_URL` | `http://localhost:5173/invite/` | 초대 링크 접두 |
| `DB_HOST`·`DB_PORT`·`DB_NAME`·`DB_USER`·`DB_PASSWORD` | localhost:3306 / illo_on / illoon | `prod` MySQL 접속 |

운영(`prod`)은 `ddl-auto: validate` — 스키마를 미리 만들어야 한다. `docs/일로ON_ERD.md` 의 DBML + 마이그레이션 순서 참고.

---

## 테스트

### 시나리오 E2E (A~D 전체 흐름)

백엔드가 떠 있는 상태에서:

```sh
bash backend/scripts/e2e.sh
```

회원가입 → 로그인 → 프로젝트·초대 → 회의 → AI 분석 → Action Point → Task → Sprint → 상태 변경 → 회의 역이동 → 메인보드 집계까지 27개 체크. `mock`·`openai` 어느 provider로도 통과한다(OpenAI 실패 시 폴백).

```
═══  PASS 27  /  FAIL 0  ═══
```

`BASE=http://다른호스트:포트 bash backend/scripts/e2e.sh` 로 대상 변경 가능.

### 프론트엔드 린트

```sh
cd front && npm run lint
```

---

## Docker

```sh
# OpenAI 분석을 쓰려면 먼저:  export OPENAI_API_KEY=sk-...   (없으면 mock 로 동작)
docker compose up --build
```

- `backend` : 멀티스테이지 빌드(Maven → JRE 21), `prod` 프로파일, `db` 의존
- `db` : MySQL 8, 볼륨 `illoon-db-data`
- `front` : Vite 빌드 → nginx, `http://localhost:5173`

`docker compose down -v` 로 DB 볼륨까지 정리.

---

## API / 데이터 모델 문서

| 문서 | 내용 |
|---|---|
| `docs/일로ON_openapi.yaml` | OpenAPI 3 스펙 (30개 엔드포인트). `curl http://localhost:8080/v3/api-docs.yaml` 로 재생성 |
| `docs/일로ON_ERD.md` | ERD + DBML + Enum 통일표 + 마이그레이션 순서 |
| `docs/일로ON_기획서_최종.md` | 서비스 기획서 |
| `docs/일로ON_개발순서.md` | Phase별 개발 로드맵 / 완료 기준 |
| `docs/일로ON_프로젝트_폴더구조.md` | 프론트엔드 폴더 구조 설계 |

## 기술 스택

- **Frontend** : Vue 3 (`<script setup>`) · Vite · Vue Router · Pinia · 순수 CSS 디자인 토큰 · axios
- **Backend** : Spring Boot 3.4 · Java 21 · Spring Security(JWT, stateless) · Spring Data JPA · Spring AI 1.0 (OpenAI) · springdoc-openapi
- **DB** : H2(dev) · MySQL(prod)
