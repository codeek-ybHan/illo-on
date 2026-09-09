# 일로ON (illo-on)

# 업무관리 플랫폼 + AI 회의록 서비스 기획서

> **회의에서 나온 일을, 메신저에서 결정된 일을, 일로ON.**

## 1. 프로젝트 개요

### 1-1. 서비스 컨셉

**프로젝트와 업무를 관리하는 플랫폼에 AI 회의록 기능을 결합하여, 회의와 메신저에서 논의된 내용을 실제 업무로 연결하는 B2B 협업 업무관리 서비스**

핵심은 새로운 AI 회의록 서비스를 만드는 것이 아니라, **기존 업무관리 플랫폼에 회의록과 AI Action Point 기능을 추가하는 것**이다.

### 1-2. 핵심 Workflow

**회의 / 메신저 → AI 분석 → Action Point → 사용자 검토 → Task → Sprint → 업무 진행 관리**

- **Project**: 프로젝트 단위로 업무와 회의를 관리
- **Meeting**: 프로젝트와 연결된 회의 생성 및 회의 내용 관리
- **AI Summary**: 회의 및 입력된 대화 내용을 AI가 요약
- **Action Point**: 실행해야 할 업무를 AI가 추출
- **Task**: 사용자가 확인한 Action Point를 실제 업무로 등록
- **Sprint**: Task를 Sprint에 배정하여 진행 상황 관리
- **Track**: 업무 진행상태 관리

### 1-3. 핵심 가치

> **회의와 메신저에서 결정된 일을 기록하는 데서 끝나지 않고, 실제 업무로 연결한다.**

## 2. 기획 배경 및 문제 정의

### 2-1. 기존 업무관리 플랫폼의 문제

일반적인 업무관리 플랫폼에서는 프로젝트와 Task를 관리할 수 있지만, **업무가 왜 생성되었는지에 대한 회의 맥락이 분리되는 경우**가 있다.

반대로 회의록 서비스에서는 회의 내용과 결정사항을 기록할 수 있지만, 회의에서 나온 업무를 다시 업무관리 시스템에 입력해야 하는 경우가 있다.

### 2-2. 업무가 발생하는 다양한 채널

기업의 업무는 회의에서만 결정되지 않는다.

- 회의에서 "이건 김 대리가 다음 주까지 해주세요."
- 메신저에서 "그럼 이 부분은 제가 처리할게요."
- 프로젝트 채팅에서 "금요일까지 API 수정하기로 하죠."

처럼 다양한 채널에서 업무가 발생한다.

MVP에서는 Slack/Teams 등의 직접 연동 대신 **회의록 또는 메신저 대화 내용을 텍스트로 붙여 넣는 방식**으로 AI 분석을 제공한다.

### 2-3. 핵심 Pain Point

1. 회의나 메신저에서 결정된 업무가 누락될 수 있다.
2. 담당자와 기한을 다시 입력해야 한다.
3. 업무가 어떤 논의에서 발생했는지 추적하기 어렵다.
4. 회의·메신저·업무관리 도구가 분리되어 업무 흐름이 끊긴다.

### 2-4. 기존 업무 흐름

```text
회의 / 메신저
 ↓
논의 및 결정
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
 ↓
Sprint 배정
```

## 3. 타깃 사용자 및 Actor

### 3-1. 타깃 사용자

**기업 내 프로젝트 팀원**

프로젝트를 수행하면서 업무와 회의를 반복적으로 진행하는 사용자.

### 3-2. 프로젝트 팀원

#### 역할

프로젝트에 참여하여 업무를 수행하는 사용자.

#### 주요 기능

- 로그인
- 프로젝트 조회
- 업무 조회
- 회의 조회
- 회의록 조회
- 본인 업무 조회
- 업무 상태 변경
- 일정 조회

### 3-3. 프로젝트 관리자

#### 역할

프로젝트를 생성하고 팀의 업무·회의를 관리하는 사용자.

