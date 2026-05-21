# Casino backend (modular monolith)

## Run

Requires JDK 21 and Gradle on `PATH`:

```powershell
cd backend
gradle :app:bootRun
```

Default profile `dev` uses in-memory H2 (`application.yml`).

## Docker

Сборка использует образ `gradle:8.14-jdk21-alpine` (не нужен `gradle-wrapper.jar` в репозитории).

```powershell
cd backend
docker build -t casino-backend .
docker run --rm -p 8080:8080 --env-file .env casino-backend
```

Контекст сборки — папка `backend/` (не корень монорепо).

Card images are stored under `/app/data/storage` (volume-friendly). Override with `CASINO_STORAGE_LOCAL_DIR`.

Copy `.env.example` to `.env` and set `DB_*`, `TELEGRAM_BOT_TOKEN`, `CASINO_JWT_SECRET`.

## API overview (prefix `/api`)

| Area | Endpoints |
|------|-----------|
| Auth | `POST /auth/telegram` |
| Users | `GET /users/me` |
| Economy | `GET /economy/history` |
| Cards | `GET /cards`, `GET /cards/inventory`, `GET /cards/collection-progress` |
| Packs | `GET /packs`, `POST /packs/open`, `GET /packs/history` |
| Casino | `POST /casino/slots/spin`, `GET /casino/slots/history`, `POST /casino/roulette/bet`, `GET /casino/roulette/history` |
| Quests | `GET /quests`, `POST /quests/submit`, `GET /quests/my-submissions` |
| Betting | `GET /betting/events`, `POST /betting/place`, `GET /betting/history` |
| Trades | `POST /trades`, `POST /trades/{id}/items`, `POST /trades/{id}/send`, `POST /trades/{id}/accept`, `POST /trades/{id}/reject`, `GET /trades/history` |
| Notifications | `GET /notifications`, `POST /notifications/read/{id}` |
| Admin | `GET /admin/stats`, `POST /admin/economy/add`, `POST /admin/economy/remove`, `POST /admin/quests`, approve/reject submissions, `POST /admin/betting/events`, `.../close`, `.../settle` |

Admin routes require JWT with role `ADMIN`.

## Modules

Gradle projects under `modules/`: `common`, `config`, `economy`, `users`, `auth`, `cards`, `packs`, `casino`, `quests`, `betting`, `trades`, `admin`, `notifications`, `storage`, `websocket`, assembled by `app`.
