# 일로ON 서버 운영 가이드

> 대상: `168.138.55.229` (Oracle Cloud, Ubuntu 22.04) · `~/illo-on` · 도메인 `illo-on.site` (Cloudflare DNS-only → Caddy가 TLS 종료)
> 배포 방식: `docker compose` (db · backend · front · caddy) + `./deploy.sh`. 자세한 스택 구성은 `README.md` "Docker / 배포" 참고.

## 이미 자동으로 되는 것 (신경 안 써도 됨)

| 항목 | 방식 |
|---|---|
| OS 보안 패치 | `unattended-upgrades` 활성화 — 매일 자동 적용 |
| HTTPS 인증서 | Caddy가 Let's Encrypt로 자동 발급·갱신 (`Caddyfile`) |
| DB 백업 | 매일 04:10(서버 시각) `~/illo-on-backup.sh` cron → `~/illo-on-backups/illo_on-YYYYMMDD-HHMMSS.sql.gz`, **7일치만 보관** |
| 컨테이너 로그 용량 | `docker-compose.yml`의 `x-logging` — 컨테이너당 최대 30MB(10MB × 3롤), 무한 증가 안 함 |
| 컨테이너 재시작 | 전 서비스 `restart: unless-stopped` — VM 재부팅해도 자동 기동 |

## 정기 점검 (권장 주기)

### 매주 — 5분
```sh
ssh -i <키파일> ubuntu@168.138.55.229
cd ~/illo-on
docker compose ps                 # 4개 다 Up/healthy 인지
docker system df                  # 빌드 캐시 쌓였으면: docker builder prune -af
df -h /                           # 디스크 여유 확인
free -h                           # 메모리(1GB라 타이트함) · swap 사용량 확인
```

### 매월 — 10분
```sh
sudo apt list --upgradable        # 자동 패치 안 되는 것(커널 등) 확인
sudo reboot                       # 커널 업데이트 반영하려면 (컨테이너는 자동 재기동됨)
ls -la ~/illo-on-backups          # 백업이 실제로 쌓이고 있는지, 최신 파일 날짜 확인
cat ~/illo-on-backup.log          # 백업 스크립트 에러 없었는지
```

### 배포/코드 업데이트할 때
```sh
ssh -i <키파일> ubuntu@168.138.55.229
cd ~/illo-on
./deploy.sh                       # git pull → build → 재기동 → 헬스체크까지 한 번에
# 코드는 그대로 두고 .env 값만 바꿨다면:
./deploy.sh --no-pull
```
- `backend`/`front` 코드가 안 바뀌었으면 Docker 빌드 캐시 덕분에 빠르게 끝남.
- `backend` 컨테이너만 재생성되는 게 보통 — `front`/`db`/`caddy`는 안 건드려서 다운타임 최소.

## 장애 대응

### 사이트가 안 열릴 때
```sh
curl -I https://illo-on.site                     # 로컬에서 먼저 확인
ssh ... 'cd ~/illo-on && docker compose ps'       # 어떤 컨테이너가 죽었는지
docker compose logs --tail=100 <서비스명>          # db|backend|front|caddy
docker compose up -d                              # 죽은 것만 재기동 시도
```

### AI 분석이 계속 규칙 기반(mock)처럼 나올 때
`provider` 필드만으로는 확신 불가(폴백이어도 "openai"로 표기됨). 로그로 확인:
```sh
docker compose logs backend --since 1h | grep -iE 'falling back|OpenAI analyze failed|Whisper STT failed'
```
뭔가 잡히면 OpenAI 크레딧/쿼터 확인 (`docs/*` 메모리 참고 — 과거에 크레딧 소진으로 429 났던 이력 있음).

### 메모리 부족(OOM) 의심될 때
```sh
free -h                                           # available이 계속 낮으면 위험 신호
dmesg | grep -i 'out of memory'                   # OOM killer 발동 기록
docker compose ps                                 # 재시작 반복(restarting)되는 컨테이너 있는지
```
1GB RAM 인스턴스라 여유가 별로 없음(스왑 상시 사용 중). 계속 문제되면 인스턴스 스펙업 고려.

### 데이터 복구가 필요할 때
```sh
# 최신 백업 확인
ls -t ~/illo-on-backups/*.sql.gz | head -1

# 복구 (주의: 현재 DB를 덮어씀)
cd ~/illo-on
set -a; . ./.env; set +a
gunzip -c ~/illo-on-backups/illo_on-<날짜>.sql.gz | \
  docker compose exec -T db mysql -uroot -p"$DB_ROOT_PASSWORD" illo_on
```

## 보안 체크리스트

- SSH 키(`ssh-key-2026-09-10.key`) 분실/유출 시 즉시 OCI 콘솔에서 인스턴스 SSH 키 교체
- `.env`(JWT_SECRET·DB_PASSWORD·OPENAI_API_KEY)는 절대 git에 커밋 금지 — `.gitignore`에 이미 포함됨, 재확인만
- iptables 80/443/22 외 포트는 기본 REJECT (신규 포트 열 때만 룰 추가 필요)
- `backend`는 `expose`만 하고 호스트에 직접 안 열려 있음 — Swagger 등 외부 노출 금지 유지

## 알려진 제약 (당장은 괜찮지만 인지하고 있을 것)

- **단일 VM** — 이 서버가 죽으면 서비스 전체 중단(DB 포함). 백업은 있지만 즉시 페일오버는 없음.
- **1GB RAM** — 트래픽이 늘면(동시 사용자 수십 명 이상) 버벅이거나 OOM 가능. 인스턴스 업그레이드가 가장 쉬운 해결책.
- **백업이 같은 서버 안에 있음** — VM 자체가 통째로 사라지면(디스크 손상 등) 백업도 같이 날아감. 정말 중요해지면 `~/illo-on-backups`를 주기적으로 로컬/S3 등 외부로 복사하는 것 권장 (`rclone`/`scp` cron 추가 가능).