#### 주요 기능

- 프로젝트 생성/관리
- 팀원 초대
- Sprint 관리
- 회의 생성
- 참석자 관리
- AI 분석 요청
- Action Point 검토/수정
- Task 등록

> 프로젝트 관리자는 별도 계정 유형이 아니라 프로젝트별 역할(Role)로 설계할 수 있다.

### 3-4. AI 시스템

#### 역할

회의 및 메신저 텍스트를 분석하여 업무 후보를 생성하는 시스템.

#### 주요 기능

- 회의 내용 요약
- 주요 결정사항 추출
- Action Point 추출
- 담당자 후보 추출
- 마감일 후보 추출

### 3-5. 외부 메신저 — 향후

향후 Slack/Teams 등의 API와 연동하여 외부 커뮤니케이션에서 발생한 업무를 직접 가져오는 기능으로 확장할 수 있다.

## 4. 서비스 해결책

### 4-1. 서비스 적용 후 Workflow

```text
프로젝트
 ↓
회의 / 메신저 내용 입력
 ↓
AI 분석
 ↓
요약 + 결정사항 + Action Point
 ↓
사용자 검토
 ↓
[업무로 등록]
 ↓
Task 생성
 ↓
Sprint 배정
 ↓
업무 진행 관리
```

### 4-2. 핵심 차별점

> **회의와 메신저에서 흩어져 발생하는 논의와 결정사항을 하나의 업무관리 흐름 안에서 연결한다.**

## 5. 핵심 기능

### 5-1. 프로젝트 관리

업무와 회의를 프로젝트 단위로 묶는다.

#### 프로젝트 정보

- 프로젝트명
- 프로젝트 설명
- 기간
- 프로젝트 상태
- 참여자

#### 프로젝트 진입 구조

```text
프로젝트
├── Overview
├── Sprint
├── Tasks
└── Meetings
```

### 5-2. 팀원 초대

프로젝트 생성자가 **초대 링크를 생성하고 링크를 공유하여 팀원을 초대**할 수 있다.

```text
프로젝트 생성
 ↓
[팀원 초대]
 ↓
초대 링크 생성
 ↓
링크 복사 / 공유
 ↓
팀원 링크 접속
 ↓
프로젝트 참여
```

프로젝트 멤버 역할:

- `ADMIN`
- `MEMBER`

### 5-3. 업무 관리

#### Task 정보

- 업무명
- 설명
- 프로젝트
- 담당자
- 마감일
- 우선순위
- 상태
- 생성일
- 관련 회의
- Sprint

#### 상태

```text
TODO
 ↓
IN_PROGRESS
 ↓
DONE
```

### 5-4. 회의 관리

#### 회의 정보

- 회의 제목
- 회의 일시
- 참석자
- 회의 내용
- 관련 프로젝트

회의 내용은 직접 입력하거나 **회의 녹음본을 업로드하거나 회의록·메신저 대화 내용을 텍스트로 입력할 수 있다.**

### 5-5. AI 회의 브리핑

AI가 입력된 회의/대화 내용을 분석한다.

#### AI 결과

- 회의 요약
- 주요 결정사항
- Action Point
- 담당자 후보
- 마감일 후보

### 5-6. Action Point → Task

AI가 추출한 Action Point를 사용자가 확인한 후 **실제 업무(Task)로 등록**한다.

#### 중요한 원칙

AI가 업무를 **자동 확정하지 않는다.**

```text
AI 추출
 ↓
사용자 검토
 ↓
수정 / 삭제
 ↓
업무 등록
```

담당자나 기한을 확실하게 판단할 수 없는 경우 **'확인 필요' 또는 '미지정'** 상태로 제공한다.

### 5-7. 회의 ↔ 업무 연결

Task에 `meeting_id`를 연결하여 업무의 생성 맥락을 추적한다.

```text
Project
   │
   ├── Meeting
   │      │
   │      └── Action Point
   │               ↓
   │             Task
   │
   └── Task
```

