#!/bin/bash
# 일로ON 전체 시나리오 E2E (A~D). backend 실행 중 상태에서 실행.
#   bash backend/scripts/e2e.sh
B="${BASE:-http://localhost:8080}"
pass=0; fail=0
S=$(date +%s)

j()   { python3 -c "import sys,json
try: print(json.load(sys.stdin)$1)
except Exception: print('')"; }
chk() { if [ "$2" = "$3" ]; then echo "  ✓ $1"; pass=$((pass+1)); else echo "  ✗ $1  (기대 $3, 실제 $2)"; fail=$((fail+1)); fi; }
BODYF=$(mktemp); SCF=$(mktemp)
# req METHOD PATH [BODY] [TOKEN] → stdout=body, 전역 SC + SCF 파일에 http status
# ($(req ...) 는 서브셸이라 전역 SC 가 안 남으므로 rc()(=SCF) 로 직전 상태코드를 읽는다.
req() {
  local m="$1" p="$2" body="$3" tok="$4"
  printf '%s' "$body" > "$BODYF"
  local hdr=()
  [ -n "$tok" ] && hdr=(-H "Authorization: Bearer $tok")
  local out
  out=$(curl -s -w '\n%{http_code}' -X "$m" "$B$p" \
        -H 'Content-Type: application/json' --data-binary "@$BODYF" "${hdr[@]}")
  SC="${out##*$'\n'}"
  printf '%s' "$SC" > "$SCF"
  printf '%s' "${out%$'\n'*}"
}
rc() { cat "$SCF"; }

echo "── Scenario A: 회원가입 → 로그인 → 프로젝트 → 초대 → 참여 ──"
ADM="adm_$S@illoon.com"; MEM="mem_$S@illoon.com"
req POST /api/auth/signup "{\"name\":\"관리자\",\"email\":\"$ADM\",\"password\":\"password1\"}" >/dev/null; chk "회원가입(admin)" "$SC" 201
req POST /api/auth/signup "{\"name\":\"팀원\",\"email\":\"$MEM\",\"password\":\"password1\"}"  >/dev/null; chk "회원가입(member)" "$SC" 201
req POST /api/auth/signup "{\"name\":\"x\",\"email\":\"$ADM\",\"password\":\"password1\"}"      >/dev/null; chk "이메일 중복 409" "$SC" 409
req POST /api/auth/login  "{\"email\":\"$ADM\",\"password\":\"nope1234\"}"                      >/dev/null; chk "잘못된 비밀번호 401" "$SC" 401
TA=$(req POST /api/auth/login "{\"email\":\"$ADM\",\"password\":\"password1\"}" | j "['token']")
TM=$(req POST /api/auth/login "{\"email\":\"$MEM\",\"password\":\"password1\"}" | j "['token']")
chk "로그인 → JWT" "$([ ${#TA} -gt 20 ] && echo ok)" ok

PID=$(req POST /api/projects '{"name":"신규 서비스 출시","status":"ACTIVE"}' "$TA" | j "['projectId']")
chk "프로젝트 생성 201" "$(rc)" 201
req GET "/api/projects/$PID" "" "$TM" >/dev/null; chk "비멤버 조회 403" "$SC" 403
req POST "/api/projects/$PID/invites" "" "$TM" >/dev/null; chk "비-ADMIN 초대 403" "$SC" 403
INV=$(req POST "/api/projects/$PID/invites" "" "$TA" | j "['token']")
req POST "/api/invites/$INV/join" "" "$TM" >/dev/null; chk "초대 참여 200" "$SC" 200
req POST "/api/invites/$INV/join" "" "$TM" >/dev/null; chk "재참여 409" "$SC" 409
MEMBERS=$(req GET "/api/projects/$PID/members" "" "$TA")
chk "멤버 2명" "$(printf '%s' "$MEMBERS" | j '.__len__()')" 2
AID=$(printf '%s' "$MEMBERS" | j "[0]['userId']")
MID=$(printf '%s' "$MEMBERS" | python3 -c "import sys,json;print([m['userId'] for m in json.load(sys.stdin) if m['role']=='MEMBER'][0])")

echo "── Scenario B: 회의 → AI 분석 → Action Point → Task → Sprint ──"
CONTENT='이번 회의는 서비스 출시 준비를 위한 것입니다. 논의 결과 10월 1일 정식 출시하기로 결정했습니다. API 명세는 관리자가 다음 주 금요일까지 작성하기로 했습니다. QA 테스트는 팀원이 긴급히 준비해야 합니다.'
MTG=$(req POST "/api/projects/$PID/meetings" "{\"title\":\"출시 킥오프\",\"meetingAt\":\"2026-10-01T14:00:00\",\"attendeeIds\":[$AID,$MID],\"content\":\"$CONTENT\"}" "$TA" | j "['meetingId']")
chk "회의 생성 201" "$(rc)" 201
req POST "/api/projects/$PID/meetings" '{"title":"x","attendeeIds":[999999]}' "$TA" >/dev/null; chk "비멤버 참석자 400" "$SC" 400
BRIEF=$(req POST "/api/meetings/$MTG/analyze" "" "$TA")
chk "AI 분석 200" "$(rc)" 200
AP_CNT=$(printf '%s' "$BRIEF" | j "['actionPoints'].__len__()")
chk "Action Point ≥ 1" "$([ "${AP_CNT:-0}" -ge 1 ] && echo ok)" ok
req GET "/api/meetings/$MTG/summary" "" "$TA" >/dev/null; chk "브리핑 재조회 200" "$SC" 200
chk "회의 hasSummary" "$(req GET "/api/meetings/$MTG" "" "$TA" | j "['hasSummary']")" True
AP_TITLE=$(printf '%s' "$BRIEF" | j "['actionPoints'][0]['title']")
TASK=$(req POST /api/tasks "{\"projectId\":$PID,\"meetingId\":$MTG,\"title\":\"$AP_TITLE\",\"assigneeId\":$MID,\"dueDate\":\"2026-09-20T18:00\",\"priority\":\"HIGH\"}" "$TA" | j "['taskId']")
chk "Action Point → Task 201" "$(rc)" 201
chk "회의 taskCount 1" "$(req GET "/api/meetings/$MTG" "" "$TA" | j "['taskCount']")" 1
SPR=$(req POST "/api/projects/$PID/sprints" '{"name":"Sprint 1","endDate":"2026-09-30"}' "$TA" | j "['sprintId']")
req PUT "/api/tasks/$TASK" "{\"title\":\"$AP_TITLE\",\"assigneeId\":$MID,\"dueDate\":\"2026-09-20T18:00\",\"priority\":\"HIGH\",\"status\":\"TODO\",\"sprintId\":$SPR,\"meetingId\":$MTG}" "$TA" >/dev/null
chk "Sprint 배정" "$(req GET "/api/tasks/$TASK" "" "$TA" | j "['sprintId']")" "$SPR"

echo "── Scenario C: 팀원 → 내 Task → 상태 변경 ──"
chk "me/tasks 스코프" "$(req GET /api/me/tasks "" "$TM" | j '.__len__()')" 1
req PUT "/api/tasks/$TASK" "{\"title\":\"$AP_TITLE\",\"assigneeId\":$MID,\"priority\":\"HIGH\",\"status\":\"IN_PROGRESS\",\"sprintId\":$SPR,\"meetingId\":$MTG}" "$TM" >/dev/null
chk "상태 변경 → IN_PROGRESS" "$(req GET "/api/tasks/$TASK" "" "$TM" | j "['status']")" IN_PROGRESS

echo "── Scenario D: Task → 관련 회의 역이동 ──"
chk "Task.meetingId 연결" "$(req GET "/api/tasks/$TASK" "" "$TM" | j "['meetingId']")" "$MTG"
req GET "/api/meetings/$MTG" "" "$TM" >/dev/null; chk "회의 상세 접근 200" "$SC" 200

echo "── 메인보드 / 캘린더 집계 ──"
req GET /api/me/board "" "$TA" >/dev/null; chk "me/board 200" "$SC" 200
req GET "/api/me/meetings?from=2026-10-01&to=2026-10-01" "" "$TA" >/dev/null; chk "me/meetings 200" "$SC" 200
chk "board.projects ≥ 1" "$([ "$(req GET /api/me/board "" "$TA" | j "['projects'].__len__()")" -ge 1 ] && echo ok)" ok

echo
echo "═══  PASS $pass  /  FAIL $fail  ═══"
[ "$fail" -eq 0 ]
