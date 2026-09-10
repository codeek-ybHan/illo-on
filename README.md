# 일로ON (illo-on)

**업무관리 플랫폼 + AI 회의록 서비스.**
회의·메신저에서 논의된 내용을 AI로 분석해 Action Point를 추출하고, 사용자 검토를 거쳐 실제 Task·Sprint로 연결한다.
회의록을 "기록"하는 데서 끝내지 않고 **결정된 일을 업무 실행 흐름까지** 잇는 것이 핵심.

```text
회의 / 메신저 텍스트 · 회의 녹음본
      │  (녹음본은 STT → 텍스트)
      │  AI 분석  (OpenAI · 실패 시 규칙 기반으로 자동 degrade)
      ▼
브리핑 (한눈에 보기 · 결정사항 · Action Point)
      │  사용자 검토·수정  ← AI는 여기서 멈춘다 (자동 Task 생성 X)
      ▼
Task ── Sprint / 담당자 / 마감일시 / 우선순위 / 상태
      ▲
      └── Task 상세에서 "관련 회의"로 역이동 (업무 생성 맥락 추적)
```

핵심 워크플로우: **회의/메신저 → AI 분석 → Action Point → 검토 → Task → Sprint → 메인보드에서 실행·관리**

---

## 저장소 구조

```text
illo-on/
├── front/               # Vue 3 (script setup) + Vite 프론트엔드
├── backend/             # Spring Boot 3.4 (Java 21, Maven) 백엔드
│   ├── config/          #   local.properties (OpenAI 키 등, git 무시)
│   └── scripts/e2e.sh   #   시나리오 A~D E2E
├── docs/                # 기획서 · OpenAPI YAML · ERD/DBML · 개발 순서 · 발표 자료
├── docker-compose.yml   # db(MySQL) + backend(prod) + front(nginx)
├── deploy.sh            # 원격 서버 배포 (pull → build → 헬스체크)
└── .env.example         # 배포용 환경변수 템플릿 (cp .env.example .env)
```

---

## 요구 사항

| | 버전 |
|---|---|
| Java | 21 |
| Node | `^22.18.0 \|\| >=24.12.0` |
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

- `http://localhost:8080` 기동, `dev` 프로파일 (H2 파일 DB, `ddl-auto: update` 로 스키마 자동 생성)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 콘솔: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/illo-on`, user `sa`, 비밀번호 없음)

### 2. 프론트엔드

```sh
cd front
npm install
npm run dev
```

- `http://localhost:5173`
- API 주소는 `front/.env` 의 `VITE_API_BASE_URL` (기본 `http://localhost:8080/api`, dev 기본값은 커밋됨)
- CORS 허용 오리진: `http://localhost:5173`, `http://localhost:5174`

### 3. 첫 사용

회원가입 → 로그인 → 프로젝트 생성 → 초대 링크로 팀원 합류 → 회의 등록 → **AI 분석하기** → Action Point를 업무로 등록 → Sprint 배정.

---

## AI 분석 설정

기본값은 **`mock`** — OpenAI 키 없이 규칙 기반으로 브리핑을 생성한다(이름·날짜·우선순위 추출, `.txt` 는 mock STT가 그대로 읽음). 데모는 가능하지만 요약 품질은 제한적.

실제 OpenAI 분석(Structured Output + Whisper STT)을 켜려면:

```sh
cd backend
cp config/local.properties.example config/local.properties
# config/local.properties 를 열어 아래를 채운다
#   app.ai.provider=openai
#   spring.ai.openai.api-key=sk-...
```

- `config/local.properties` 는 `application.yml` 이 `optional:file:` 로 자동 로드하며 `.gitignore` 에 포함 — **키는 커밋되지 않는다.**
- 저장 후 백엔드 재시작.
- 환경변수로도 주입 가능: `AI_PROVIDER`, `OPENAI_API_KEY`, `OPENAI_CHAT_MODEL`(기본 `gpt-4o-mini`), `OPENAI_STT_MODEL`(기본 `whisper-1`).

### 장애 시 동작 (graceful degrade — 요청은 항상 200)

