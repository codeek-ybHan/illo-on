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

### 남은 것

- [ ] 공통 컴포넌트: `BaseInput` · `BaseModal` · `BaseSelect`
- [ ] `api/axios.js` — baseURL, JWT Authorization 인터셉터, 401/에러 공통 처리
- [ ] `.env` (`VITE_API_BASE_URL` 등)
- [ ] Pinia 스토어 골격: `stores/auth.js` · `project.js` · `task.js`
- [ ] Router Guard (현재 `router/index.js` 주석 자리 활성화)
- [ ] `utils/date.js` · `utils/validation.js`
- [ ] **ERD / DDL / DBML** 확정 — `USER · TEAM · PROJECT · PROJECT_MEMBER · PROJECT_INVITE · MEETING · MEETING_MEMBER · TASK · SPRINT`
      (특히 `TASK.meeting_id`, `TASK.sprint_id` FK 구조)
- [ ] BE 셋업 (`backend/`): Spring Boot, CORS, JWT 필터, BCrypt, `@RestControllerAdvice` 예외 핸들러, Swagger/OpenAPI

---

## 1. 인증 — Signup → Login → JWT

- [ ] 회원가입 UI (`SignupView`) — 이름·이메일·비밀번호
- [ ] 로그인 UI (`LoginView`)
- [ ] `POST /api/auth/signup` — 이메일 중복 확인 → BCrypt 암호화 저장
- [ ] `POST /api/auth/login` — JWT 발급
- [ ] JWT 저장 + Axios Authorization 헤더 처리
- [ ] `stores/auth.js` — 로그인 상태 / 사용자 정보
- [ ] Router Guard — 미인증 시 `/login` 리다이렉트

```text
Signup → Login → JWT → 인증 상태 관리
```

**완료 기준**: 회원가입 → 로그인 → 메인보드 진입, 새로고침해도 로그인 유지, 만료 토큰이면 로그인 화면으로.

---

## 2. 프로젝트 — 업무의 기본 단위

- [ ] 프로젝트 목록 / 생성 / 상세 / 수정 / 삭제
- [ ] 프로젝트 멤버 조회
- [ ] 초대 링크 생성 · 복사
- [ ] 초대 링크로 프로젝트 참여 (미로그인 시 로그인 후 자동 이어받기)

```http
GET    /api/projects
POST   /api/projects
GET    /api/projects/{projectId}
PUT    /api/projects/{projectId}
DELETE /api/projects/{projectId}

POST /api/projects/{projectId}/invites
GET  /api/projects/{projectId}/members
POST /api/invites/{token}/join
```

FE: `api/project.js` · `stores/project.js` · `ProjectForm`(생성 모달) · `ProjectDetailView` Overview 탭 · 초대 수락 화면

**완료 기준 (시나리오 A)**: 프로젝트 생성 → 초대 링크 공유 → 다른 계정이 링크로 참여 → 멤버 목록에 표시.

---

## 3. Task — 실제 업무 관리

- [ ] Task 목록 / 생성 / 상세 / 수정 / 삭제
- [ ] 담당자 지정 · 마감일 · 우선순위(`HIGH/MEDIUM/LOW`) · 상태(`TODO/IN_PROGRESS/DONE`)
- [ ] 내 업무 목록
- [ ] 상태 변경 (`TODO → IN_PROGRESS → DONE`)

```http
GET    /api/tasks
POST   /api/tasks
GET    /api/tasks/{taskId}
PUT    /api/tasks/{taskId}
DELETE /api/tasks/{taskId}
GET    /api/me/tasks
```

FE: `api/task.js` · `stores/task.js` · `TaskCard` / `TaskList` / `TaskForm` · `TaskDetailView` 실데이터

**완료 기준 (시나리오 C)**: 로그인 → 내 Task 확인 → 상태 변경 반영.

> Meeting/AI보다 Task를 먼저 만드는 이유: AI의 "업무로 등록" 단계가 Task CRUD에 의존한다.
> Task를 먼저 세워두면 이후 AI 단계가 그 자리에서 끝까지 검증된다.

---

## 4. Sprint

- [ ] Sprint 목록 / 생성 / 상세 / 수정 / 삭제
- [ ] Task → Sprint 배정
- [ ] Sprint 진행률 · Sprint별 Task 목록

```http
GET    /api/projects/{projectId}/sprints
POST   /api/projects/{projectId}/sprints
GET    /api/sprints/{sprintId}
PUT    /api/sprints/{sprintId}
DELETE /api/sprints/{sprintId}
```

**Sprint 배정은 별도 API를 만들지 않는다** — `PUT /api/tasks/{taskId}` 에 `{ "sprintId": 1 }` 로 연결 (전체 API 27개 유지).

FE: `api/sprint.js` · `SprintCard` / `SprintTaskList` · `SprintView` 보드(TODO / IN_PROGRESS / DONE)

**완료 기준**: Sprint 생성 → Task 배정 → 보드에서 상태별로 확인, 진행률 계산.

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
