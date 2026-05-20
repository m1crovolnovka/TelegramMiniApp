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

http://localhost:5174 — вход с `dev-admin-login-token-2026` (роль ADMIN).

## Структура frontend

| Папка | Назначение |
|-------|------------|
| `frontend/user-app` | Игровое приложение: главная, магазин, казино, инвентарь, квесты, ставки, трейды |
| `frontend/admin-panel` | Админка: статистика, экономика, квесты, события |

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

Пустой `VITE_API_URL` — запросы через Vite proxy.
