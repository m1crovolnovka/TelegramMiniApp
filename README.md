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

Админка встроена в **user-app** (`/admin`) для пользователей с ролью admin. Отдельная `admin-panel` опциональна.

```
VITE_API_URL=https://your-backend.example.com
```

### Квесты в Telegram-боте (`modules/quests`)

Бот встроен в основной backend (`gradle :app:bootRun`). Отдельный `quest-bot-app` больше не нужен.

| Variable | Описание |
|----------|----------|
| `QUEST_BOT_TOKEN` | Токен Telegram-бота квестов (обязателен для запуска бота) |
| `QUEST_BOT_USERNAME` | Username бота (без @) |
| `QUEST_BOT_ADMIN_IDS` | Telegram ID админов через запятую |
| `QUEST_BOT_ADMIN_USERNAMES` | Usernames админов бота (по умолчанию `admin`) |
| `CASINO_MINI_APP_URL` | URL Mini App для кнопки в боте |

Пользователь идентифицируется по **Telegram username**. При `/start` создаётся запись в Casino.  
Шаблоны квестов — в админке Mini App (`/admin/quests`) или в самом боте (админ-панель).  
После одобрения квеста обоим начисляются коины и карточки (1 / 7 / 15 квестов между парой).

В Mini App: `VITE_QUESTS_BOT_URL` и `VITE_QUESTS_BOT_USERNAME`.

### Идентификация пользователей

Уникальный ключ — **Telegram username** (без @). Dev-токены: `dev-admin-login-token-2026` → `admin`, `dev-player-login-token-2026` → `player`.
