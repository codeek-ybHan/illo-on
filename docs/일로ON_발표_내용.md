# 일로ON 발표 PPT — 슬라이드 내용

> 코드(`front/`, `backend/`) + 기획서(`일로ON_기획서_최종.md`) + ERD + OpenAPI YAML 기반.
> 디자인은 `일로ON_발표_디자인프롬프트.md` 참조.

## 목차 (권장 순서)

원래 안: ① 개요 ② Pain–Solution ③ **요구사항** ④ 액터 ⑤ UI 흐름 ⑥ 데이터 모델 ⑦ API ⑧ 향후

**권장: ③과 ④를 바꿔 "액터 → 요구사항" 순서.**
- 이유 1: 요구사항(REQ)이 "누가" 하는지를 전제로 서술된다(관리자만 프로젝트 생성 등). 액터를 먼저 세우면 요구사항이 자연스럽게 읽힌다.
- 이유 2: ⑤ UI 흐름도가 "액터별로 구분"이라, 액터 정의가 바로 앞 슬라이드에 있어야 흐름이 자연스럽게 이어진다.
- 이유 3: 기획서 자체도 Actor(§3) → 요구사항(§9) 순서다.

아래 내용은 **권장 순서**로 정리했다. 원래 번호를 유지하고 싶으면 3·4 슬라이드만 스왑하면 된다.

---

## 1. 서비스 개요 및 목적

### 한 줄 정의
> **일로ON은 기업 프로젝트 팀을 대상으로, 회의와 메신저에서 발생한 논의·결정사항을 AI로 구조화하고 실제 Task·Sprint로 연결하여 업무 실행까지 지원하는 AI 기반 협업·업무관리 SaaS다.**

### 태그라인
> 회의에서 나온 일을, 메신저에서 결정된 일을, **일로ON**.

### 컨셉
- 새로운 AI 회의록 서비스를 만드는 것이 아니다.
- **기존 업무관리 플랫폼(프로젝트·Task·Sprint)에 AI 회의록 + Action Point 기능을 결합**한다.
- 회의록을 "기록"하는 데서 끝내지 않고, 결정된 일을 **실제 업무 흐름으로 연결**한다.

### 목적
- **논의가 발생하는 곳(회의·메신저)** ↔ **업무가 실행되는 곳(Task·Sprint·메인보드)** 사이의 단절 제거.

### 핵심 워크플로우 (가로 흐름 다이어그램)
```
회의 / 메신저 내용  →  AI 분석  →  요약 · 결정사항 · Action Point
  →  사용자 검토/수정  →  Task 등록  →  Sprint 배정  →  메인보드에서 실행·관리
```

### 타깃 사용자
- 기업 내 프로젝트 팀 — 프로젝트를 수행하며 회의와 업무를 반복하는 팀원과 관리자.

---

## 2. Pain Point → Solution

### 현재의 문제 (기존 업무 흐름)
```
회의/메신저 → 논의·결정 → 회의록 작성 → Action Point 확인
  → (별도 툴로 이동) Task 생성 → 담당자 배정 → 마감일 설정 → Sprint 배정
```

| # | Pain Point | 일로ON Solution |
|---|---|---|
| 1 | 회의·메신저에서 결정된 업무가 **누락**된다 | AI가 회의 텍스트에서 **Action Point를 자동 추출** — 빠뜨리지 않는다 |
| 2 | 담당자·기한을 **다시 입력**해야 한다 | AI가 **담당자 후보(assigneeHint)·기한 후보**를 제시, 사용자는 확인만 |
| 3 | 업무가 **어떤 논의에서 나왔는지 추적**하기 어렵다 | Task에 `meeting_id` 연결 → Task 상세에서 **"관련 회의"로 역이동** |
| 4 | 회의·메신저·업무툴이 **분리**되어 업무 흐름이 끊긴다 | 회의 → Action Point → Task → Sprint → 메인보드를 **한 플랫폼에서 연결** |
| 5 | 업무가 회의에서만 나오지 않는다 (메신저·채팅) | MVP는 **텍스트 붙여넣기**로 채널 무관 대응, 향후 Slack/Teams 연동 |

### 핵심 차별점 (하단 배너)
> AI가 업무를 **자동 확정하지 않는다.**
> `LLM → 자연어 답변 → 사람이 재입력` ❌
> `LLM → 구조화된 Action Point → 사용자 검토 → Task` ✅
> 담당자·기한이 불확실하면 **"미지정"(null)** 으로 두고 강제로 채우지 않는다.

---

## 3. 시스템 액터 정의