Task 상세에서 관련 회의로 이동하여 **업무가 어떤 논의에서 생성되었는지** 확인할 수 있다.

### 5-8. Sprint 관리

프로젝트별 Sprint를 생성하고 Task를 배정하여 Agile 방식으로 업무를 관리한다.

#### MVP 기능

- Sprint 생성
- Sprint 기간 설정
- Sprint 상태 관리
- Task의 Sprint 배정
- Sprint별 Task 조회
- Sprint 진행률 확인

#### Sprint 상태

- `PLANNED`
- `ACTIVE`
- `COMPLETED`

#### 향후 확장

- Backlog 관리
- Story Point
- Burndown Chart
- Sprint 회고
- AI 기반 Sprint 업무 추천

## 6. 화면 구성

### 6-1. 상위 네비게이션

일로ON의 메인 네비게이션은 **메인보드 / 회의** 두 개의 상위 탭으로 단순화한다.

```text
일로ON
│
├── 🏠 메인보드
│    ├── 내가 해야 할 Task
│    ├── 내 프로젝트
│    ├── Sprint 현황
│    └── 오늘의 일정
│
└── 📝 회의
     ├── 회의 목록
     ├── 회의 생성
     └── 회의 상세
          ├── 회의 내용
          ├── AI 회의 브리핑
          ├── 결정사항
          └── Action Point
                ↓
             Task 등록
```

> **메인보드 = 업무를 관리하는 곳**  
> **회의 = 업무를 만들어내는 곳**

### 6-2. 메인보드

로그인 후 가장 먼저 진입하는 **업무 중심 화면**이다.

#### 내가 해야 할 일

- 담당 Task
- 마감일
- 우선순위
- 진행 상태
- 관련 프로젝트

#### 내 프로젝트

- 참여 중인 프로젝트
- 프로젝트 진행률
- 현재 Sprint
- 프로젝트 상태

#### 현재 Sprint

- Sprint명
- 기간
- 진행률
- TODO / IN_PROGRESS / DONE 업무 수

#### 오늘의 일정

- 오늘 마감되는 Task
- 오늘 예정된 Meeting

### 6-3. 회의

#### 회의 목록

- 회의 제목
- 회의 일시
- 관련 프로젝트
- 참석자
- Action Point 수
- AI 분석 상태

#### 회의 생성

- 회의 제목
- 회의 일시
- 참석자
- 관련 프로젝트
- 회의 내용

#### 회의 상세

```text
회의 상세
────────────────────────
신규 서비스 출시 회의
2026.09.08 14:00
참석자: 김민지 · 이준호 · 박서준

[회의 내용]
────────────────
[텍스트 입력] [녹음본 업로드]

회의 내용 입력 / 메신저 대화 붙여넣기
또는
회의 녹음 파일 업로드

             [AI 분석하기]

────────────────────────
📝 회의 브리핑

한눈에 보기
신규 서비스 출시 일정 및 개발 업무 논의

📌 결정사항
• 10월 1일 서비스 출시

⚡ Action Point

┌──────────────────────┐
│ API 명세 작성         │
│ 담당자 김민지         │
│ 기한 09/15            │
│ 우선순위 HIGH         │
│              [수정]  │
└──────────────────────┘

              [업무로 등록]
```

### 6-4. 필수 화면

1. Login
2. 메인보드
3. 회의 목록
4. 회의 생성/상세
5. Action Point 확인/수정
6. 프로젝트 상세
7. Task List / Task Detail
8. Sprint
9. Calendar

### 6-5. 프로젝트 진입

```text
메인보드
   ↓
내 프로젝트
   ↓
프로젝트 상세
   ├── Overview
   ├── Sprint
   ├── Tasks
   └── Meetings
```

## 7. 사용자 시나리오 및 UI Flow

### 7-1. Scenario A — 프로젝트 생성 및 팀원 초대

