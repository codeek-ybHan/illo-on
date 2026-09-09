# 업무관리 플랫폼 + AI 회의록 서비스 기획서

## 1. 프로젝트 개요

### 서비스 컨셉

**프로젝트와 업무를 관리하는 플랫폼에 AI 회의록 기능을 결합하여, 회의에서 논의된 내용을 실제 업무로 연결하는 B2B 협업 업무관리 서비스**

핵심은 새로운 AI 회의록 서비스를 만드는 것이 아니라, **기존 업무관리 플랫폼에 회의록과 AI Action Point 기능을 추가하는 것**이다.

### 핵심 Workflow

**Project → Meeting → AI Summary → Action Point → Task → Schedule → Track**

- **Project**: 프로젝트 단위로 업무와 회의를 관리
- **Meeting**: 프로젝트와 연결된 회의 생성 및 회의록 관리
- **AI Summary**: 회의 내용을 AI가 요약
- **Action Point**: 회의에서 실행해야 할 업무 자동 추출
- **Task**: 확인된 Action Point를 실제 업무로 등록
- **Schedule**: 담당자와 마감일을 기준으로 일정 관리
- **Track**: 업무 진행상태 관리

### 핵심 가치

> **회의를 기록하는 데서 끝나는 것이 아니라, 회의에서 결정된 일을 실제 업무로 연결한다.**

---

# 2. 기획 배경

## 2-1. 기존 업무관리 플랫폼의 문제

일반적인 업무관리 플랫폼에서는 프로젝트와 Task를 관리할 수 있지만, **업무가 왜 생성되었는지에 대한 회의 맥락이 분리되는 경우**가 있다.

반대로 회의록 서비스에서는 회의 내용과 결정사항을 기록할 수 있지만, 회의에서 나온 업무를 다시 업무관리 시스템에 입력해야 하는 경우가 있다.

### 문제

```text
회의
 ↓
회의록 작성
 ↓
Action Point 확인
 ↓
별도 업무관리 플랫폼에서 Task 생성
 ↓
담당자 배정
 ↓
마감일 설정
```

회의와 업무 사이에 **수작업으로 연결해야 하는 과정**이 발생한다.

---

# 3. 해결하고자 하는 문제

### Pain Point 1. 회의와 업무가 분리됨

회의에서 업무가 결정되어도 실제 Task는 별도로 생성해야 한다.

### Pain Point 2. 회의 후 Action Point 정리에 시간이 걸림

회의 내용을 다시 읽으면서 실행해야 할 업무를 찾아야 한다.

### Pain Point 3. 담당자와 마감일 관리가 번거로움

회의에서 언급된 담당자와 일정을 다시 확인하고 업무에 입력해야 한다.

### Pain Point 4. 업무의 생성 맥락을 추적하기 어려움

기존 Task만 보면 **“이 업무가 어느 회의에서 왜 결정되었는가?”**를 확인하기 어렵다.

---

# 4. 서비스가 제공하는 해결책

## 기존

```text
회의
 ↓
회의록
 ↓
사람이 Action Point 추출
 ↓
업무관리 플랫폼에 재입력
 ↓
Task 관리
```

## 서비스 적용 후

```text
프로젝트
 ↓
회의 생성
 ↓
AI 회의록 분석
 ↓
요약 + 결정사항 + Action Point
 ↓
사용자 검토
 ↓
[업무로 등록]
 ↓
Task 생성
 ↓
담당자 / 마감일 / 상태 관리
```

### 핵심 차별점

> **회의와 업무를 하나의 업무관리 흐름 안에서 연결한다.**

---

# 5. 타깃 사용자

## Primary Actor

**기업 내 프로젝트 팀원**

프로젝트를 수행하면서 업무와 회의를 반복적으로 진행하는 사용자.

## 주요 사용자

### 팀원

- 본인 업무 확인
- 프로젝트 확인
- 회의록 확인
- 회의에서 생성된 Action Point 확인
- 업무 상태 변경
- 일정 확인

### 프로젝트/회의 관리자

- 프로젝트 관리
- 회의 생성
- 참석자 지정
- AI 회의록 분석
- Action Point 검토
- 담당자/마감일 수정
- Task 등록