| 액터 | 역할 | 주요 기능 |
|---|---|---|
| **프로젝트 팀원** (`MEMBER`) | 프로젝트에 참여해 업무를 수행 | 로그인 · 프로젝트/업무/회의/회의록 조회 · 내 업무 조회 · 업무 상태 변경 · 일정(캘린더) 조회 |
| **프로젝트 관리자** (`ADMIN`) | 프로젝트를 만들고 팀의 업무·회의를 관리 | 프로젝트 생성/수정/삭제 · 팀원 초대(링크) · Sprint 생성/관리 · 회의 생성 · 참석자 관리 · **AI 분석 요청** · **Action Point 검토/수정** · **Task 등록** |
| **AI 시스템** | 회의·메신저 텍스트를 분석해 업무 후보를 생성 | (음성 시) STT 변환 · 회의 요약 · 주요 결정사항 추출 · Action Point 추출 · 담당자 후보 · 마감일 후보 · **AI 어시스턴트 대화**(내 현황 기반 응답) |
| **외부 메신저** *(향후)* | Slack/Teams 등 외부 커뮤니케이션 | API 연동으로 외부에서 발생한 논의를 직접 수집 (MVP 제외) |

### 각주
- 관리자는 **별도 계정 유형이 아니라 프로젝트별 역할(Role)**. 같은 사용자가 A 프로젝트에서는 ADMIN, B 프로젝트에서는 MEMBER일 수 있다.
- 프로젝트 생성자는 자동으로 해당 프로젝트의 `ADMIN` 멤버가 된다.

---

## 4. 요구사항 정리

### 기능 요구사항 (REQ-01 ~ REQ-22, 전부 **필수** = MVP 범위)

| 구분 | ID | 요구사항 |
|---|---|---|
| 회원 | REQ-01 | 이름·이메일·비밀번호로 회원가입 (이메일 인증 없음) |
| 회원 | REQ-02 | 이메일·비밀번호로 로그인 |
| 프로젝트 | REQ-03 | 내가 참여 중인 프로젝트 목록 조회 |
| 프로젝트 | REQ-04 | ADMIN은 프로젝트 생성 |
| 프로젝트 | REQ-05 | 참여자는 프로젝트 상세 조회 |
| 프로젝트 | REQ-06 | ADMIN은 프로젝트 수정·삭제 |
| 멤버 | REQ-07 | ADMIN은 초대 링크 생성 (7일 유효) |
| 멤버 | REQ-08 | 초대 링크로 프로젝트 참여 |
| 업무 | REQ-09 | Task 등록·조회·수정·삭제 |
| 업무 | REQ-10 | Task의 담당자·마감일·우선순위·상태 설정 |
| 업무 | REQ-11 | 나에게 할당된 업무 별도 조회 |
| Sprint | REQ-12 | ADMIN은 Sprint 생성 및 기간·상태 관리 |
| Sprint | REQ-13 | Task를 Sprint에 배정, Sprint별 목록·진행률 확인 |
| 회의 | REQ-14 | 프로젝트별 회의 등록, 목록·상세 조회 |
| 회의 | REQ-15 | 회의 내용 직접 입력 / 메신저 대화 붙여넣기 |
| 회의 | REQ-16 | 회의 녹음 파일 업로드로 회의 내용 분석 |
| AI | REQ-17 | AI가 회의 텍스트로 브리핑·주요 결정사항 생성 |
| AI | REQ-18 | AI가 Action Point 추출 + 담당자·기한 후보 제시 |
| AI | REQ-19 | 사용자가 AI Action Point를 검토·수정 |
| 업무 연계 | REQ-20 | 검토한 Action Point를 실제 Task로 등록 (`meeting_id` 연결) |
| 대시보드 | REQ-21 | 메인보드에서 내 업무·프로젝트·현재 Sprint를 한눈에 |
| AI 처리 | REQ-22 | 음성 입력 시 STT로 텍스트 변환 후 AI 분석 |

### MoSCoW (우선순위 범위)

- **Must**: 회원가입/로그인 · 프로젝트 생성·조회 · 팀원 초대 · Task 관리 · 회의 생성·조회 · 회의록/메신저 텍스트 입력 · 녹음본 업로드 · STT · AI 요약 · Action Point 추출/검토 · Task 등록 · Sprint 생성/배정 · My Tasks · 회의↔Task 연결
- **Should**: Team Tasks · Calendar · 검색/필터 · 우선순위
- **Could**: 마감 알림 · Slack/메일 직접 연동 · 반복 회의 · Burndown Chart
- **Won't**: 복잡한 Jira/Notion 대체 · 완전 자동 업무 생성 · AI가 담당자/마감일 강제 확정 · 대규모 프로젝트 관리

### 비기능
- 인증: JWT(stateless), 비밀번호 BCrypt 암호화
- 날짜: ISO-8601 통일 (`LocalDate` / `LocalDateTime`)
- Enum 값 FE/BE/DB 동일 문자열
- AI 장애 시 502 대신 **규칙 기반 분석으로 degrade** (요청은 200 유지)
- 에러 응답 규격 통일 `{ code, message, timestamp }`