```text
로그인
 ↓
메인보드
 ↓
프로젝트 생성
 ↓
팀원 초대 링크 생성
 ↓
링크 공유
 ↓
팀원 참여
 ↓
Sprint / Task / Meeting 관리
```

### 7-2. Scenario B — 회의에서 Task 생성

```text
프로젝트 선택
 ↓
회의 생성
 ↓
회의 녹음본 업로드
 또는
회의록 / 메신저 내용 입력
 ↓
녹음본인 경우 STT 변환
 ↓
AI 분석
 ↓
회의 브리핑 확인
 ↓
Action Point 확인
 ↓
담당자 / 마감일 수정
 ↓
업무 등록
 ↓
Sprint 배정
```

### 7-3. Scenario C — 팀원 업무 수행

```text
로그인
 ↓
메인보드
 ↓
내 Task 확인
 ↓
업무 수행
 ↓
상태 변경
```

### 7-4. Scenario D — 업무 맥락 확인

```text
Task Detail
 ↓
관련 회의
 ↓
Meeting Detail
 ↓
AI Summary / 결정사항
 ↓
회의 내용 확인
```

### 7-5. 전체 UI Flow

```text
                    일로ON
                       │
              ┌────────┴────────┐
              ↓                 ↓
           메인보드             회의
              │                 │
       ┌──────┼──────┐          ├── 회의 목록
       ↓      ↓      ↓          ├── 회의 생성
     내 Task 프로젝트 Sprint      └── 회의 상세
       │      │                   ↓
       │      │              AI 회의 브리핑
       │      │                   ↓
       │      │              Action Point
       │      │                   ↓
       │      └──────────────→  Task
       │                          ↓
       └────────────────────→  Sprint
                                  ↓
                              업무 진행
```

### 핵심 서비스 흐름

> **회의에서 결정된 일 → Task → Sprint → 메인보드에서 실행·관리**

## 8. AI 기능 설계

### 8-1. AI Pipeline

```text
┌───────────────┐
│ 회의 녹음본    │
└───────┬───────┘
        ↓
       STT
        ↓
    회의 텍스트
        │
        ├──────────────┐
        ↓              ↓
 회의록 / 메신저 텍스트
        │              │
        └──────┬───────┘
               ↓
          Spring AI
               ↓
              LLM
               ↓
    ┌──────────┼──────────┐
    ↓          ↓          ↓
  Summary   Decisions  Action Points
                           ↓
                   Structured Output
                           ↓
                   Backend Validation
                           ↓
                       사용자 검토
                           ↓
                          Task
```

### 8-2. Structured Output

