# 일로ON (illo-on) 프로젝트 폴더 구조

## 1. 전체 프로젝트 구조

```text
illo-on/
│
├── front/                  # Vue Frontend
│   ├── src/
│   │   ├── views/          # 페이지 단위 화면
│   │   ├── components/     # 재사용 UI 컴포넌트
│   │   ├── api/            # Backend API 호출
│   │   ├── stores/         # Pinia 전역 상태 관리
│   │   ├── router/         # Vue Router
│   │   ├── layouts/        # 레이아웃(Default/Auth)
│   │   ├── assets/         # CSS, 이미지 등 정적 리소스
│   │   ├── utils/          # 공통 함수
│   │   ├── App.vue
│   │   └── main.js
│   ├── public/
│   ├── Dockerfile
│   └── package.json
│
├── backend/                 # Spring Boot Backend (com.illoon 패키지, 도메인별 구성)
├── docs/                    # 기획서 / API YAML / DBML / 운영 가이드
├── docker-compose.yml       # db · backend · front · caddy
├── deploy.sh                # 원격 서버 배포 스크립트
├── Caddyfile                # 리버스 프록시 / HTTPS
└── README.md
```

---

## 2. Vue `front/src` 구조

```text
front/src/
│
├── assets/
│   ├── brand/
│   │   └── logo-lockup.svg
│   └── styles/
│       ├── reset.css
│       └── main.css        # 디자인 토큰(색상/타이포/간격) 중앙 관리
│
├── components/
│   ├── common/              # BaseButton, BaseInput, BaseModal, BaseCard,
│   │                         # AppHeader, AppRail, SearchPalette, ToastHost 등
│   ├── task/
│   │   ├── TaskCard.vue
│   │   ├── TaskForm.vue
│   │   └── TaskList.vue
│   ├── meeting/
│   │   ├── MeetingCard.vue
│   │   ├── MeetingForm.vue   # 회의 등록 + 오디오 업로드
│   │   ├── AiBriefing.vue    # AI 요약/브리핑
│   │   └── ActionPointCard.vue
│   ├── project/
│   │   └── ProjectForm.vue
│   └── sprint/
│       ├── SprintBoard.vue
│       ├── SprintForm.vue
│       └── SprintPanel.vue
│
├── views/
│   ├── MainBoardView.vue
│   ├── CalendarView.vue
│   ├── auth/
│   │   ├── LoginView.vue
│   │   └── SignupView.vue
│   ├── project/
│   │   ├── ProjectListView.vue
│   │   ├── ProjectDetailView.vue
│   │   └── InviteJoinView.vue
│   ├── meeting/
│   │   ├── MeetingListView.vue
│   │   └── MeetingDetailView.vue
│   ├── task/
│   │   └── TaskDetailView.vue
│   └── sprint/
│       └── SprintView.vue
│
├── layouts/
│   ├── DefaultLayout.vue
│   └── AuthLayout.vue
│
├── stores/                  # auth, project, task, meeting, sprint, ai, ui
├── api/                     # axios, auth, project, task, meeting, sprint, ai
├── router/
│   └── index.js
├── utils/                   # date, validation, token, toast
│
├── App.vue
└── main.js
```

## 3. 폴더별 역할

### `views/`
사용자가 직접 진입하는 **페이지 단위 화면**.

### `components/`
페이지 안에서 재사용하는 **UI 컴포넌트**.

### `layouts/`
페이지를 감싸는 **공통 레이아웃**(로그인 전/후).

### `api/`
Spring Boot Backend의 **REST API 호출 코드**.

### `stores/`
Pinia를 이용한 **전역 상태 관리**.

### `router/`
Vue Router를 이용한 **페이지 이동 및 URL 관리**.

### `assets/`
CSS(디자인 토큰), 이미지, 아이콘 등 **정적 리소스**.

### `utils/`
날짜 변환, 입력값 검증, 토큰 처리 등 **공통 함수**.

---

## 4. 일로ON 화면과 폴더 연결

```text
일로ON
│
├── 메인보드
│    └── MainBoardView.vue
│
├── 회의
│    ├── MeetingListView.vue
│    └── MeetingDetailView.vue
│         ├── MeetingForm.vue (오디오 업로드 포함)
│         ├── AiBriefing.vue
│         └── ActionPointCard.vue
│
├── 프로젝트
│    ├── ProjectListView.vue
│    ├── ProjectDetailView.vue
│    └── InviteJoinView.vue
│
├── Task
│    └── TaskDetailView.vue
│
└── Sprint
     └── SprintView.vue
```

## 5. 백엔드 (`backend/src/main/java/com/illoon`)

도메인 패키지 기준으로 구성하며, 각 도메인 패키지 루트에 Controller/Service/Repository를,
필요 시 `domain/`(엔티티) · `dto/`(요청·응답) 서브패키지를 둔다.

```text
com/illoon/
├── auth/        # AuthController, AuthService
├── board/       # BoardController, BoardService (메인보드 집계)
├── project/     # ProjectController, InviteController, ProjectService
│   ├── domain/  # Project, ProjectMember, ProjectInvite, ...
│   └── dto/
├── meeting/     # MeetingController, MeetingService, MeetingRepository, ...
│   ├── domain/
│   └── dto/
├── sprint/      # SprintController, SprintService, SprintRepository
│   ├── domain/
│   └── dto/
├── task/        # TaskController, TaskService, TaskRepository
│   ├── domain/
│   └── dto/
├── team/        # Team, TeamRepository
├── user/        # User, UserRepository
│   └── dto/
├── ai/          # AI 분석/브리핑 (analyzer, stt, domain, dto)
└── common/      # 설정, 공통 엔티티, 보안, 예외 처리
```

## 6. 개발 원칙

- **페이지** → `views/`
- **재사용 UI** → `components/`
- **API 호출** → `api/`
- **전역 상태** → `stores/`
- **페이지 이동** → `router/`
- **공통 함수** → `utils/`

처음부터 모든 파일을 만들 필요는 없다. 기능을 구현하면서 필요한 컴포넌트와 파일을 추가한다.