> 프로젝트/회의 관리자는 별도 계정 유형이 아니라 팀원에게 부여되는 역할로 설계할 수 있다.

---

# 6. Actor

## Actor 1. 팀원

### 기능
- 로그인
- 프로젝트 조회
- 업무 조회
- 회의 조회
- 회의록 조회
- 본인 업무 조회
- 업무 상태 변경
- 일정 조회

## Actor 2. 프로젝트/회의 관리자

### 기능
- 프로젝트 생성/관리
- 회의 생성
- 참석자 관리
- 회의 내용 입력
- AI 분석 요청
- Action Point 검토/수정
- Task 등록

## Actor 3. AI 시스템

### 기능
- 회의 내용 요약
- 주요 결정사항 추출
- Action Point 추출
- 담당자 후보 추출
- 마감일 후보 추출

## Actor 4. 관리자 — 선택

- 사용자 관리
- 팀 관리

3일 프로젝트에서는 관리자 기능을 최소화하거나 제외한다.

---

# 7. 핵심 기능

## 7-1. 프로젝트 관리

업무와 회의를 프로젝트 단위로 묶는다.

### 프로젝트 정보
- 프로젝트명
- 프로젝트 설명
- 기간
- 프로젝트 상태
- 참여자

### Project Detail

```text
Project A
├── Overview
├── Tasks
├── Meetings
└── Calendar
```

---

# 8. 업무 관리

기존 업무관리 플랫폼의 핵심 기능.

### Task 정보

- 업무명
- 설명
- 프로젝트
- 담당자
- 마감일
- 우선순위
- 상태
- 생성일
- 관련 회의

### 상태

```text
TODO
 ↓
IN_PROGRESS
 ↓
DONE
```

### My Tasks

로그인 사용자가 담당한 업무를 모아 보여준다.

---

# 9. 회의 관리

## 회의 생성

프로젝트 내부에서 회의를 생성한다.

### 입력

- 회의 제목
- 회의 일시
- 참석자
- 회의 내용
- 관련 프로젝트

### Meeting List

프로젝트별 회의 목록을 제공한다.

- 회의 제목
- 회의 일시
- 참석자
- Action Point 수
- 분석 상태

---

# 10. AI 회의록

회의 내용을 AI가 분석한다.

### AI 결과

#### 회의 요약
회의에서 논의된 핵심 내용을 요약.

#### 주요 결정사항
팀이 합의하거나 결정한 내용을 추출.

#### Action Point
실제로 수행해야 할 업무를 추출.

#### 담당자 / 마감일
회의 내용에서 담당자와 날짜가 명시된 경우 후보값으로 추출.

---

# 11. Action Point → Task

## 핵심 기능

AI가 추출한 Action Point를 **실제 업무(Task)로 등록**한다.

### 예시

회의 내용:

> “김대리는 다음 주까지 API 명세 작성해주세요.”

AI 결과:

```text
Action Point

업무명: API 명세 작성
담당자: 김대리
마감일: 2026-09-15
우선순위: HIGH
```

사용자가 확인 후:

**[업무로 등록]**

→ Task 생성

### 중요한 원칙

AI가 업무를 **자동 확정하지 않는다.**

```text
AI 추출
 ↓
사용자 검토
 ↓
수정 가능
 ↓
업무 등록
```

이를 통해 잘못된 담당자나 날짜가 자동으로 업무에 반영되는 문제를 줄인다.

---

# 12. 회의 ↔ 업무 연결

Task에 `meeting_id`를 연결하여 업무의 생성 맥락을 추적한다.

예:

```text
Project A
   │
   ├── Meeting #01
   │      │
   │      ├── Action Point
   │      │       ↓
   │      └────── Task #101
   │
   └── Task #102
```

Task 상세에서:

> **“이 업무는 9월 8일 신규 서비스 출시 회의에서 생성되었습니다.”**

와 같이 원본 회의로 이동할 수 있도록 설계한다.

### 핵심 가치

**업무 → 회의 원문/결정사항까지 추적 가능**

---

# 13. 화면 구성

## 필수 화면

### 1. Login

- 이메일
- 비밀번호
- 로그인

### 2. Dashboard

- 진행 중인 프로젝트
- 오늘 마감 업무
- 마감 임박 업무
- 최근 회의
- 최근 생성된 Action Point