---

## 5. 서비스 UI 흐름도 (액터별)

### 앱 구조
```
[아이콘 레일]  메인보드 · 회의 · 캘린더 · 프로젝트 · Sprint        ← 상위 네비게이션
[헤더]         브레드크럼 | 알림 · 검색(⌘K) · 만들기 · AI 패널
[AI 어시스턴트 패널]  우측 슬라이드 — 회의 상세 진입 시 자동 오픈
```
> **메인보드 = 업무를 관리하는 곳 / 회의 = 업무를 만들어내는 곳**

### 필수 화면 (9)
Login · 메인보드 · 회의 목록 · 회의 생성/상세 · Action Point 검토/수정 · 프로젝트 상세(Overview·Sprint·Tasks·Meetings) · Task 목록/상세 · Sprint · 캘린더

---

### 흐름 A — 프로젝트 관리자 (ADMIN)  *시나리오 A + B*
```
로그인 → 메인보드 → 프로젝트 생성 → 팀원 초대 링크 생성 → 링크 공유
   → (팀원 합류 후) 회의 생성
   → 회의 내용 입력  또는  녹음본 업로드
   → [AI 분석하기]
   → 회의 브리핑 확인 (요약 · 결정사항 · Action Point)
   → Action Point 검토 · 담당자/기한 수정
   → [업무로 등록] → Task 생성 (meeting_id 자동 연결)
   → Sprint 생성 → Task를 Sprint에 배정
   → 메인보드/Sprint 보드에서 진행 관리
```

### 흐름 B — 프로젝트 팀원 (MEMBER)  *시나리오 C + D*
```
초대 링크 접속 → (미로그인 시) 로그인/가입 후 복귀 → 프로젝트 참여
   → 메인보드: 내가 해야 할 Task · 내 프로젝트 진행률 · 현재 Sprint D-day · 오늘의 일정
   → 내 Task 선택 → 업무 수행 → 상태 변경 (TODO → IN_PROGRESS → DONE)
   → Task 상세 → "관련 회의" → 회의 상세
   → AI 요약 / 결정사항 / 원문으로 업무의 생성 맥락 확인
```

### 흐름 C — AI 시스템 (분석 파이프라인)
```
회의 입력
 ├─ 텍스트 / 메신저 대화  ─────────────┐
 └─ 음성 파일 → STT(Whisper/mock) → 회의 텍스트 저장 ┤
                                        ▼
                            Spring AI → LLM (gpt-4o-mini)
                                        ▼
                    Structured Output (JSON, entity 매핑)
                    ├─ summary       한눈에 보기
                    ├─ decisions[]   결정사항
                    └─ actionPoints[]  title · assigneeHint · dueDate · priority
                                        ▼
                            Backend 검증 · MeetingAnalysis 저장 (회의당 1건)
                                        ▼
                            사용자 검토 UI  ← AI는 여기서 멈춘다 (자동 Task 생성 X)
```
- `provider=mock`(기본): OpenAI 키 없이 규칙 기반(이름·날짜·우선순위 추출)
- `provider=openai`: Spring AI `ChatClient.entity(Briefing.class)` + Whisper STT
- OpenAI 호출 실패 시 자동으로 mock 분석으로 폴백

---

## 6. 데이터 모델 설계

### 핵심 관계
```
TEAM 1 ── N PROJECT
PROJECT N ── M USER      → PROJECT_MEMBER (role: ADMIN/MEMBER)
PROJECT 1 ── N PROJECT_INVITE
PROJECT 1 ── N MEETING
MEETING N ── M USER      → MEETING_MEMBER
PROJECT 1 ── N SPRINT
PROJECT 1 ── N TASK
MEETING 1 ── N TASK      (task.meeting_id, nullable — 업무 생성 맥락)
SPRINT  1 ── N TASK      (task.sprint_id, nullable — Sprint 배정)
USER    1 ── N TASK      (task.assignee_id, nullable — 담당자 미지정 허용)
MEETING 1 ── 1 MEETING_ANALYSIS  (회의당 1건, 재분석 시 덮어씀)
```
> **중심 원칙: PROJECT가 회의·업무·Sprint를 묶고, TASK가 MEETING·SPRINT를 참조한다.** (참조는 모두 nullable)

