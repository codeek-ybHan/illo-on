# 일로ON (illo-on) 프로젝트 폴더 구조

## 1. 전체 프로젝트 구조

```text
illo-on/
│
├── src/                    # Vue Frontend
│   ├── views/              # 페이지 단위 화면
│   ├── components/         # 재사용 UI 컴포넌트
│   ├── api/                # Backend API 호출
│   ├── stores/             # Pinia 전역 상태 관리
│   ├── router/             # Vue Router
│   ├── assets/             # CSS, 이미지 등 정적 리소스
│   ├── App.vue
│   └── main.js
│
├── backend/                # Spring Boot Backend
├── docs/                   # 기획서 / API YAML / DBML / UI Flow
├── public/
├── package.json
├── vite.config.js
└── README.md
```

> 현재 Vue 프로젝트가 `illo-on` 루트에 생성되어 있으므로, 당장은 `src/`를 중심으로 개발하고 `backend/`, `docs/`는 필요할 때 추가한다.

---

## 2. Vue `src` 구조

```text
src/
│
├── assets/
│   └── styles/
│       ├── reset.css
│       └── main.css
│
├── components/
│   ├── common/
│   │   ├── BaseButton.vue
│   │   ├── BaseInput.vue
│   │   ├── BaseModal.vue
│   │   └── BaseCard.vue
│   │
│   ├── task/
│   │   ├── TaskCard.vue
│   │   ├── TaskList.vue
│   │   └── TaskForm.vue
│   │
│   ├── meeting/
│   │   ├── MeetingCard.vue
│   │   ├── MeetingForm.vue
│   │   ├── AudioUploader.vue
│   │   ├── AiSummary.vue
│   │   └── ActionPointCard.vue
│   │
│   ├── project/
│   │   ├── ProjectCard.vue
│   │   └── ProjectForm.vue
│   │
│   └── sprint/
│       ├── SprintCard.vue
│       └── SprintTaskList.vue
│
├── views/
│   ├── auth/
│   │   ├── LoginView.vue
│   │   └── SignupView.vue
│   ├── MainBoardView.vue
│   ├── project/
│   │   └── ProjectDetailView.vue
│   ├── meeting/
│   │   ├── MeetingListView.vue
│   │   └── MeetingDetailView.vue
│   ├── task/
│   │   └── TaskDetailView.vue
│   └── sprint/
│       └── SprintView.vue
│
├── stores/
│   ├── auth.js
│   ├── project.js
│   └── task.js
│
├── api/
│   ├── axios.js
│   ├── auth.js
│   ├── project.js
│   ├── task.js
│   ├── meeting.js
│   └── sprint.js
│
├── router/
│   └── index.js
│
├── utils/
│   ├── date.js
│   └── validation.js
│
├── App.vue
└── main.js
```

## 3. 폴더별 역할

### `views/`
사용자가 직접 진입하는 **페이지 단위 화면**.

### `components/`
페이지 안에서 재사용하는 **UI 컴포넌트**.

### `api/`
Spring Boot Backend의 **REST API 호출 코드**.

### `stores/`
Pinia를 이용한 **전역 상태 관리**.

### `router/`
Vue Router를 이용한 **페이지 이동 및 URL 관리**.

### `assets/`
CSS, 이미지, 아이콘 등 **정적 리소스**.

### `utils/`
날짜 변환, 입력값 검증 등 **공통 함수**.

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
│         ├── AudioUploader.vue
│         ├── AiSummary.vue
│         └── ActionPointCard.vue
│
├── 프로젝트
│    └── ProjectDetailView.vue
│
├── Task
│    └── TaskDetailView.vue
│
└── Sprint
     └── SprintView.vue
```

## 5. 개발 원칙

- **페이지** → `views/`
- **재사용 UI** → `components/`
- **API 호출** → `api/`
- **전역 상태** → `stores/`
- **페이지 이동** → `router/`
- **공통 함수** → `utils/`

처음부터 모든 파일을 만들 필요는 없다. 기능을 구현하면서 필요한 컴포넌트와 파일을 추가한다.