### 3. Project List

- 프로젝트 목록
- 프로젝트 상태
- 기간
- 참여자

### 4. Project Detail

탭 구조 추천:

```text
[Overview] [Tasks] [Meetings] [Calendar]
```

### 5. Task List / Task Detail

- 업무 목록
- 상태
- 담당자
- 마감일
- 우선순위
- 관련 회의

### 6. Meeting List

- 회의 목록
- 제목
- 날짜
- 참석자
- AI 분석 상태

### 7. Meeting Detail

- 회의 정보
- 회의 내용
- AI Summary
- 결정사항
- Action Point

### 8. Action Point 확인/수정

- 업무명
- 담당자
- 마감일
- 우선순위
- 수정/삭제
- 업무 등록

### 9. My Tasks

- 내 업무
- 상태
- 마감일
- 우선순위

### 10. Calendar

- 프로젝트 일정
- 업무 마감일
- 회의 일정

---

# 14. UI Flow

```text
Login
  ↓
Dashboard
  │
  ├────────→ Project List
  │               ↓
  │          Project Detail
  │           ┌────┼─────┐
  │           ↓    ↓     ↓
  │         Tasks Meetings Calendar
  │                ↓
  │          Meeting Detail
  │                ↓
  │         AI Summary
  │                ↓
  │        Action Points
  │                ↓
  │          사용자 검토
  │                ↓
  │          [업무로 등록]
  │                ↓
  │              Task
  │
  └────────→ My Tasks
```

---

# 15. 핵심 사용자 시나리오

### Scenario A — 회의 주최자

```text
프로젝트 선택
 ↓
회의 생성
 ↓
회의 내용 입력
 ↓
AI 분석
 ↓
회의 요약 확인
 ↓
Action Point 확인
 ↓
담당자/마감일 수정
 ↓
업무 등록
 ↓
Project Task에 반영
```

### Scenario B — 팀원

```text
로그인
 ↓
Dashboard
 ↓
My Tasks
 ↓
새로 배정된 업무 확인
 ↓
업무 수행
 ↓
상태 변경
```

### Scenario C — 업무 맥락 확인

```text
Task Detail
 ↓
관련 회의
 ↓
Meeting Detail
 ↓
AI Summary / 결정사항
 ↓
회의 원문 확인
```

---

# 16. AI 적용 구조

## Pipeline

```text
회의 내용
    ↓
Spring AI
    ↓
LLM
    ├── Summary
    ├── Decisions
    └── Action Points
             ↓
      Structured Output
             ↓
       Backend Validation
             ↓
        사용자 검토
             ↓
          Task DB
```

## Structured Output

AI 응답을 서비스에서 사용할 수 있는 JSON 형태로 구조화한다.

```json
{
  "summary": "신규 서비스 출시 일정 및 개발 업무를 논의함",
  "decisions": [
    "10월 1일 서비스 출시"
  ],
  "actionPoints": [
    {
      "title": "API 명세 작성",
      "assignee": "김OO",
      "dueDate": "2026-09-15",
      "priority": "HIGH"
    }
  ]
}
```

### 핵심

**LLM → 자연어 답변 → 사람이 다시 입력**

이 아니라

**LLM → 구조화된 Action Point → 사용자 검토 → Task**

로 연결한다.

---

# 17. SKALA 기술 적용

| 기술 | 적용 |
|---|---|
| Spring Boot | Backend 서버 |
| REST API | Project/Task/Meeting CRUD |
| Swagger/OpenAPI | API 명세 |
| JWT/JWK | 사용자 인증 |
| Spring AI | 회의 AI 분석 |
| Prompt Engineering | 회의 요약/Action Point 추출 |
| Structured Output | AI 결과 JSON 구조화 |
| Advisor | AI 요청 공통 처리 |
| MSA | Project/Task/Meeting/AI 도메인 분리 |
| API Gateway | 서비스 API 진입점 |
| Eureka | Service Discovery |
| Kafka | AI 분석 완료/Task 생성 이벤트 |
| Docker | 컨테이너화 |
| Agile | MVP 및 팀 업무 관리 |

### 이번 프로젝트에서 핵심으로 보여줄 기술

**Spring AI + Structured Output + REST API + JWT**