### 테이블 (11)
| 테이블 | 핵심 컬럼 |
|---|---|
| `users` | user_id PK · name · email UNIQUE(로그인 ID) · password(BCrypt) · created_at |
| `TEAM` | team_id PK · name |
| `PROJECT` | project_id PK · team_id FK · name · description · start_date · end_date · status |
| `PROJECT_MEMBER` | (project_id, user_id) PK · role |
| `PROJECT_INVITE` | invite_id PK · project_id FK · token UNIQUE · created_by · expires_at |
| `MEETING` | meeting_id PK · project_id FK · title · content(회의/메신저/STT 결과) · meeting_at · created_by |
| `MEETING_MEMBER` | (meeting_id, user_id) PK |
| `SPRINT` | sprint_id PK · project_id FK · name · start_date · end_date · status |
| `TASK` | task_id PK · project_id FK · meeting_id FK? · sprint_id FK? · assignee_id FK? · title · description · due_date · priority · status |
| `MEETING_ANALYSIS` | analysis_id PK · meeting_id UNIQUE FK · overview · source(TEXT/AUDIO) |
| `MEETING_ANALYSIS_*` | HIGHLIGHT / DECISION / ACTION_POINT 3종 @ElementCollection (seq PK, action point는 title·assignee_hint·due_date·priority) |

### Enum 통일표 (FE = BE = DB)
| Enum | 값 |
|---|---|
| `project_status` | `PLANNED` · `ACTIVE` · `COMPLETED` |
| `member_role` | `ADMIN` · `MEMBER` |
| `task_priority` | `HIGH` · `MEDIUM` · `LOW` |
| `task_status` | `TODO` · `IN_PROGRESS` · `DONE` |
| `sprint_status` | `PLANNED` · `ACTIVE` · `COMPLETED` |
| `analysis_source` | `TEXT` · `AUDIO` |

### 화면 ↔ 주요 테이블
| 화면 | 읽는 테이블 |
|---|---|
| 메인보드 | TASK(assignee=me) · PROJECT_MEMBER+PROJECT · SPRINT(ACTIVE) · MEETING(오늘) |
| 프로젝트 상세 | PROJECT · PROJECT_MEMBER · SPRINT · TASK · MEETING |
| 회의 상세 | MEETING · MEETING_MEMBER · MEETING_ANALYSIS · TASK(meeting_id) |
| Task 상세 | TASK · MEETING(관련) · users(담당자) |
| 캘린더 | MEETING(meeting_at) · TASK(due_date) |

### 설계 노트
- `ACTION_POINT`는 기획서상 "선택"이었으나, 재분석·`hasSummary` 표시를 위해 `MEETING_ANALYSIS` + 컬렉션 테이블로 영속화.
- AI가 확정 못 한 담당자/기한은 **검토 UI에서만 "미지정" 표기**, Task로 저장될 땐 그냥 `NULL`.

---

## 7. API 명세 정의

### 전체 31개 엔드포인트 (JWT Bearer, stateless)

| 도메인 | 수 | 주요 엔드포인트 |
|---|---:|---|
| Auth | 2 | `POST /api/auth/signup` · `POST /api/auth/login` |
| Project | 5 | `GET·POST /api/projects` · `GET·PUT·DELETE /api/projects/{id}` |
| Member / Invite | 3 | `POST /api/projects/{id}/invites` · `GET /api/projects/{id}/members` · `POST /api/invites/{token}/join` |
| Task | 6 | `GET·POST /api/tasks` · `GET·PUT·DELETE /api/tasks/{id}` · `GET /api/me/tasks` |
| Sprint | 5 | `GET·POST /api/projects/{id}/sprints` · `GET·PUT·DELETE /api/sprints/{id}` |
| Meeting | 6 | `GET·POST /api/projects/{id}/meetings` · `GET·PUT·DELETE /api/meetings/{id}` · `GET /api/me/meetings?from=&to=` |
| AI | 3 | `POST /api/meetings/{id}/analyze` · `GET /api/meetings/{id}/summary` · `POST /api/ai/chat` |
| Board | 1 | `GET /api/me/board` |

> 기획서 코어 + 대시보드 집계 2개(`/me/board`, `/me/meetings`, N+1 방지) + AI 어시스턴트 1개(`/ai/chat`) = **31개** (컨트롤러 `@Mapping` 및 `openapi.yaml` 기준).
> 상태 변경·Sprint 배정은 별도 API 없이 `PUT /api/tasks/{id}` 전체 교체로 처리.

### 핵심 비즈니스 흐름
```
POST /api/auth/signup → POST /api/auth/login → (JWT 발급)
   → POST /api/projects → POST /api/projects/{id}/invites → POST /api/invites/{token}/join
   → POST /api/projects/{id}/meetings → POST /api/meetings/{id}/analyze
   → (Action Point 검토) → POST /api/tasks  { projectId, meetingId, ... }
   → PUT /api/tasks/{id}  { sprintId }   ← Sprint 배정
```

### AI 분석 API 상세 — `POST /api/meetings/{meetingId}/analyze`
- 입력: `multipart/form-data`의 `audio` 파일 **또는** 빈 본문(저장된 회의 내용 분석)
- audio 있으면 → STT → `content` 저장 → 분석 / 없으면 → 저장된 `content` 분석
- 응답: `{ summary, decisions[], actionPoints[], source }` (`BriefingResponse`)
- `GET .../summary` — 저장된 분석 결과 조회 (없으면 404)