```json
{
  "summary": "신규 서비스 출시 일정 및 개발 업무를 논의함",
  "decisions": ["10월 1일 서비스 출시"],
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

### 8-3. 음성 회의 분석

회의 녹음본이 입력되면 STT(Speech-to-Text)를 통해 음성을 텍스트로 변환한 후 AI 분석을 수행한다.

- 녹음본 업로드
- 음성 → 텍스트 변환
- 변환된 회의 내용 저장
- AI 요약 및 결정사항 추출
- Action Point 추출
- 사용자 검토 후 Task 등록

### 8-3. 핵심 원칙

**LLM → 자연어 답변 → 사람이 다시 입력**

이 아니라

**LLM → 구조화된 Action Point → 사용자 검토 → Task**

로 연결한다.

## 9. 요구사항 정의

| ID     | 구분      | 요구사항                                                                                                | 우선순위 |
| ------ | --------- | ------------------------------------------------------------------------------------------------------- | -------- |
| REQ-01 | 회원      | 사용자는 이름, 이메일, 비밀번호를 입력하여 회원가입할 수 있어야 한다.                                   | 필수     |
| REQ-02 | 회원      | 사용자는 이메일과 비밀번호로 로그인할 수 있어야 한다.                                                   | 필수     |
| REQ-03 | 프로젝트  | 사용자는 자신이 참여하고 있는 프로젝트 목록을 조회할 수 있어야 한다.                                    | 필수     |
| REQ-04 | 프로젝트  | 프로젝트 관리자(ADMIN)는 프로젝트를 생성할 수 있어야 한다.                                              | 필수     |
| REQ-05 | 프로젝트  | 프로젝트 참여자는 프로젝트의 상세 정보를 조회할 수 있어야 한다.                                         | 필수     |
| REQ-06 | 프로젝트  | 프로젝트 관리자(ADMIN)는 프로젝트 정보를 수정하거나 삭제할 수 있어야 한다.                              | 필수     |
| REQ-07 | 멤버      | 프로젝트 관리자는 초대 링크를 생성하여 사용자를 프로젝트에 초대할 수 있어야 한다.                       | 필수     |
| REQ-08 | 멤버      | 사용자는 초대 링크를 통해 프로젝트에 참여할 수 있어야 한다.                                             | 필수     |
| REQ-09 | 업무      | 사용자는 프로젝트의 업무(Task)를 등록, 조회, 수정, 삭제할 수 있어야 한다.                               | 필수     |
| REQ-10 | 업무      | 사용자는 업무의 담당자, 마감일, 우선순위, 상태 등을 설정할 수 있어야 한다.                              | 필수     |
| REQ-11 | 업무      | 사용자는 자신에게 할당된 업무를 별도로 조회할 수 있어야 한다.                                           | 필수     |
| REQ-12 | Sprint    | 프로젝트 관리자는 Sprint를 생성하고 기간과 상태를 관리할 수 있어야 한다.                                | 필수     |
| REQ-13 | Sprint    | 사용자는 업무를 특정 Sprint에 배정하고 Sprint별 업무 목록과 진행 상황을 확인할 수 있어야 한다.          | 필수     |
| REQ-14 | 회의      | 사용자는 프로젝트별 회의를 등록하고 회의 목록 및 상세 내용을 조회할 수 있어야 한다.                     | 필수     |
| REQ-15 | 회의      | 사용자는 회의 내용을 직접 입력하거나 메신저 대화 내용을 붙여넣을 수 있어야 한다.                        | 필수     |
| REQ-16 | 회의      | 사용자는 회의 녹음 파일을 업로드하여 회의 내용을 분석할 수 있어야 한다.                                 | 필수     |
| REQ-17 | AI        | AI는 회의 텍스트를 분석하여 회의 브리핑과 주요 결정사항을 생성해야 한다.                                | 필수     |
| REQ-18 | AI        | AI는 회의 내용에서 Action Point를 추출하고 담당자 및 기한 후보를 제시해야 한다.                         | 필수     |
| REQ-19 | AI        | 사용자는 AI가 추출한 Action Point를 검토하고 수정할 수 있어야 한다.                                     | 필수     |
| REQ-20 | 업무 연계 | 사용자는 검토한 Action Point를 실제 Task로 등록할 수 있어야 한다.                                       | 필수     |
| REQ-21 | 대시보드  | 사용자는 메인보드에서 자신의 업무, 참여 프로젝트, 현재 Sprint 등의 정보를 한눈에 확인할 수 있어야 한다. | 필수     |
| REQ-22 | AI 처리   | 음성 회의 입력 시 STT를 통해 음성을 텍스트로 변환한 후 AI 분석을 수행해야 한다.                         | 필수     |

회의/메신저/음성 입력
↓
STT (음성인 경우)
↓
AI 분석
↓
회의 브리핑 · 결정사항 · Action Point
↓
사용자 검토/수정
↓
Task 등록
↓
Sprint 배정
↓
업무 진행 관리

## 10. 데이터 모델 및 ERD

### 10-1. USER

```text
user_id PK
name
email UNIQUE
password
created_at
```

- `email`은 로그인 ID로 사용
- 이메일 인증 기능은 MVP에서 제외
- 비밀번호는 암호화하여 저장

### 10-2. TEAM

```text
team_id PK
name
created_at
```

### 10-3. PROJECT

```text
project_id PK
team_id FK
name
description
start_date
end_date
status
created_at
```

### 10-4. PROJECT_MEMBER

```text
project_id FK
user_id FK
role
```

### 10-5. PROJECT_INVITE

```text
invite_id PK
project_id FK
token
created_by FK
expires_at
created_at
```

### 10-6. TASK

```text
task_id PK
project_id FK
meeting_id FK
sprint_id FK
title
description
assignee_id FK
due_date
priority
status
created_at
```

### 10-7. MEETING

```text
meeting_id PK
project_id FK
title
content
meeting_at
created_by FK
created_at
```

### 10-8. MEETING_MEMBER

```text
meeting_id FK
user_id FK
```

### 10-9. SPRINT

```text
sprint_id PK
project_id FK
name
start_date
end_date
status
created_at
```

### 10-10. ACTION_POINT — 선택

3일 프로젝트에서는 AI 분석 결과를 사용자 검토 후 TASK로 생성하는 구조로 단순화할 수 있으므로 별도 테이블은 선택사항이다.

### 10-11. 관계

```text
TEAM 1 ─── N PROJECT
PROJECT N ─── M USER       → PROJECT_MEMBER
PROJECT 1 ─── N MEETING
MEETING N ─── M USER       → MEETING_MEMBER
PROJECT 1 ─── N TASK
MEETING 1 ─── N TASK
PROJECT 1 ─── N SPRINT
SPRINT 1 ─── N TASK
PROJECT 1 ─── N PROJECT_INVITE
```

핵심은 **PROJECT가 업무·회의·Sprint를 묶고, TASK가 MEETING과 SPRINT를 참조하는 구조**이다.

## 11. REST API

**총 28개**

| Domain                  |   API 수 | 주요 역할            |
| ----------------------- | -------: | -------------------- |
| Auth                    |        2 | 회원가입 / 로그인    |
| Project                 |        5 | 프로젝트 CRUD        |
| Project Member / Invite |        3 | 팀원 초대 및 참여    |
| Task                    |        6 | 업무 CRUD + 내 업무  |
| Meeting                 |        5 | 회의 CRUD            |
| AI                      |        2 | AI 분석 및 결과 조회 |
| Sprint                  |        5 | Sprint CRUD          |
| **합계**                | **28개** |                      |

### 11-1. Auth

```http
POST /api/auth/signup
POST /api/auth/login
```

회원가입은 이메일 인증 없이 이름, 이메일, 비밀번호를 입력받아 계정을 생성한다.

- 이메일 중복 확인
- 비밀번호 암호화 후 저장
- 회원가입 완료 후 로그인 가능
- 이메일은 로그인 ID로 사용

### 11-2. Project

```http
GET    /api/projects
POST   /api/projects
GET    /api/projects/{projectId}
PUT    /api/projects/{projectId}
DELETE /api/projects/{projectId}
```

### 11-3. Project Member / Invite

```http
POST /api/projects/{projectId}/invites
GET  /api/projects/{projectId}/members
POST /api/invites/{token}/join
```

### 11-4. Task

```http
GET    /api/tasks
POST   /api/tasks
GET    /api/tasks/{taskId}
PUT    /api/tasks/{taskId}
DELETE /api/tasks/{taskId}
GET    /api/me/tasks
```

### 11-5. Meeting

```http
GET    /api/projects/{projectId}/meetings
POST   /api/projects/{projectId}/meetings
GET    /api/meetings/{meetingId}
PUT    /api/meetings/{meetingId}
DELETE /api/meetings/{meetingId}
```

### 11-6. AI

```http
POST /api/meetings/{meetingId}/analyze
GET  /api/meetings/{meetingId}/summary
```

`POST /api/meetings/{meetingId}/analyze`는 회의 녹음본 또는 텍스트를 입력으로 받아 분석한다.

- 텍스트 입력: 입력된 회의/메신저 내용을 바로 AI 분석
- 녹음본 입력: 음성 파일 → STT → 회의 텍스트 → AI 분석
- 분석 결과: Summary / Decisions / Action Points

따라서 별도의 STT API를 추가하지 않고 기존 AI 분석 API에서 음성 입력을 처리하여 **전체 API 수는 27개로 유지한다.**

### 11-7. Sprint

```http
GET    /api/projects/{projectId}/sprints
POST   /api/projects/{projectId}/sprints
GET    /api/sprints/{sprintId}
PUT    /api/sprints/{sprintId}
DELETE /api/sprints/{sprintId}
```

### 11-8. 핵심 비즈니스 API 흐름

```text
POST /api/auth/signup
      ↓
