# 일로ON 개발 순서

> 전체 UI 틀만 잡힌 상태(스캐폴드 완료)에서 시작하는 기능 개발 로드맵.
> 원칙: **각 Phase = 세로 슬라이스** — 한 기능을 BE API(또는 목) → FE `api/` → store → 화면까지 관통해서 끝낸다. "화면 9개 먼저 만들고 나중에 API 연결"은 금지(통합 빅뱅 방지).

---

## 0. 공통 기반

### 이미 완료 (스캐폴드)

- [x] Vue 프로젝트 · Git/GitHub 초기 설정 · 폴더 구조
- [x] `reset.css` / `main.css` 디자인 토큰
- [x] 앱 셸: `AppRail`(아이콘 레일) · `AppHeader` · `AiAssistantPanel` · `DefaultLayout` / `AuthLayout`
- [x] 공통 컴포넌트: `BaseCard` · `BaseButton` · `ProgressBar` · `StatCard` · `SummaryChip` · `PagePlaceholder` · `AppIcon`
- [x] 9개 화면 라우팅 + 플레이스홀더

### 남은 것 — ✅ 완료 (2026-09-09)

- [x] 공통 컴포넌트: `BaseInput` · `BaseModal` · `BaseSelect`
- [x] `api/axios.js` — baseURL(`VITE_API_BASE_URL`), JWT Authorization 인터셉터, 401 → 로그인 리다이렉트, 에러 메시지 정규화
- [x] `.env` / `.env.example`
- [x] Pinia 스토어 골격: `stores/auth.js`(토큰·유저·`isAuthenticated`·`setAuth`·`logout`) · `project.js` · `task.js`
- [x] Router Guard — `public` 라우트 외 토큰 없으면 `/login`(redirect 쿼리 보관), `utils/token.js`
- [x] `utils/date.js`(ISO 포맷·D-day) · `utils/validation.js`
- [x] **ERD / DBML** — `docs/일로ON_ERD.md` (DBML + Enum 통일표 + 마이그레이션 순서 + 화면↔테이블)
- [x] BE 셋업 (`backend/`): Spring Boot 3.4 (Maven, Java 21), H2(dev)/MySQL(prod) 프로파일, JWT(`JwtTokenProvider`/`JwtAuthenticationFilter`), `SecurityConfig`(stateless·CORS·BCrypt), `GlobalExceptionHandler` + `ErrorCode`, `BaseTimeEntity`, Swagger(`/swagger-ui.html`) — 부팅 확인 완료

---

## 1. 인증 — Signup → Login → JWT ✅ (2026-09-09)

- [x] 회원가입 UI (`SignupView`) — 이름·이메일·비밀번호, 클라이언트 검증, 에러 표시
- [x] 로그인 UI (`LoginView`) — `redirect` 쿼리 복귀, 가입 완료 안내
- [x] `POST /api/auth/signup` — `User`(table `users`) · `UserRepository` · 이메일 중복 409 · BCrypt 저장 · 201 반환
- [x] `POST /api/auth/login` — 비밀번호 검증 · `JwtTokenProvider` 토큰 발급 · `{ token, user }`
- [x] JWT + user localStorage 저장 (`utils/token.js`), axios Authorization 헤더 (Phase 0)
- [x] `stores/auth.js` — `login` / `signup` / `logout` 액션, 새로고침 후 상태 복원
- [x] Router Guard (Phase 0) + `AppRail` 계정 팝오버 로그아웃
- [x] `GlobalExceptionHandler` 보강 — 404(NoResourceFound) · 400(malformed body)

```text
Signup → Login → JWT → 인증 상태 관리
```

**완료 기준**: 회원가입 → 로그인 → 메인보드 진입, 새로고침해도 로그인 유지, 만료/무효 토큰이면 로그인 화면으로 — curl E2E 검증 완료 (signup 201 / dup 409 / 검증 400, login 200+JWT / 실패 401, 보호 라우트 401→토큰시 통과, CORS :5173).

**API 검증 방법**: `cd backend && ./mvnw spring-boot:run` → Swagger `http://localhost:8080/swagger-ui.html` 에서 Auth 태그.

---