| 실패 지점 | 폴백 | 로그 |
|---|---|---|
| 회의 분석 (`OpenAiAnalyzer`) | 규칙 기반 분석(`MockAiAnalyzer`) | `OpenAI analyze failed, falling back to rule-based analyzer` |
| STT (`OpenAiSpeechToText`) | mock STT(`MockSpeechToText`) | `Whisper STT failed, falling back to mock` |
| AI 어시스턴트 대화 (`AiChatService`) | 안내 문구 응답 | `AI chat failed` |

관련 견고성 설정: 멀티파트 상한 **30MB**, 외부 HTTP `read-timeout` **120s** / `connect-timeout` 10s, Spring AI 재시도 **2회**(백오프 1s→10s).

---

## 프로필 / 환경변수

| 변수 | 기본값 | 용도 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev`(H2 파일) / `prod`(MySQL) |
| `JWT_SECRET` | dev 더미 | 운영에서 반드시 교체 — `openssl rand -base64 48` |
| `JWT_EXPIRATION_MS` | `86400000` | 토큰 유효기간 (24h) |
| `AI_PROVIDER` | `mock` | `mock` / `openai` |
| `OPENAI_API_KEY` | `not-configured` | `provider=openai` 일 때 필요 |
| `OPENAI_CHAT_MODEL` / `OPENAI_STT_MODEL` | `gpt-4o-mini` / `whisper-1` | OpenAI 모델 |
| `APP_BASE_URL` *(compose)* | `http://localhost:5173` | 서버 공개 주소. `INVITE_BASE_URL` 로 파생돼 **초대 링크에 박힘** |
| `INVITE_BASE_URL` | `http://localhost:5173/invite/` | 초대 링크 접두 (백엔드 직접 실행 시) |
| `DB_HOST`·`DB_PORT`·`DB_NAME`·`DB_USER`·`DB_PASSWORD` | `localhost`·`3306`·`illo_on`·`illoon`·`illoon` | `prod` MySQL 접속 |
| `DB_ROOT_PASSWORD` *(compose)* | `root` | MySQL root (compose db 컨테이너) |

`prod` 프로파일은 `ddl-auto: validate` — 스키마를 미리 만들어야 한다. `docs/일로ON_ERD.md` 의 DBML + 마이그레이션 순서 참고.
(단, `docker-compose.yml` 은 **데모 편의상** `--spring.jpa.hibernate.ddl-auto=update` 로 오버라이드해 첫 기동 시 스키마를 자동 생성한다.)

---

## 테스트

### 시나리오 E2E (A~D 전체 흐름)

백엔드가 떠 있는 상태에서:

```sh
bash backend/scripts/e2e.sh
```

- **Scenario A** 회원가입 → 로그인(JWT) → 프로젝트 → 초대 → 참여
- **Scenario B** 회의 → AI 분석 → Action Point → Task → Sprint
- **Scenario C** 팀원 → 내 Task → 상태 변경
- **Scenario D** Task → 관련 회의 역이동
- 메인보드 / 캘린더 집계

총 **27개 체크**. `mock`·`openai` 어느 provider로도 통과한다(OpenAI 실패 시 폴백).

```
═══  PASS 27  /  FAIL 0  ═══
```

`BASE=http://다른호스트:포트 bash backend/scripts/e2e.sh` 로 대상 변경 가능.

### 프론트엔드 린트

```sh
cd front && npm run lint     # oxlint → eslint 순차 실행
```

---

## Docker / 배포

```sh
cp .env.example .env      # APP_BASE_URL, JWT_SECRET, DB_PASSWORD 등을 채운다
./deploy.sh               # 빌드 + 기동 + 헬스체크. 이후 갱신도 ./deploy.sh
```

- `db` : MySQL 8, 볼륨 `illoon-db-data`, healthcheck 통과 후 backend 기동
- `backend` : 멀티스테이지 빌드(Maven → JRE 21), `prod` 프로파일, 포트 비공개(`expose: 8080`, nginx가 프록시)
- `front` : Vite 빌드 → nginx (`/` SPA + `/api` → `backend:8080` 프록시), 공개 포트 `5173:80`

