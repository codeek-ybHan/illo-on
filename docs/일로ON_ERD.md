# 일로ON ERD / 데이터 모델

> 기획서 §10 기반. Phase 0 산출물 — 이후 엔티티/마이그레이션은 이 문서를 기준으로 한다.
> DBML은 [dbdiagram.io](https://dbdiagram.io) 에 붙여넣어 시각화할 수 있다.

## 핵심 구조

```text
TEAM 1 ── N PROJECT
PROJECT N ── M USER     → PROJECT_MEMBER (role)
PROJECT 1 ── N PROJECT_INVITE
PROJECT 1 ── N MEETING
MEETING N ── M USER     → MEETING_MEMBER
PROJECT 1 ── N SPRINT
PROJECT 1 ── N TASK
MEETING 1 ── N TASK     (task.meeting_id, nullable — 업무 생성 맥락)
SPRINT  1 ── N TASK     (task.sprint_id, nullable — Sprint 배정)
USER    1 ── N TASK     (task.assignee_id, nullable — 담당자 미지정 허용)
```

**중심 원칙**: PROJECT가 업무·회의·Sprint를 묶고, TASK가 MEETING·SPRINT를 참조한다.

## Enum 값 (전 계층 통일 — FE/BE/DB 동일 문자열)

| Enum | 값 | 사용처 |
|---|---|---|
| `project_status` | `PLANNED` · `ACTIVE` · `COMPLETED` | PROJECT.status *(기획서 미명시 → 제안값)* |
| `member_role` | `ADMIN` · `MEMBER` | PROJECT_MEMBER.role |
| `task_priority` | `HIGH` · `MEDIUM` · `LOW` | TASK.priority |
| `task_status` | `TODO` · `IN_PROGRESS` · `DONE` | TASK.status |
| `sprint_status` | `PLANNED` · `ACTIVE` · `COMPLETED` | SPRINT.status |

> AI가 담당자·기한을 확정 못 하는 Action Point는 **DB에 넣기 전 단계**(회의 브리핑 검토 UI)에서만 "미지정"으로 표시한다. TASK로 등록될 때 `assignee_id` / `due_date` 는 그냥 `NULL` 로 저장.

## DBML

```dbml
Project illo_on {
  database_type: 'MySQL'
}

Table USER {
  user_id     bigint      [pk, increment]
  name        varchar(50) [not null]
  email       varchar(255)[not null, unique, note: '로그인 ID']
  password    varchar(255)[not null, note: 'BCrypt 해시']
  created_at  timestamp   [not null, default: `now()`]
}

Table TEAM {
  team_id     bigint      [pk, increment]
  name        varchar(100)[not null]
  created_at  timestamp   [not null, default: `now()`]
}

Table PROJECT {
  project_id  bigint      [pk, increment]
  team_id     bigint      [not null, ref: > TEAM.team_id]
  name        varchar(100)[not null]
  description text
  start_date  date
  end_date    date
  status      varchar(20) [not null, default: 'PLANNED', note: 'project_status']
  created_at  timestamp   [not null, default: `now()`]
}

Table PROJECT_MEMBER {
  project_id  bigint      [not null, ref: > PROJECT.project_id]
  user_id     bigint      [not null, ref: > USER.user_id]
  role        varchar(10) [not null, default: 'MEMBER', note: 'member_role']

  indexes {
    (project_id, user_id) [pk]
  }
}

Table PROJECT_INVITE {
  invite_id   bigint      [pk, increment]
  project_id  bigint      [not null, ref: > PROJECT.project_id]
  token       varchar(64) [not null, unique, note: '초대 링크 토큰']
  created_by  bigint      [not null, ref: > USER.user_id]
  expires_at  timestamp   [not null]
  created_at  timestamp   [not null, default: `now()`]
}

Table MEETING {
  meeting_id  bigint      [pk, increment]
  project_id  bigint      [not null, ref: > PROJECT.project_id]
  title       varchar(200)[not null]
  content     text        [note: '회의 내용 / 메신저 대화 / STT 결과']
  meeting_at  timestamp
  created_by  bigint      [not null, ref: > USER.user_id]
  created_at  timestamp   [not null, default: `now()`]
}

Table MEETING_MEMBER {
  meeting_id  bigint      [not null, ref: > MEETING.meeting_id]
  user_id     bigint      [not null, ref: > USER.user_id]

  indexes {
    (meeting_id, user_id) [pk]
  }
}

Table SPRINT {
  sprint_id   bigint      [pk, increment]
  project_id  bigint      [not null, ref: > PROJECT.project_id]
  name        varchar(100)[not null]
  start_date  date
  end_date    date
  status      varchar(20) [not null, default: 'PLANNED', note: 'sprint_status']
  created_at  timestamp   [not null, default: `now()`]
}

Table TASK {
  task_id     bigint      [pk, increment]
  project_id  bigint      [not null, ref: > PROJECT.project_id]
  meeting_id  bigint      [ref: > MEETING.meeting_id, note: 'nullable — 생성 맥락 회의']
  sprint_id   bigint      [ref: > SPRINT.sprint_id, note: 'nullable — Sprint 배정']
  assignee_id bigint      [ref: > USER.user_id, note: 'nullable — 담당자 미지정 허용']
  title       varchar(200)[not null]
  description text
  due_date    date
  priority    varchar(10) [not null, default: 'MEDIUM', note: 'task_priority']
  status      varchar(15) [not null, default: 'TODO', note: 'task_status']
  created_at  timestamp   [not null, default: `now()`]
}
```

## ACTION_POINT — 선택 (MVP 제외)

3일 범위에서는 `POST /api/meetings/{id}/analyze` 결과(JSON)를 프론트가 검토 화면에 들고 있다가
`POST /api/tasks` 로 바로 등록한다. 별도 테이블/영속화는 하지 않는다.
(회의 재분석 이력·부분 등록이 필요해지면 그때 도입)

## 마이그레이션 순서

`USER` → `TEAM` → `PROJECT` → (`PROJECT_MEMBER`, `PROJECT_INVITE`) → `MEETING` → `MEETING_MEMBER` → `SPRINT` → `TASK`

## 화면 ↔ 주요 테이블

| 화면 | 읽는 테이블 |
|---|---|
| 메인보드 | TASK(assignee=me), PROJECT_MEMBER+PROJECT, SPRINT(status=ACTIVE), MEETING(오늘) |
| 프로젝트 상세 | PROJECT, PROJECT_MEMBER, SPRINT, TASK, MEETING |
| 회의 상세 | MEETING, MEETING_MEMBER, (analyze 결과), TASK(meeting_id) |
| Task 상세 | TASK, MEETING(관련), USER(담당자) |
| Sprint | SPRINT, TASK(sprint_id) |
| 캘린더 | MEETING(meeting_at), TASK(due_date) |