## 2. 프로젝트 — 업무의 기본 단위 ✅ (2026-09-09)

- [x] 프로젝트 목록 / 생성 / 상세 / 수정(PUT 전체교체) / 삭제
- [x] 프로젝트 멤버 조회 (ADMIN 먼저 정렬)
- [x] 초대 링크 생성 (7일 유효, ADMIN) · 클립보드 복사
- [x] 초대 링크로 프로젝트 참여 — 미로그인 시 가드가 `redirect` 보관 후 로그인→복귀

```http
GET    /api/projects            내가 멤버인 프로젝트 (myRole·memberCount 포함)
POST   /api/projects            생성자 = ADMIN 멤버, Team 자동 생성
GET    /api/projects/{id}        멤버만 (403 NOT_PROJECT_MEMBER)
PUT    /api/projects/{id}        ADMIN만 (403 NOT_PROJECT_ADMIN)
DELETE /api/projects/{id}        ADMIN만, 멤버십 정리 후 삭제
POST   /api/projects/{id}/invites   ADMIN만 → { token, inviteUrl, expiresAt }
GET    /api/projects/{id}/members
POST   /api/invites/{token}/join    만료 410 / 이미 참여 409 / 없음 404
```

**BE**: `team/Team` · `project/domain`(`Project` `ProjectMember`(복합PK) `ProjectInvite` `ProjectStatus` `MemberRole`) · `ProjectService`(멤버/ADMIN 가드) · `ProjectController` · `InviteController`
**FE**: `api/project.js` · `stores/project.js` · `views/project/`(`ProjectListView` `ProjectDetailView` `InviteJoinView`) · `components/project/ProjectForm.vue` · `components/common/StatusBadge.vue` · 라우트 `/projects` `/invite/:token` · 레일 프로젝트 → `/projects`

**완료 기준 (시나리오 A)**: 프로젝트 생성 → 초대 링크 → 다른 계정 참여 → 멤버 목록 표시 — curl E2E 검증 완료 (create 201, 비멤버 조회 403, 비-ADMIN invite/update/delete 403, join 200 / 재참여 409 / 잘못된 토큰 404).

> ⚠️ 메인보드 "내 프로젝트" 섹션 실데이터 연동은 Phase 7로 이월 (지금은 `/projects` 전용 화면).

---

## 3. Task — 실제 업무 관리 ✅ (2026-09-09)

- [x] Task 목록 / 생성 / 상세 / 수정 / 삭제
- [x] 담당자 지정(프로젝트 멤버만 400 검증) · 마감일 · 우선순위 · 상태
- [x] 내 업무 목록 (`/api/me/tasks`, 마감일 오름차순)
- [x] 상태 변경 — 별도 API 없이 `PUT` 전체 교체 (TaskCard 셀렉트 · TaskDetail "다음 단계로")

```http
GET    /api/tasks?projectId=   프로젝트 멤버만
POST   /api/tasks              projectId 필수, meetingId/sprintId 선택
GET    /api/tasks/{taskId}
PUT    /api/tasks/{taskId}      전체 교체 (status·sprintId·meetingId 포함)
DELETE /api/tasks/{taskId}
GET    /api/me/tasks
```

**BE**: `task/domain`(`Task` `TaskStatus` `TaskPriority`) · `TaskRepository` · `TaskService`(ProjectService 멤버 가드 재사용, 담당자 검증, 배치 이름 해석) · `TaskController`
**FE**: `api/task.js` · `stores/task.js`(`toUpdatePayload` 헬퍼, `changeStatus`) · `components/task/`(`TaskCard` `TaskList` `TaskForm`) · ProjectDetail **Tasks 탭** 연동 · `TaskDetailView` 실데이터(정보·상태전이·관련 회의 자리·수정/삭제) · MainBoard **"내가 해야 할 Task"** 실데이터 + 인사말에 사용자 이름

**완료 기준 (시나리오 C)**: 로그인 → 메인보드에서 내 Task 확인 → 상태 변경 반영 — curl E2E 검증 완료 (create 201 / 비멤버 담당자 400 / 비멤버 목록 403 / me·tasks 스코프 / status PUT / 404 / delete 204).