`deploy.sh` 는 `.env` 검증(예시값 `JWT_SECRET` 거부, `localhost` `APP_BASE_URL` 경고) → `git pull --ff-only` → `docker compose up -d --build` → 헬스체크(front `200` + `/api/me/board` `401`) 순으로 동작.

옵션: `./deploy.sh --no-pull` (현재 코드로 빌드) · `--logs` (기동 후 로그) · `down` (스택 중지, DB 볼륨 유지).
외부 접속은 `.env` 의 `APP_BASE_URL`, 방화벽에서 `5173`(또는 `80`) 인바운드 허용. DB 볼륨까지 삭제하려면 `docker compose down -v`.
직접 실행: `docker compose --env-file .env up -d --build`.

---

## API 개요

JWT Bearer 인증(stateless). 총 **31개 엔드포인트**.

| 도메인 | 수 | 주요 엔드포인트 |
|---|---:|---|
| Auth | 2 | `POST /api/auth/signup` · `POST /api/auth/login` |
| Project | 5 | `GET·POST /api/projects` · `GET·PUT·DELETE /api/projects/{id}` |
| Member · Invite | 3 | `POST /api/projects/{id}/invites` · `GET /api/projects/{id}/members` · `POST /api/invites/{token}/join` |
| Task | 6 | `GET·POST /api/tasks` · `GET·PUT·DELETE /api/tasks/{id}` · `GET /api/me/tasks` |
| Sprint | 5 | `GET·POST /api/projects/{id}/sprints` · `GET·PUT·DELETE /api/sprints/{id}` |
| Meeting | 6 | `GET·POST /api/projects/{id}/meetings` · `GET·PUT·DELETE /api/meetings/{id}` · `GET /api/me/meetings` |
| AI | 3 | `POST /api/meetings/{id}/analyze` · `GET /api/meetings/{id}/summary` · `POST /api/ai/chat` |
| Board | 1 | `GET /api/me/board` |

- 상태 변경·Sprint 배정은 별도 API 없이 `PUT /api/tasks/{id}` 전체 교체로 처리.
- `POST /api/meetings/{id}/analyze` : `multipart/form-data` 의 `audio` 파일이 있으면 STT 후 분석, 없으면 저장된 회의 내용 분석.
- 에러 응답 규격 `{ code, message, timestamp }` — `401`(인증) `403`(`NOT_PROJECT_MEMBER`/`NOT_PROJECT_ADMIN`) `404` `400`(검증) `409`(중복/이미 참여) `410`(만료 초대).

---

## 문서

| 문서 | 내용 |
|---|---|
| `docs/일로ON_openapi.yaml` | OpenAPI 3 스펙 (31개). `curl http://localhost:8080/v3/api-docs.yaml` 로 재생성 |
| `docs/일로ON_ERD.md` | ERD + DBML + Enum 통일표 + 마이그레이션 순서 + 화면↔테이블 |
| `docs/일로ON.dbml` | DBML 독립 파일 (dbdiagram.io 붙여넣기 / 제출용) |
| `docs/일로ON_기획서_최종.md` | 서비스 기획서 |
| `docs/일로ON_개발순서.md` | Phase별 개발 로드맵 / 완료 기준 |
| `docs/일로ON_프로젝트_폴더구조.md` | 프론트엔드 폴더 구조 설계 |
| `docs/일로ON_발표_내용.md` · `docs/일로ON_발표_디자인프롬프트.md` | 발표 슬라이드 내용 / 디자인 프롬프트 |

---

## 기술 스택

- **Frontend** : Vue 3 (`<script setup>`) · Vite · Vue Router · Pinia · axios · 순수 CSS 디자인 토큰(`assets/styles/`) · oxlint + eslint + prettier
- **Backend** : Spring Boot 3.4 · Java 21 · Spring Security(JWT stateless, jjwt) · BCrypt · Spring Data JPA · Spring AI 1.0 (OpenAI Chat + Whisper) · springdoc-openapi
- **DB** : H2 파일(dev, `MODE=MySQL`) · MySQL 8(prod)
- **인프라** : Docker 멀티스테이지 · nginx 리버스 프록시 · docker compose
