#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────
#  일로ON 공개 터널  ·  로컬 docker 스택을 인터넷에 노출 (데모용)
#
#  전제: ./deploy.sh 로 docker 스택이 떠 있어야 함 (front → 127.0.0.1:5173)
#  이 망에서 Cloudflare·ngrok 은 막혀서 SSH 터널(serveo / localhost.run)을 씀.
#
#    ./tunnel.sh          # 터널 열고 공개 URL 을 .env(APP_BASE_URL)에 반영 + backend 재기동
#    ./tunnel.sh stop     # 종료
#
#  ── 주소 종류 ─────────────────────────────────────────────
#    serveo (~/.ssh/serveo 키 사용):
#      https://illo-on.serveousercontent.com  — 고정. 단 첫 방문 시 serveo 경고 1회.
#      깔끔한 *.serveo.net + 경고 제거는 serveo Pro(유료).
#    serveo 실패 시 → localhost.run 폴백:
#      https://xxxx.lhr.life  — 경고 없음. 단 재접속 때 URL 이 바뀔 수 있음.
#
#  주의: 이 맥이 켜져 있고 안 자야 함.  권장:  caffeinate -s ./tunnel.sh
# ─────────────────────────────────────────────────────────────
set -euo pipefail
cd "$(dirname "$0")"

SUBDOMAIN="illo-on"
KEY="$HOME/.ssh/serveo"
LOCAL_PORT=5173
PIDFILE=".tunnel.pid"
LOGFILE=".tunnel.log"

stop() {
  [ -f "$PIDFILE" ] && kill "$(cat "$PIDFILE")" 2>/dev/null || true
  pkill -f "ssh.*-R .*:${LOCAL_PORT}.*(serveo\.net|localhost\.run)" 2>/dev/null || true
  rm -f "$PIDFILE"
}

[ "${1:-}" = "stop" ] && { stop; echo "터널 종료."; exit 0; }

[ -f .env ] || { echo "✗ .env 없음. ./deploy.sh 먼저."; exit 1; }
curl -sf -o /dev/null "http://127.0.0.1:${LOCAL_PORT}/" \
  || { echo "✗ 127.0.0.1:${LOCAL_PORT} 무응답. ./deploy.sh 로 스택을 먼저 띄우세요."; exit 1; }

stop; sleep 1

SSH_COMMON=(-o StrictHostKeyChecking=accept-new -o ServerAliveInterval=30
           -o ServerAliveCountMax=3 -o ExitOnForwardFailure=yes)

URL=""

# 1) serveo + 등록된 키 → 고정 주소
if [ -f "$KEY" ]; then
  echo "▶ serveo (고정 주소 시도)…"
  nohup ssh -i "$KEY" "${SSH_COMMON[@]}" \
    -R "${SUBDOMAIN}:80:127.0.0.1:${LOCAL_PORT}" serveo.net > "$LOGFILE" 2>&1 &
  echo $! > "$PIDFILE"
  for _ in $(seq 1 12); do
    sleep 2
    URL=$(grep -oaE 'https://[a-z0-9.-]+\.(serveo\.net|serveousercontent\.com)' "$LOGFILE" | head -1 || true)
    [ -n "$URL" ] && break
    if grep -qa "console.serveo.net/ssh/keys?add=" "$LOGFILE"; then
      echo
      echo "  ⚠  이 키가 아직 serveo 에 등록되지 않았습니다. 아래 링크를 열어 GitHub 로그인 → 등록:"
      grep -oaE 'https://console\.serveo\.net/ssh/keys\?add=[^ ]+' "$LOGFILE" | head -1 | sed 's/^/     /'
      echo "  등록 후 ./tunnel.sh 다시 실행하면 https://${SUBDOMAIN}.serveo.net 로 고정됩니다."
      echo "  지금은 localhost.run 임시 터널로 진행합니다…"
      echo
      stop; sleep 1; break
    fi
  done
else
  echo "  (~/.ssh/serveo 키 없음 → 고정 주소 건너뜀.  ssh-keygen -t ed25519 -f ~/.ssh/serveo -N \"\")"
fi

# 2) 폴백: localhost.run nokey (URL 이 바뀔 수 있음)
if [ -z "$URL" ]; then
  echo "▶ localhost.run 임시 터널…"
  nohup ssh "${SSH_COMMON[@]}" \
    -R "80:127.0.0.1:${LOCAL_PORT}" nokey@localhost.run > "$LOGFILE" 2>&1 &
  echo $! > "$PIDFILE"
  for _ in $(seq 1 30); do
    sleep 2
    URL=$(grep -oaE 'https://[a-z0-9-]+\.lhr\.life' "$LOGFILE" | head -1 || true)
    [ -n "$URL" ] && break
  done
fi

[ -n "$URL" ] || { echo "✗ 터널 URL 을 못 받았습니다. $LOGFILE 확인."; tail -20 "$LOGFILE"; exit 1; }

echo "▶ .env APP_BASE_URL → $URL"
sed -i.bak "s|^APP_BASE_URL=.*|APP_BASE_URL=${URL}|" .env && rm -f .env.bak
echo "▶ backend 재기동 (초대 링크에 새 주소 반영)"
docker compose up -d backend >/dev/null

echo
echo "  ┌──────────────────────────────────────────────"
echo "  │  공개 링크 :  ${URL}"
case "$URL" in
  *.serveo.net)            echo "  │  (고정 주소 · 경고 페이지 없음)" ;;
  *.serveousercontent.com) echo "  │  (고정 주소 · 첫 방문 시 serveo 경고 페이지 → 계속 클릭)" ;;
  *)                       echo "  │  (임시 주소 · 재실행하면 바뀔 수 있음)" ;;
esac
echo "  └──────────────────────────────────────────────"
echo
echo "  종료:  ./tunnel.sh stop"