> Meeting/AI보다 Task를 먼저 만드는 이유: AI의 "업무로 등록" 단계가 Task CRUD에 의존한다.
> Task를 먼저 세워두면 이후 AI 단계가 그 자리에서 끝까지 검증된다.
> ⚠️ 메인보드 스탯카드·프로젝트·일정 섹션은 여전히 placeholder (Phase 7).

> Meeting/AI보다 Task를 먼저 만드는 이유: AI의 "업무로 등록" 단계가 Task CRUD에 의존한다.
> Task를 먼저 세워두면 이후 AI 단계가 그 자리에서 끝까지 검증된다.

---

## 4. Sprint ✅ (2026-09-09)

- [x] Sprint 목록 / 생성 / 상세 / 수정 / 삭제 (생성·수정·삭제 = ADMIN)
- [x] Task → Sprint 배정 — `PUT /api/tasks/{id}` `{ sprintId }` (별도 API 없음, 27개 유지)
- [x] Sprint 진행률 (doneCount/taskCount) · `GET /api/tasks?projectId=&sprintId=` 필터
- [x] Sprint 삭제 시 배정 Task는 유지, `sprintId` 만 해제

```http
GET    /api/projects/{projectId}/sprints   진행률 포함
POST   /api/projects/{projectId}/sprints   ADMIN
GET    /api/sprints/{sprintId}
PUT    /api/sprints/{sprintId}              ADMIN, 전체 교체
DELETE /api/sprints/{sprintId}              ADMIN
```

**BE**: `sprint/domain`(`Sprint` `SprintStatus`) · `SprintRepository` · `SprintService`(ProjectService ADMIN 가드, 진행률 집계, 삭제 시 Task unassign) · `SprintController` · `Task.assignSprint()` + `TaskRepository` sprint 카운트/필터 · `TaskController` `sprintId` 쿼리 파라미터
**FE**: `api/sprint.js` · `stores/sprint.js` · `components/sprint/`(`SprintForm` `SprintBoard`(TODO/IN_PROGRESS/DONE) `SprintPanel`) · ProjectDetail **Sprint 탭** (목록·생성·보드·Task 배정/해제) · `SprintView`(`/sprints`) 프로젝트별 Sprint 현황 오버뷰

**완료 기준**: Sprint 생성 → Task 배정 → 보드에서 상태별 확인, 진행률 계산 — curl E2E 검증 완료 (create 201, 비-ADMIN 403, PUT sprintId 배정, progress 50% (1/2), sprintId 필터, delete 204 → Task `sprintId` null + Sprint 404).

---

## 5. Meeting — 핵심 기능 진입

- [ ] 회의 목록 / 생성 / 상세 / 수정 / 삭제
- [ ] 회의 내용 직접 입력 · 메신저 대화 붙여넣기
- [ ] 회의 참석자 관리
- [ ] `AudioUploader` — 녹음본 파일 선택 UI (STT 연동은 Phase 6 후순위)

```http
GET    /api/projects/{projectId}/meetings
POST   /api/projects/{projectId}/meetings
GET    /api/meetings/{meetingId}
PUT    /api/meetings/{meetingId}
DELETE /api/meetings/{meetingId}
```

FE: `api/meeting.js` · `stores/meeting.js` · `MeetingForm` · `MeetingListView` 실데이터 · `MeetingDetailView` 내용 입력/저장

**완료 기준**: 회의 생성 → 목록 → 상세 → 내용 저장.

---

## 6. ⭐ AI 회의 분석 — 일로ON의 차별점

```text
회의 내용
  ├── 텍스트 / 메신저 대화   → 바로 분석
  └── 음성 파일 → STT → 회의 텍스트   (후순위)
        ↓
      Spring AI → LLM
        ↓
  Structured Output (JSON)
  ├── summary      회의 브리핑
  ├── decisions    결정사항
  └── actionPoints title / assignee / dueDate / priority
```

```http
POST /api/meetings/{meetingId}/analyze
GET  /api/meetings/{meetingId}/summary
```

