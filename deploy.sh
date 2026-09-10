#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────
#  일로ON 배포 스크립트  ·  원격 서버에서 실행
#
#  최초 1회 (서버에서):
#    git clone https://github.com/codeek-ybHan/illo-on.git
#    cd illo-on
#    cp .env.example .env      # 그리고 값을 채운다 (APP_BASE_URL, JWT_SECRET, ...)
#    ./deploy.sh
#
#  이후 갱신:
#    ./deploy.sh               # git pull + 재빌드 + 기동
#
#  옵션:
#    ./deploy.sh --no-pull     # git pull 생략, 현재 코드로 빌드
#    ./deploy.sh --logs        # 기동 후 로그 따라가기
#    ./deploy.sh down          # 스택 중지 (DB 볼륨은 유지)
# ─────────────────────────────────────────────────────────────
set -euo pipefail
cd "$(dirname "$0")"

RED=$'\e[31m'; YEL=$'\e[33m'; GRN=$'\e[32m'; DIM=$'\e[2m'; NC=$'\e[0m'
info() { echo "${DIM}▶${NC} $*"; }
warn() { echo "${YEL}⚠${NC}  $*"; }
die()  { echo "${RED}✗${NC} $*" >&2; exit 1; }

# ── down 서브커맨드 ──
if [ "${1:-}" = "down" ]; then
  info "docker compose down (DB 볼륨은 유지)"
  docker compose down
  echo "${GRN}중지 완료.${NC} DB 데이터는 illoon-db-data 볼륨에 남아 있습니다."
  exit 0
fi

PULL=1; FOLLOW_LOGS=0
for arg in "$@"; do
  case "$arg" in
    --no-pull) PULL=0 ;;
    --logs)    FOLLOW_LOGS=1 ;;
    *) die "알 수 없는 옵션: $arg" ;;
  esac
done

# ── 0. 사전 점검 ──
command -v docker >/dev/null || die "docker 가 설치돼 있지 않습니다."
docker compose version >/dev/null 2>&1 || die "docker compose v2 가 필요합니다 (docker compose version)."
docker info >/dev/null 2>&1 || die "docker 데몬에 접근할 수 없습니다. (sudo 없이 실행하려면 사용자를 docker 그룹에 추가)"

[ -f .env ] || die ".env 가 없습니다.  cp .env.example .env  후 값을 채우세요."

# .env 값 검증
# shellcheck disable=SC1091
set -a; . ./.env; set +a
: "${APP_BASE_URL:?}" "${JWT_SECRET:?}"
case "$JWT_SECRET" in
  *change-me*|*________*) die ".env 의 JWT_SECRET 이 예시값입니다.  openssl rand -base64 48  로 교체하세요." ;;
esac
case "$APP_BASE_URL" in
  *localhost*|*127.0.0.1*) warn "APP_BASE_URL 이 $APP_BASE_URL — 외부 사용자는 이 초대 링크로 접속 못 합니다. 서버 공개 주소로 바꾸세요." ;;
esac
[ "${AI_PROVIDER:-mock}" = "openai" ] && [ -z "${OPENAI_API_KEY:-}" ] && \
  warn "AI_PROVIDER=openai 인데 OPENAI_API_KEY 가 비어 있습니다. 규칙 기반으로 동작합니다."

# ── 1. 코드 갱신 ──
if [ "$PULL" = 1 ] && [ -d .git ]; then
  info "git pull --ff-only"
  git pull --ff-only || die "git pull 실패. 서버에 로컬 변경이 있는지 확인하세요."
  info "현재 커밋: $(git rev-parse --short HEAD) $(git log -1 --pretty=%s)"
fi

# ── 2. 빌드 & 기동 ──
info "docker compose up -d --build (최초 빌드는 3~5분)"
docker compose up -d --build --remove-orphans

# ── 3. 헬스 체크 ──
FRONT_PORT="$(docker compose port front 80 2>/dev/null | sed 's/.*://' || true)"
FRONT_PORT="${FRONT_PORT:-5173}"
BASE="http://127.0.0.1:${FRONT_PORT}"

info "헬스 체크 ($BASE)"
ok=0
for i in $(seq 1 40); do
  front=$(curl -s -o /dev/null -w '%{http_code}' "$BASE/" || true)
  # 인증 없이 보호 API → 401 이면 백엔드가 nginx 프록시 통해 정상 응답
  api=$(curl -s -o /dev/null -w '%{http_code}' "$BASE/api/me/board" || true)
  if [ "$front" = "200" ] && [ "$api" = "401" ]; then ok=1; break; fi
  sleep 3
done

echo
if [ "$ok" = 1 ]; then
  echo "${GRN}✓ 배포 완료${NC}"
  echo "  서버 내부 확인 : $BASE"
  echo "  외부 접속 링크 : ${APP_BASE_URL}"
  echo "  ${DIM}(방화벽/보안그룹에서 ${FRONT_PORT} 포트 인바운드 허용 필요)${NC}"
else
  echo "${RED}✗ 헬스 체크 실패${NC} (front=$front, api=$api)"
  echo "  로그 확인: docker compose logs --tail=100 backend"
  docker compose ps
  exit 1
fi

docker compose ps
[ "$FOLLOW_LOGS" = 1 ] && exec docker compose logs -f