회원가입
      ↓
POST /api/auth/login
      ↓
JWT 발급
      ↓
POST /projects
      ↓
프로젝트 생성
      ↓
POST /projects/{id}/invites
      ↓
초대 링크 공유
      ↓
POST /invites/{token}/join
      ↓
팀원 참여

POST /projects/{id}/meetings
      ↓
회의 생성
      ↓
POST /meetings/{id}/analyze
      ↓
AI 분석
      ↓
Action Point
      ↓
사용자 검토
      ↓
POST /projects/{id}/tasks
      ↓
Task 생성
      ↓
sprintId 지정
      ↓
Sprint 배정
```

## 12. 시스템 아키텍처

### 12-1. MSA Architecture

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

### 12-2. 서비스 역할

#### Project Service

- 프로젝트
- 프로젝트 참여자
- 초대

#### Task Service

- Task
- 담당자
- 상태
- 마감일
- 우선순위
- Sprint

#### Meeting Service

- 회의
- 참석자
- 회의 내용

#### AI Service

- 요약
- 결정사항
- Action Point 추출

### 12-4. 기술 스택

| 기술               | 적용                          |
| ------------------ | ----------------------------- |
| Spring Boot        | Backend 서버                  |
| REST API           | Project/Task/Meeting CRUD     |
| Swagger/OpenAPI    | API 명세                      |
| JWT/JWK            | 로그인 후 인증                |
| BCrypt             | 비밀번호 암호화               |
| Spring AI          | 회의 AI 분석                  |
| Prompt Engineering | 회의 요약/Action Point 추출   |
| Structured Output  | AI 결과 JSON 구조화           |
| Advisor            | AI 요청 공통 처리             |
| MSA                | 도메인 분리                   |
| API Gateway        | 서비스 API 진입점             |
| Eureka             | Service Discovery — 선택 적용 |
| Docker             | 컨테이너화                    |
| Agile              | Sprint 및 업무 관리           |

### 핵심 기술

**STT + Spring AI + Structured Output + REST API + JWT**

MSA와 API Gateway는 서비스 구조와 확장성을 고려해 설계하며, Eureka와 Docker는 필요에 따라 선택 적용한다.

## 13. MVP 범위

### Must Have

- 회원가입
- 로그인
- 프로젝트 생성/조회
- 팀원 초대
- Task 관리
- 회의 생성/조회
- 회의록/메신저 텍스트 입력
- 회의 녹음본 업로드
- STT 기반 음성 → 텍스트 변환
- AI 요약
- Action Point 추출
- Action Point 검토/수정
- Task 등록
- Sprint 생성/배정
- My Tasks
- 회의 ↔ Task 연결

### Should Have

- Team Tasks
- Calendar
- 검색/필터
- 우선순위

### Could Have

- 마감 알림
- Slack/메일 직접 연동
- 회의 녹음 업로드
- 자동 음성 인식
- 반복 회의
- Burndown Chart

### Won't Have

- 복잡한 Jira/Notion 대체 기능
- 완전 자동 업무 생성
- AI가 담당자/마감일을 강제로 확정
- 대규모 프로젝트 관리 기능

## 14. 개발 일정

### 14-1. DAY 1 — 서비스 기획 + UI

- 과제 평가 기준 및 산출물 확인
- Target User / Actor 확정
- Pain Point 정의
- 핵심 가치 및 MVP 확정
- 요구사항 ID 작성
- Actor별 기능 정의
- User Scenario 작성
- 전체 UI Flow 작성
- Wireframe 제작
- Requirement ↔ UI 검증

### 14-2. DAY 2 — ERD + API + AI Architecture

- UI에서 필요한 데이터 추출
- Entity 확정
- ERD / PK / FK / 관계 작성
- UI ↔ ERD 검증
- REST API 작성
- Request / Response 정의
- API ↔ ERD 검증
- OpenAPI YAML 작성
- Spring AI / Prompt / Structured Output 설계
- STT → AI 분석 Pipeline 설계
- MSA / API Gateway / Eureka / JWT/JWK 설계

### 14-3. DAY 3 — 최종 보완 + 제출 + 발표

- 전체 기획서 리뷰
- Wireframe 보완
- 예외 / 오류 상황 추가
- ERD / DBML / API YAML 최종 검수
- UI ↔ API ↔ ERD ↔ AI 전체 연결 검증
- PDF / YAML / DBML 제출 파일 확인
- UI Flow 포함 여부 확인
- 최종 제출
- 5분 발표 준비

## 15. 발표 구성 — 5분

### 15-1. Problem — 40초

> 업무관리 플랫폼과 회의록이 분리되어 있어, 회의나 메신저에서 결정된 업무를 다시 Task로 등록해야 하는 불편이 있습니다.

### 15-2. Solution — 50초

> 일로ON은 프로젝트 업무관리 기능에 AI 회의 분석을 결합하여, 회의와 메신저에서 발생한 논의와 결정사항을 Action Point로 추출하고 실제 Task로 연결합니다.

### 15-3. 핵심 Flow — 1분

```text
회의 / 메신저
 ↓