- [ ] AI Service + Spring AI · 프롬프트 엔지니어링 · Structured Output · Advisor(공통 처리)
- [ ] `POST .../analyze` — 텍스트 입력 우선 구현, 녹음본(STT)은 이후
- [ ] FE: `AiSummary` · `ActionPointCard`(담당자·기한·우선순위 수정 가능)
- [ ] 담당자·기한을 확정 못하면 **"확인 필요 / 미지정"** 상태로 (강제로 채우지 않음)
- [ ] 분석 로딩 상태 · 우측 `AiAssistantPanel` 연동

### 핵심 UX — AI가 Task를 자동 확정하지 않는다

```text
AI 분석 → Action Point → 사용자 검토/수정 → [업무로 등록] → Task → Sprint 배정
```

- [ ] "업무로 등록" → `POST /api/tasks` 호출 시 `meeting_id` 연결

**완료 기준 (시나리오 B, D)**: 회의 텍스트 입력 → AI 분석하기 → 브리핑 + Action Point 표시 → 담당자/기한 수정 → 업무로 등록 → Task 상세에서 "관련 회의"로 역이동.

---

## 7. 메인보드 + 캘린더 연결

기능이 다 만들어진 뒤 **마지막에** 실데이터 연결. (처음부터 실데이터로 만들면 Task/Project/Sprint 구조가 바뀔 때마다 다시 뜯어고치게 됨.)

- [ ] `MainBoardView`: 내가 해야 할 Task · 내 프로젝트(진행률) · 현재 Sprint · 오늘의 일정
- [ ] 요약 칩 카운트 연동 (내 Task · 예정 회의 · Action Point · 마감 임박)
- [ ] `CalendarView`: 회의 일정 + Task 마감일 (필수화면 9)

**완료 기준**: 로그인 직후 메인보드에서 시나리오 A~D의 결과물이 한눈에 보인다.

---

## 8. 전체 연결 및 예외처리

```text
화면 필드 ↕ Request ↕ Response ↕ DB
```

- [ ] API URL 일치
- [ ] request / response 필드명 일치
- [ ] 날짜 형식 통일 (ISO-8601)
- [ ] enum 값 통일 — `TODO/IN_PROGRESS/DONE` · `HIGH/MEDIUM/LOW` · `PLANNED/ACTIVE/COMPLETED` · 프로젝트 상태 · 멤버 role(`ADMIN/MEMBER`)
- [ ] 에러 처리 (400/403/404/500 공통 토스트)
- [ ] 로딩 처리 (스켈레톤 → 실데이터 전환)
- [ ] 빈 데이터 처리 (프로젝트·회의·Task 0건)
- [ ] 로그인 만료(401) 처리 → 로그인 화면 + 원위치 복귀

---

## 9. 산출물 및 최종 테스트

- [ ] Swagger / OpenAPI YAML 최종화
- [ ] DBML / ERD 최종 검수
- [ ] UI ↔ API ↔ ERD ↔ AI 전체 연결 검증
- [ ] (선택) MSA 분리 · API Gateway · Eureka · Docker
- [ ] 예외 / 오류 시나리오 점검
- [ ] 5분 발표 준비 (Problem → Solution → 핵심 Flow → AI 기술 → System Design → Closing)

---

## 전체 순서 한 장

```text
0. 공통 기반 (남은 것 + ERD + BE 셋업)
   ↓
1. 인증
   ↓
2. 프로젝트  (시나리오 A)
   ↓
3. Task      (시나리오 C)
   ↓
4. Sprint
   ↓
5. Meeting
   ↓
6. AI 분석 ⭐  → 업무로 등록  (시나리오 B, D)
   ↓
7. 메인보드 + 캘린더
   ↓
8. 계약 점검 · 예외처리
   ↓
9. 산출물 · 최종 테스트
```

## Git — 기능 단위로 바로바로

```bash
feat: 회원가입 기능 구현
feat: 로그인 기능 구현
feat: 프로젝트 목록 조회 구현
feat: 프로젝트 생성 기능 구현
feat: Task CRUD 구현
feat: Sprint 관리 기능 구현
feat: 회의 관리 기능 구현
feat: AI 회의 분석 기능 구현
feat: 메인보드 데이터 연동
fix: 로그인 토큰 처리 오류 수정
```