MSA/Kafka/Eureka/Docker는 프로젝트 범위에 따라 설계 또는 선택 구현한다.

---

# 18. MSA Architecture

```text
                         Frontend
                            │
                            ▼
                       API Gateway
                            │
             ┌──────────────┼──────────────┐
             ↓              ↓              ↓
       Project Service   Task Service   Meeting Service
             │              │              │
             ↓              ↓              ↓
        Project DB       Task DB        Meeting DB
                                           │
                                           ↓
                                       AI Service
                                           │
                                           ↓
                                          LLM
```

### 서비스 역할

#### Project Service
- 프로젝트
- 팀/프로젝트 참여자

#### Task Service
- Task
- 담당자
- 상태
- 마감일
- 우선순위

#### Meeting Service
- 회의
- 참석자
- 회의록

#### AI Service
- 요약
- 결정사항
- Action Point 추출

---

# 19. Kafka 적용

AI 분석 완료 후 Action Point를 Task로 전달하는 이벤트에 적용 가능.

```text
Meeting Service
      ↓
AI 분석 요청
      ↓
AI Service
      ↓
분석 완료
      ↓
Kafka Event
      ↓
Task Service
      ↓
Action Point / Task 생성
```

예:

```json
{
  "eventType": "MEETING_ANALYZED",
  "meetingId": 101,
  "projectId": 10,
  "actionPointCount": 3
}
```

### 장점
- Meeting과 Task 서비스 결합도 감소
- 후속 처리 비동기화
- 추후 알림 서비스 확장 가능

---

# 20. ERD 초안

## USER

```text
user_id
name
email
password
created_at
```

## TEAM

```text
team_id
name
created_at
```

## PROJECT

```text
project_id
team_id
name
description
start_date
end_date
status
created_at
```

## PROJECT_MEMBER

```text
project_id
user_id
```

## TASK

```text
task_id
project_id
meeting_id
title
description
assignee_id
due_date
priority
status
created_at
```

## MEETING

```text
meeting_id
project_id
title
content
meeting_at
created_by
created_at
```

## MEETING_MEMBER

```text
meeting_id
user_id
```

## ACTION_POINT — 선택

Action Point를 AI 분석 결과와 실제 Task 사이의 임시 객체로 별도 관리할 경우 사용한다.

```text
action_point_id
meeting_id
title
description
assignee_id
due_date
priority
status
created_at
```

### 추천

3일 프로젝트라면 `ACTION_POINT`를 별도 테이블로 만들지 않고, **AI 분석 결과를 사용자 검토 후 TASK로 생성하는 구조**로 단순화하는 것도 좋다.

---

# 21. 관계

```text
TEAM
  1
  │
  N
PROJECT

PROJECT
  N
  │
  N
USER
  ↓
PROJECT_MEMBER

PROJECT
  1
  │
  N
MEETING

MEETING
  N
  │
  N
USER
  ↓
MEETING_MEMBER

PROJECT
  1
  │
  N
TASK

MEETING
  1
  │
  N
TASK
```

핵심은:

> **PROJECT가 업무와 회의를 모두 묶고, TASK가 MEETING을 참조할 수 있도록 하는 것.**

---

# 22. REST API 초안

## 인증

```http
POST /api/auth/login
```

## Project

```http
GET    /api/projects
POST   /api/projects
GET    /api/projects/{projectId}
PUT    /api/projects/{projectId}
DELETE /api/projects/{projectId}
```

## Task

```http
GET    /api/tasks
POST   /api/tasks
GET    /api/tasks/{taskId}
PUT    /api/tasks/{taskId}
DELETE /api/tasks/{taskId}
```

## Project Tasks

```http
GET /api/projects/{projectId}/tasks
```

## Meeting

```http
GET    /api/projects/{projectId}/meetings
POST   /api/projects/{projectId}/meetings
GET    /api/meetings/{meetingId}
PUT    /api/meetings/{meetingId}
DELETE /api/meetings/{meetingId}
```

## AI

```http
POST /api/meetings/{meetingId}/analyze
GET  /api/meetings/{meetingId}/summary
```

## My Tasks

```http
GET /api/me/tasks
```

## Calendar

```http
GET /api/calendar
```

---

# 23. 요구사항 ID

