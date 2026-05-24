# Casino Telegram Mini App

Монолитный backend (Java/Spring Boot) + frontend (React/Vite).

## Быстрый старт (локально)

### 1. Backend

```powershell
cd backend
gradle :app:bootRun
```

API: http://localhost:8080  
Профиль `dev`: H2 in-memory, автосид данных (карты, паки, квесты, событие).

### 2. User App (Telegram Mini App)

```powershell
cd frontend/user-app
npm install
npm run dev
```

Откройте http://localhost:5173 — в dev-режиме авторизация через stub `initData` (`dev-player-login-token-2026`).

### 3. Admin Panel

```powershell
cd frontend/admin-panel
npm install
npm run dev
```

http://localhost:5174 — вход через Telegram auth; доступ только если ваш **username** в `CASINO_ADMIN_ALLOWED_USERNAMES`.

## Структура frontend

| Папка | Назначение |
|-------|------------|
| `frontend/user-app` | Mini App: казино, паки, коллекция, трейды, ставки (квесты — в боте) |
| `frontend/admin-panel` | Админка (RU): пользователи, карточки, баланс, квесты, события |

Vite проксирует `/api` → `http://localhost:8080`.

## Telegram

В BotFather укажите Web App URL на хостинг `user-app` (HTTPS).  
В production задайте `casino.telegram.bot-token` и реализуйте полную проверку `initData`.

## Переменные окружения (опционально)

`frontend/user-app/.env`:

```
VITE_API_URL=
VITE_DEV_INIT_DATA=dev-player-login-token-2026
```

Пустой `VITE_API_URL` — запросы через Vite proxy (только локально).

### Production (Vercel)

Фронт: [telegram-mini-app-4njn.vercel.app](https://telegram-mini-app-4njn.vercel.app/)

В **Vercel → Settings → Environment Variables** добавьте:

| Variable | Value |
|----------|--------|
| `VITE_API_URL` | URL **бэкенда** (Spring), **не** `telegram-mini-app-4njn.vercel.app` |

**Частая ошибка:** `VITE_API_URL` = URL фронта на Vercel → запросы идут на `vercel.app/api/...`, CORS/404.

CORS на бэкенде: `application.yml` → `casino.cors` (маска `https://telegram-mini-app-4njn*.vercel.app` для production и preview).

### Backend (Docker / production)

| Variable | Описание |
|----------|----------|
| `CASINO_ADMIN_ALLOWED_USERNAMES` | Telegram usernames через запятую (без @), кому доступна `/api/admin/**` |
| `CASINO_JWT_SECRET` | Секрет JWT |
| `TELEGRAM_BOT_TOKEN` | Токен бота для проверки initData |

### Admin panel

```
VITE_API_URL=https://your-backend.example.com
VITE_ADMIN_INIT_DATA=dev-admin-login-token-2026
```

### Квесты в боте (`quest-bot-app`)

Отдельное Spring Boot приложение в `backend/quest-bot-app` (порт **8081**).

```powershell
cd backend
gradle :quest-bot-app:bootRun
```

| Variable | Описание |
|----------|----------|
| `QUEST_BOT_TOKEN` | Токен Telegram-бота квестов |
| `QUEST_BOT_USERNAME` | Username бота (без @) |
| `QUEST_BOT_ADMIN_IDS` | Telegram ID админов через запятую |
| `QUEST_BOT_ADMIN_USERNAMES` | Usernames админов (по умолчанию `admin`) |
| `CASINO_API_URL` | URL casino backend (по умолчанию `http://localhost:8080`) |
| `CASINO_INTERNAL_API_KEY` | Ключ internal API (тот же, что `casino.internal.api-key`) |

Пользователь идентифицируется по **Telegram username** (обязателен). При `/start` бот регистрирует пользователя в Casino через internal API.  
После одобрения квеста админом обоим начисляются коины и карточки (1 квест — common, 7 — rare, 15 — legendary между одной парой).

В Mini App страница «Квесты» ведёт в бота: `VITE_QUESTS_BOT_URL=https://t.me/your_bot`.

### Идентификация пользователей

Уникальный ключ — **Telegram username** (без @). Dev-токены: `dev-admin-login-token-2026` → `admin`, `dev-player-login-token-2026` → `player`.