AI Summary
 ↓
Action Point
 ↓
사용자 검토
 ↓
Task
 ↓
Sprint
 ↓
메인보드
```

### 15-4. AI 기술 — 1분

> Spring AI와 Structured Output을 활용해 입력된 내용을 요약하고 Action Point를 구조화된 데이터로 추출합니다. AI가 생성한 결과는 사용자가 검토한 후 Task로 등록됩니다.

### 15-5. System Design — 1분

> Project, Task, Meeting, AI 도메인을 분리하고 REST API 기반으로 연결했습니다. 향후 MSA와 Kafka를 적용하여 서비스 확장성을 고려했습니다.

### 15-6. Closing — 30초

> **“회의록을 작성하는 것에서 끝나는 것이 아니라, 회의와 메신저에서 결정된 업무가 실제 Task가 되어 Sprint에서 실행될 수 있도록 연결하는 업무관리 플랫폼입니다.”**

## 16. 최종 서비스 정의

### 서비스 정의

> **일로ON은 기업의 프로젝트 팀을 대상으로, 회의와 메신저에서 발생한 논의·결정사항을 AI로 구조화하고 실제 Task와 Sprint로 연결하여 업무 실행까지 지원하는 AI 기반 협업·업무관리 SaaS이다.**

### 핵심 문제

> **회의와 메신저에서 결정된 업무가 실제 업무관리 시스템과 분리되어 있어, 담당자·기한을 다시 입력하고 업무의 생성 맥락을 추적하기 어렵다.**

### 핵심 해결책

> **회의와 메신저 내용을 AI가 분석하여 Action Point를 추출하고, 사용자의 검토 후 실제 Task와 Sprint로 연결한다.**

### 핵심 차별화

> **논의가 발생한 곳에서 실제 업무가 실행되는 곳까지의 단절을 없앤다.**

### 핵심 Workflow

> **회의 녹음 / 회의록 / 메신저 → STT·AI 분석 → Action Point → 사용자 검토 → Task → Sprint → 메인보드**