### 에러 규격
- 응답 형식 `{ code, message, timestamp }` 통일 (`GlobalExceptionHandler`)
- `401` 인증 실패 → FE 토큰 제거 + 로그인 리다이렉트
- `403` `NOT_PROJECT_MEMBER` / `NOT_PROJECT_ADMIN`
- `400` 검증 실패(비멤버 담당자 지정 등) · `404` 리소스 없음 · `409` 이메일 중복 / 이미 참여 · `410` 만료된 초대

### 산출물
- `docs/일로ON_openapi.yaml` — OpenAPI 3 (Swagger `/swagger-ui.html`, `/v3/api-docs.yaml`에서 생성)
- 검증: `backend/scripts/e2e.sh` 시나리오 A~D — **PASS 27 / FAIL 0** (mock·openai 양쪽)

---

## 8. 향후 추가 및 개선사항

### 기능 확장
- Team Tasks (팀 전체 업무 뷰) · 검색/필터 고도화 · 우선순위 정렬
- 마감 알림 (메일/푸시) · 반복 회의 · 프로젝트 아카이브

### AI 고도화
- `assigneeHint` → 프로젝트 멤버 **자동 매칭** (현재는 FE 수동 매칭)
- 회의록 **화자 분리(diarization)** 후 발화자별 Action Point
- **AI 기반 Sprint 업무 추천** · Sprint 회고 자동 요약
- STT 실패 시 환각 방지 가드 강화 (실제 이슈 대응 반영)

### 아키텍처
- 도메인별 **MSA 분리**: Project / Task / Meeting / AI Service
- **API Gateway** 단일 진입점 · **Eureka** Service Discovery (선택)
- 서비스 간 **Kafka 이벤트** (회의 분석 완료 → Task 후보 생성 등 비동기)
- **Docker / K8s** 배포 파이프라인 (현재 `docker-compose` 수준)

### 데이터 모델 진화
- `ACTION_POINT` 독립 테이블 승격 (상태·이력 관리)
- **Story Point · Backlog · Burndown Chart**
- 외부 메신저(Slack/Teams) 연동 테이블 · 원문 링크 보관

### 클로징 메시지
> **회의록을 작성하는 것에서 끝나는 것이 아니라, 회의와 메신저에서 결정된 업무가 실제 Task가 되어 Sprint에서 실행될 수 있도록 연결하는 업무관리 플랫폼입니다.**

---
---

# 부록 (Appendix)

> 발표 본편에는 넣지 않지만, 질의응답·심사 시 펼쳐 볼 참고 자료. 슬라이드 뒤쪽에 배치.

## A. 기술 스택 & 선택 이유

| 영역 | 기술 | 선택 이유 |
|---|---|---|
| Frontend | Vue 3 (`<script setup>`) · Vite | 빠른 HMR, 컴포지션 API로 상태 로직 분리 |
| | Vue Router · Pinia | 9개 화면 라우팅 + 전역 상태(auth·project·task·meeting·sprint·ui·ai) |
| | 순수 CSS 디자인 토큰 | 프레임워크 없이 `main.css` 변수로 톤 통일, 번들 경량 |
| Backend | Spring Boot 3.4 · Java 21 | 팀 숙련도, JPA·Security·AI 생태계 |
| | Spring Security + JWT(jjwt) | stateless 인증, MSA 확장 시 세션 서버 불필요 |
| | Spring Data JPA · H2/MySQL | dev는 무설치(H2 파일), prod는 MySQL |
| | **Spring AI 1.0** | `ChatClient.entity()` 로 LLM 응답을 DTO(record)에 바로 매핑 — 파싱 코드 제거 |
| | springdoc-openapi | 코드 → OpenAPI YAML 자동 생성, 계약 검증 |
| Infra | Docker 멀티스테이지 · nginx | 프론트 정적 서빙 + `/api` 프록시, 백엔드 포트 비공개 |

**핵심 기술 요약**: `STT + Spring AI + Structured Output + REST + JWT`

## B. 시스템 아키텍처

### 현재 (MVP — 모듈형 모놀리식)
```
Vue SPA ──(/api, JWT)──> Spring Boot 단일 앱
   패키지로 도메인 분리: auth · project · task · sprint · meeting · ai · board
   └─ ai 모듈: AiAnalyzer / SpeechToText 인터페이스 + provider별 구현(mock·openai)
DB: H2(dev) / MySQL(prod) 단일 스키마
```