| ID | 요구사항 | 우선순위 |
|---|---|---|
| REQ-01 | 사용자 로그인 | Must |
| REQ-02 | 프로젝트 생성/조회 | Must |
| REQ-03 | 프로젝트별 Task 관리 | Must |
| REQ-04 | Task 담당자/마감일/상태 관리 | Must |
| REQ-05 | 프로젝트별 회의 생성/조회 | Must |
| REQ-06 | 회의록 작성/조회 | Must |
| REQ-07 | AI 회의 요약 | Must |
| REQ-08 | AI Action Point 추출 | Must |
| REQ-09 | Action Point 검토/수정 | Must |
| REQ-10 | Action Point → Task 등록 | Must |
| REQ-11 | 관련 회의 조회 | Must |
| REQ-12 | My Tasks | Must |
| REQ-13 | Team Tasks | Should |
| REQ-14 | Calendar | Should |
| REQ-15 | 알림 | Could |

---

# 24. MVP 범위

## Must Have

- 로그인
- 프로젝트
- Task 관리
- 회의 생성/조회
- 회의록
- AI 요약
- Action Point 추출
- Action Point 검토/수정
- Task 등록
- My Tasks
- 회의 ↔ Task 연결

## Should Have

- Team Tasks
- Calendar
- 검색/필터
- 우선순위

## Could Have

- 마감 알림
- Slack/메일 연동
- 회의 녹음 업로드
- 자동 음성 인식
- 반복 회의

## Won't Have

- 복잡한 Jira/Notion 대체 기능
- 완전 자동 업무 생성
- AI가 담당자/마감일을 강제로 확정
- 대규모 프로젝트 관리 기능

---

# 25. 서비스 차별화

## 단순 AI 회의록

```text
회의
 ↓
AI 요약
 ↓
회의록
```

## 일반 업무관리

```text
프로젝트
 ↓
Task
 ↓
담당자 / 일정
```

## 우리 서비스

```text
프로젝트
 ↓
회의
 ↓
AI 요약
 ↓
Action Point
 ↓
사용자 검토
 ↓
Task
 ↓
담당자 / 일정
 ↓
업무 진행
```

### 차별화 문장

> **“회의록과 업무관리 기능을 별도로 사용하는 것이 아니라, 회의에서 나온 결정과 업무를 프로젝트의 실제 Task로 바로 연결합니다.”**

---

# 26. 3일 작업 일정

## DAY 1 — 서비스 기획 + UI

### 오전

**09:00–10:00**
- 과제 평가 기준 확인
- 산출물 구조 확인
- 기존 업무관리 플랫폼의 범위 정의

**10:00–11:00**
- Target User / Actor 확정
- Pain Point 정의

**11:00–12:00**
- 서비스 핵심 가치 확정
- Project → Meeting → Task 구조 확정
- MVP 범위 확정

### 오후

**13:00–14:00**
- 요구사항 ID 작성
- Actor별 기능 정의

**14:00–15:00**
- User Scenario
- 전체 UI Flow

**15:00–17:00**
Wireframe 제작:
1. Login
2. Dashboard
3. Project List
4. Project Detail
5. Task List/Detail
6. Meeting List
7. Meeting Detail
8. Action Point 확인/수정
9. My Tasks
10. Calendar

**17:00–18:00**
- Requirement ↔ UI 검증
- 화면 누락 확인
- Day 1 문서 정리

### DAY 1 완료 기준

```text
Problem
 ↓
Target User
 ↓
Solution
 ↓
Requirement
 ↓
UI Flow
```

---

# 27. DAY 2 — ERD + API + AI Architecture

## 오전

**09:00–10:00**
- UI에서 필요한 데이터 추출
- Entity 목록 확정

**10:00–11:30**
- ERD 작성
- PK/FK
- 1:N / N:M
- 데이터 타입
- 제약조건

**11:30–12:00**
- UI ↔ ERD 검증

## 오후

**13:00–14:30**
- REST API 전체 작성
- HTTP Method
- Path
- Query
- Body
- Request/Response

**14:30–15:30**
- API ↔ ERD 검증
- Error Code 정의

**15:30–16:30**
- Spring AI 구조
- Prompt
- Structured Output
- AI → Action Point → Task Flow

