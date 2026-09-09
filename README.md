# 일로ON (illo-on)

업무관리 플랫폼 + AI 회의록 서비스.
회의·메신저에서 논의된 내용을 AI로 분석해 Action Point를 추출하고, 사용자 검토를 거쳐 실제 Task·Sprint로 연결한다.

## 저장소 구조

```text
illo-on/
├── front/     # Vue 3 + Vite 프론트엔드
├── backend/   # Spring Boot 백엔드 (MSA)
└── docs/      # 기획서 / API YAML / DBML / UI Flow
```

## 프론트엔드 실행

```sh
cd front
npm install
npm run dev
```

빌드는 `npm run build`, 린트는 `npm run lint`. 자세한 내용은 `front/README.md` 참고.

## 문서

- `docs/일로ON_기획서_최종.md` — 서비스 기획서
- `docs/일로ON_프로젝트_폴더구조.md` — 프론트엔드 폴더 구조 설계