### 목표 (확장 시)
```
              Frontend
                 │
            API Gateway (단일 진입점 · 인증 위임)
   ┌─────────────┼─────────────┬─────────────┐
 Project Svc   Task Svc    Meeting Svc     AI Svc ── LLM / STT
   │             │             │             │
 Project DB    Task DB      Meeting DB    (분석 결과)
        └──────── Kafka 이벤트(회의 분석 완료 → Task 후보) ────────┘
   + Eureka(Service Discovery, 선택) · Docker/K8s
```
> 도메인 경계를 패키지로 먼저 그어두어 서비스 분리 비용을 낮춤.

## C. AI 파이프라인 상세

### C-1. 프롬프트 설계 원칙 (`OpenAiAnalyzer` SYSTEM)
- 역할 부여: "시니어 PM 어시스턴트", 출력은 한국어·불릿·군더더기 없이
- 4개 필드 분리 지시: `overview`(한 문장) / `highlights`(3~7 불릿, 배경→쟁점→결론 순) / `decisions`(확정된 것만, 추측 금지) / `actionPoints`
- Action Point 규칙:
  - `title` = "무엇을 + 동작" 명사구 (대화 문장 금지). 예: "API 명세는 김민지가 9/15까지…" → **"API 명세서 작성"**
  - `assignee` = 명확히 지목된 경우만 이름, 아니면 `null` (지어내지 않음)
  - `dueDate` = 명확한 경우만 `yyyy-MM-dd`. "다음 주 금요일"·"9/15"는 **프롬프트에 주입한 '오늘' 기준**으로 계산, 임의 연도 금지
  - `priority` = `HIGH | MEDIUM | LOW` (기본 MEDIUM)

### C-2. Structured Output 스키마 (`Briefing` record)
```json
{
  "overview": "신규 서비스 출시 일정 및 개발 업무를 논의함",
  "highlights": ["10월 1일 출시 목표 확정", "API 우선 개발 합의"],
  "decisions": ["10월 1일 서비스 출시"],
  "actionPoints": [
    { "title": "API 명세서 작성", "assignee": "김민지", "dueDate": "2026-09-15", "priority": "HIGH" }
  ]
}
```
Spring AI `ChatClient.prompt().call().entity(Briefing.class)` — LLM JSON → record 자동 바인딩.

### C-3. mock(규칙 기반) 추출 — OpenAI 없이 데모
- 문장 분리 후 접속어 제거(`그리고|또한|따라서…`)
- **담당자**: `([가-힣]{2,4})(님|씨|대리|과장…)?(가|이|께서)` 정규식 + 한국어 성씨 집합으로 오탐 제거
- **마감일**: `9/15`·`다음 주 금요일`·`이번 달 말` 등 표현 → 오늘 기준 날짜 계산
- **우선순위**: "긴급"·"급함" → `HIGH`
- `.txt` 첨부는 mock STT가 원문 그대로 반환

### C-4. Graceful Degrade (요청은 항상 200)
| 실패 지점 | 폴백 | HTTP |
|---|---|---|
| `OpenAiAnalyzer` | `MockAiAnalyzer` (규칙 기반) | 200 |
| `OpenAiSpeechToText` (Whisper) | `MockSpeechToText` | 200 |
| `AiChatService` (어시스턴트) | 안내 문구 | 200 |
| 회의 내용 없이 analyze | — | 400 `MEETING_CONTENT_EMPTY` |
| STT 변환 자체 실패(형식 오류) | — | 422 `STT_FAILED` |

견고성 설정: 멀티파트 30MB · HTTP read-timeout 120s · Spring AI 재시도 2회.

### C-5. AI 어시스턴트(우측 패널) 컨텍스트 주입
`AiChatContext.render(userId)` 가 `[사용자 현황]`(보드 집계) + `[회의 기록]`(요약·결정·원문)을 짧은 트랜잭션에서 문자열화 → SYSTEM 프롬프트에 결합. 최근 8턴 히스토리 유지. "기록에 없으면 지어내지 말 것" 지시.

## D. 인증 / 보안 설계

- **JWT (HS256, jjwt)**: `subject = userId`, claim `email`, 만료 24h(`JWT_EXPIRATION_MS`)
- `JwtAuthenticationFilter` 가 `Authorization: Bearer` 파싱 → `@AuthenticationPrincipal Long userId` 로 주입
- **stateless** (`SessionCreationPolicy.STATELESS`), CSRF 비활성, CORS 화이트리스트(`:5173`, `:5174`)
- 비밀번호: **BCrypt**
- 공개 경로(`PUBLIC_PATHS`): `/api/auth/signup`, `/api/auth/login`, swagger/api-docs, `/h2-console`
- 그 외 전 경로 인증 필요 → 무효/만료 토큰은 `401` → FE 토큰 제거 + 로그인 리다이렉트(`redirect` 쿼리 보관)
- 권한: 프로젝트 멤버십·ADMIN 가드는 `ProjectService` 에서 재사용 (`NOT_PROJECT_MEMBER` / `NOT_PROJECT_ADMIN`)