**16:30–17:30**
- MSA Architecture
- API Gateway
- Kafka
- Eureka
- JWT/JWK

**17:30–18:00**
- 전체 연결 검증

```text
Requirement
 ↓
UI
 ↓
API
 ↓
DB
 ↓
AI
```

---

# 28. DAY 3 — 최종 보완 + 제출 + 발표

## 오전

**09:00–10:00**
- 전체 기획서 리뷰
- Problem → Solution 논리 검증

**10:00–11:00**
- Wireframe 보완
- 예외/오류 상황 추가
- AI 결과 검토/수정 Flow 보완

**11:00–12:00**
- ERD 최종
- DBML 최종
- API YAML 최종

**12:00–13:00**
- 점심 / 파일 정리

**13:00–14:00**
- PDF 최종 검수
- YAML 검수
- DBML 검수
- UI Flow가 PDF에 포함되었는지 확인

**14:00**
- 최종 제출

**14:30**
- 5분 발표

---

# 29. 팀 업무 분담

## 역할 A — PM / 서비스 기획

### 담당
- 서비스 개요
- Target User
- Pain Point
- 핵심 가치
- 요구사항
- Actor
- User Scenario
- 최종 문서 통합

### DAY 1 집중

---

## 역할 B — UI/UX

### 담당
- UI Flow
- Wireframe
- 화면별 기능
- 화면 간 이동
- 예외/오류 화면

### DAY 1 집중

---

## 역할 C — DB/ERD

### 담당
- Entity
- Schema
- PK/FK
- 관계
- DBML
- UI 데이터 검증

### DAY 2 집중

---

## 역할 D — API/AI Architecture

### 담당
- REST API
- Request/Response
- OpenAPI YAML
- Spring AI
- Prompt
- Structured Output
- AI Flow
- MSA Architecture

### DAY 2 집중

---

## 공동 업무

모든 팀원이 다음을 함께 검증한다.

```text
UI
 ↕
API
 ↕
ERD
 ↕
AI
```

특히 **화면에 있는 데이터가 API와 ERD에 실제로 존재하는지** 반드시 확인한다.

---

# 30. 발표 구성 — 5분

## 1. Problem — 40초

> 업무관리 플랫폼과 회의록이 분리되어 있어, 회의에서 결정된 업무를 다시 Task로 등록해야 하는 불편이 있습니다.

## 2. Solution — 50초

> 저희는 기존 업무관리 플랫폼에 회의록 기능을 결합했습니다. 회의 내용을 AI가 분석하고 Action Point를 추출하여 실제 Task로 연결합니다.

## 3. 핵심 Flow — 1분

```text
Project
 ↓
Meeting
 ↓
AI Summary
 ↓
Action Point
 ↓
사용자 검토
 ↓
Task
```

## 4. AI 기술 — 1분

> Spring AI와 Structured Output을 활용해 회의 내용을 요약하고 Action Point를 구조화된 데이터로 추출합니다. AI가 생성한 결과는 사용자가 검토한 후 Task로 등록됩니다.

## 5. System Design — 1분

> Project, Task, Meeting, AI 도메인을 분리하고 REST API 기반으로 연결했습니다. 향후 MSA와 Kafka를 적용하여 서비스 확장성을 고려했습니다.

## 6. Closing — 30초

> **“회의록을 작성하는 것에서 끝나는 것이 아니라, 회의에서 결정된 업무가 실제 Task가 되어 실행될 수 있도록 연결하는 업무관리 플랫폼입니다.”**

---

# 31. 최종 핵심 문장

### 서비스 정의

> **프로젝트와 업무를 관리하는 플랫폼에 AI 회의록 기능을 결합하여, 회의에서 논의된 내용을 실제 업무로 연결하는 B2B 협업 업무관리 서비스.**

### 핵심 문제

> **회의와 업무가 분리되어 회의에서 결정된 내용을 다시 Task로 등록하고 관리해야 한다.**

### 핵심 해결책

> **회의 내용을 AI가 요약하고 Action Point를 추출하여 사용자의 검토 후 실제 Task로 연결한다.**

### 핵심 차별화

> **회의록 → 업무관리의 단절을 없앤다.**

### 핵심 Workflow

> **Project → Meeting → AI Summary → Action Point → Task → Schedule → Track**