## E. 전체 에러 코드 (`ErrorCode` enum)

응답 규격: `{ "code": "...", "message": "...", "timestamp": "..." }`

| code | HTTP | 메시지 |
|---|---|---|
| `INVALID_INPUT` | 400 | 입력값이 올바르지 않습니다. |
| `MEETING_CONTENT_EMPTY` | 400 | 분석할 회의 내용이 없습니다. |
| `UNAUTHORIZED` | 401 | 인증이 필요합니다. |
| `LOGIN_FAILED` | 401 | 이메일 또는 비밀번호가 올바르지 않습니다. |
| `INVALID_TOKEN` | 401 | 유효하지 않은 토큰입니다. |
| `FORBIDDEN` / `NOT_PROJECT_MEMBER` / `NOT_PROJECT_ADMIN` | 403 | 권한 없음 / 프로젝트 멤버 아님 / 관리자 전용 |
| `*_NOT_FOUND` (PROJECT·TASK·SPRINT·MEETING·INVITE) | 404 | 리소스를 찾을 수 없습니다. |
| `EMAIL_ALREADY_EXISTS` | 409 | 이미 사용 중인 이메일입니다. |
| `ALREADY_MEMBER` | 409 | 이미 참여 중인 프로젝트입니다. |
| `INVITE_EXPIRED` | 410 | 만료된 초대 링크입니다. |
| `STT_FAILED` | 422 | 음성 파일을 텍스트로 변환하지 못했습니다. (형식 확인 안내) |
| `AI_ANALYZE_FAILED` | 502 | AI 분석에 실패했습니다. *(폴백으로 사실상 미발생)* |
| `INTERNAL_ERROR` | 500 | 서버 오류가 발생했습니다. |

## F. API Request / Response 예시

### 로그인
```http
POST /api/auth/login
{ "email": "kim@illoon.com", "password": "password1" }
→ 200  { "token": "eyJ...", "user": { "userId": 1, "name": "김민지", "email": "kim@illoon.com" } }
```

### 회의 AI 분석 (텍스트)
```http
POST /api/meetings/12/analyze          (본문 없음 — 저장된 회의 내용 분석)
→ 200  { "summary": "...", "decisions": ["10월 1일 출시"],
         "actionPoints": [ { "title": "API 명세서 작성", "assignee": "김민지",
                             "dueDate": "2026-09-15", "priority": "HIGH" } ],
         "source": "TEXT" }
```

### 회의 AI 분석 (녹음본)
```http
POST /api/meetings/12/analyze          multipart/form-data;  audio=<meeting.m4a>
→ STT → content 저장 → 분석,  "source": "AUDIO"
```

### Action Point → Task 등록
```http
POST /api/tasks
{ "projectId": 3, "meetingId": 12, "title": "API 명세서 작성",
  "assigneeId": 1, "dueDate": "2026-09-15T18:00:00", "priority": "HIGH" }
→ 201  { "taskId": 45, ... , "status": "TODO" }
```

### Sprint 배정 (상태 변경도 동일 — 별도 API 없음)
```http
PUT /api/tasks/45     { ...전체 필드, "sprintId": 7, "status": "IN_PROGRESS" }
```

## G. 데이터 모델 — DDL & 마이그레이션

- 전체 DBML: `docs/일로ON.dbml` (dbdiagram.io 붙여넣기)
- 테이블 11개 + Enum 6종 (§6 참조)
- **마이그레이션 순서**: `users` → `TEAM` → `PROJECT` → (`PROJECT_MEMBER`, `PROJECT_INVITE`) → `MEETING` → `MEETING_MEMBER` → `SPRINT` → `TASK` → `MEETING_ANALYSIS` (+ 컬렉션 3종)
- dev(H2) `ddl-auto: update` 자동 / prod(MySQL) `validate` — 스키마 선생성 (compose는 데모용으로 `update` 오버라이드)
- `task.due_date`, `meeting_analysis_action_point.due_date` = `TIMESTAMP` (마감일**시**)

## H. 화면 ↔ 라우트

| 화면 | 라우트 | 주 액터 |
|---|---|---|
| 로그인 / 회원가입 | `/login` · `/signup` | 공통 |
| 메인보드 | `/` | 팀원·관리자 |
| 회의 목록 | `/meetings` | 팀원·관리자 |
| 회의 상세 (+ AI 브리핑·Action Point) | `/meetings/:id` | 관리자 |
| 프로젝트 목록 | `/projects` | 팀원·관리자 |
| 프로젝트 상세 (Overview·Sprint·Tasks·Meetings 탭) | `/projects/:id` | 팀원·관리자 |
| 초대 수락 | `/invite/:token` | 팀원 |
| Task 상세 | `/tasks/:id` | 팀원·관리자 |
| Sprint 현황 | `/sprints` | 관리자 |
| 캘린더 | `/calendar` | 팀원·관리자 |
| 앱 셸 공통 | 레일(5메뉴) · 헤더(검색 ⌘K·알림·만들기·AI 패널) | — |

## I. 요구사항 ↔ 구현 추적 매트릭스 (발췌)

| REQ | API | 화면 |
|---|---|---|
| REQ-01/02 회원가입·로그인 | `POST /api/auth/{signup,login}` | `/signup` `/login` |
| REQ-03~06 프로젝트 | `GET·POST /api/projects`, `GET·PUT·DELETE /api/projects/{id}` | `/projects` `/projects/:id` |
| REQ-07/08 초대 | `POST /api/projects/{id}/invites`, `POST /api/invites/{token}/join` | 프로젝트 상세, `/invite/:token` |
| REQ-09~11 업무 | `/api/tasks` CRUD, `GET /api/me/tasks` | 프로젝트 Tasks 탭, `/tasks/:id`, 메인보드 |
| REQ-12/13 Sprint | `/api/projects/{id}/sprints`, `/api/sprints/{id}`, `PUT /api/tasks/{id}` | 프로젝트 Sprint 탭, `/sprints` |
| REQ-14~16 회의 | `/api/projects/{id}/meetings`, `/api/meetings/{id}` | `/meetings`, `/meetings/:id` |
| REQ-17~19 AI | `POST /api/meetings/{id}/analyze`, `GET /api/meetings/{id}/summary` | 회의 상세 — AI 브리핑 / Action Point 카드 |
| REQ-20 업무 연계 | `POST /api/tasks` (`meetingId`) | Action Point "업무로 등록" |
| REQ-21 대시보드 | `GET /api/me/board`, `GET /api/me/meetings` | 메인보드, 캘린더 |
| REQ-22 STT | `POST /api/meetings/{id}/analyze` (multipart) | 회의 상세 — 녹음본 업로드 탭 |

## J. E2E 검증 결과 (`backend/scripts/e2e.sh`)

`PASS 27 / FAIL 0` — mock·openai 양쪽 통과.

| 시나리오 | 검증 |
|---|---|
| A | 회원가입 201 · 중복 409 · 로그인 실패 401 · 로그인 JWT · 프로젝트 생성 201 · 비멤버 조회 403 · 초대 · join 200 / 재참여 409 / 잘못된 토큰 404 |
| B | 회의 생성 201 · 텍스트 분석 200 · 빈 내용 400 · Action Point → Task(`meetingId`) · Sprint 배정 · 진행률 |
| C | `/api/me/tasks` 스코프 · 상태 `PUT` 반영 |
| D | Task → 관련 회의 역이동 · 회의 삭제 시 Task 유지·`meetingId` null |
| 집계 | `/api/me/board` — openTask·dueSoon·진행률·활성 Sprint·오늘 회의 |

## K. 개발 일정 (3일)

| DAY | 산출물 |
|---|---|
| 1 | Target/Actor · Pain Point · MVP · 요구사항 ID · Actor별 기능 · User Scenario · 전체 UI Flow · Wireframe |
| 2 | Entity·ERD·PK/FK · REST API · Request/Response · OpenAPI YAML · Spring AI/Prompt/Structured Output · STT 파이프라인 · MSA/Gateway/JWT 설계 |
| 3 | 기획서 리뷰 · 예외/오류 시나리오 · ERD/DBML/YAML 검수 · UI↔API↔ERD↔AI 연결 검증 · 제출 · 5분 발표 준비 |

## L. 용어집

| 용어 | 뜻 |
|---|---|
| Action Point | AI가 회의에서 추출한 "실행해야 할 업무" 후보. 검토 전이라 아직 Task 아님 |
| Task | 사용자가 확정한 실제 업무. `meeting_id` 로 생성 맥락(회의) 연결 |
| 브리핑 | AI 분석 결과 묶음 (한눈에 보기 + 결정사항 + Action Point) |
| Sprint | Task를 배정해 기간 단위로 진행 관리하는 Agile 단위 (`PLANNED`/`ACTIVE`/`COMPLETED`) |
| 메인보드 | 로그인 후 첫 화면. 내 업무·프로젝트·현재 Sprint·오늘 일정 집계 |
| STT | Speech-to-Text. 회의 녹음본 → 텍스트 (Whisper / mock) |
| Structured Output | LLM 응답을 자연어가 아닌 정해진 JSON 구조로 받는 것 (`Briefing` record) |
| Graceful Degrade | OpenAI 장애 시 502 대신 규칙 기반/안내 문구로 낮춰 응답 (요청은 200) |
| Provider (`mock`/`openai`) | AI 구현 선택 스위치 (`app.ai.provider`) |
