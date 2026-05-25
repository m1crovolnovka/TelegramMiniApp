import { api } from './client';
import type {
  AdminCard,
  AdminTransaction,
  AdminUser,
  BettingEvent,
  CardDefinition,
  CollectionProgress,
  Inventory,
  LeaderboardEntry,
  Notification,
  OpenPackResponse,
  Pack,
  PackHistoryItem,
  PublicUser,
  Quest,
  QuestSubmission,
  RouletteResult,
  TokenResponse,
  Trade,
  Transaction,
  User,
} from './types';

export const authApi = {
  telegram: (initData: string) =>
    api.post<TokenResponse>('/api/auth/telegram', { initData }).then((r) => r.data),
};

export const userApi = {
  me: () => api.get<User>('/api/users/me').then((r) => r.data),
  isAdmin: () => api.get<{ admin: boolean }>('/api/users/me/is-admin').then((r) => r.data.admin),
  leaderboard: (limit = 50) =>
    api.get<LeaderboardEntry[]>('/api/users/leaderboard', { params: { limit } }).then((r) => r.data),
  byId: (id: number) => api.get<PublicUser>(`/api/users/${id}`).then((r) => r.data),
  byUsername: (username: string) =>
    api.get<PublicUser>(`/api/users/by-username/${encodeURIComponent(username)}`).then((r) => r.data),
  inventory: (id: number) => api.get<Inventory>(`/api/users/${id}/inventory`).then((r) => r.data),
};

export const cardsApi = {
  catalog: () => api.get<CardDefinition[]>('/api/cards').then((r) => r.data),
  inventory: () => api.get<Inventory>('/api/cards/inventory').then((r) => r.data),
  collectionProgress: () =>
    api.get<CollectionProgress>('/api/cards/collection-progress').then((r) => r.data),
};

export const packsApi = {
  list: () => api.get<Pack[]>('/api/packs').then((r) => r.data),
  open: (packId: number, idempotencyKey?: string) =>
    api.post<OpenPackResponse>('/api/packs/open', { packId, idempotencyKey }).then((r) => r.data),
  history: () => api.get<PackHistoryItem[]>('/api/packs/history').then((r) => r.data),
};

export const casinoApi = {
  spin: (bet: number, variant: string) =>
    api
      .post<{ payout: number; variant: string; symbols: string[] }>('/api/casino/slots/spin', {
        bet,
        variant,
      })
      .then((r) => r.data),
  slotsHistory: () => api.get('/api/casino/slots/history').then((r) => r.data),
  rouletteBet: (body: { betType: string; numberValue?: number; stake: number }) =>
    api.post<RouletteResult>('/api/casino/roulette/bet', body).then((r) => r.data),
  rouletteHistory: () => api.get('/api/casino/roulette/history').then((r) => r.data),
};

export const questsApi = {
  list: () => api.get<Quest[]>('/api/quests').then((r) => r.data),
  submit: (questId: number, proofText: string) =>
    api.post<QuestSubmission>('/api/quests/submit', { questId, proofText }).then((r) => r.data),
  mySubmissions: () => api.get<QuestSubmission[]>('/api/quests/my-submissions').then((r) => r.data),
};

export const bettingApi = {
  events: () => api.get<BettingEvent[]>('/api/betting/events').then((r) => r.data),
  place: (optionId: number, stakeCoins: number) =>
    api.post<number>('/api/betting/place', { optionId, stakeCoins }).then((r) => r.data),
  history: () => api.get('/api/betting/history').then((r) => r.data),
};

export const tradesApi = {
  create: (partnerUsername: string) => api.post<Trade>('/api/trades', { partnerUsername }).then((r) => r.data),
  addCard: (tradeId: number, cardDefinitionId: number, quantity = 1, fromUserId?: number) =>
    api.post<Trade>(`/api/trades/${tradeId}/items`, { cardDefinitionId, quantity, ...(fromUserId != null ? { fromUserId } : {}) }).then((r) => r.data),
  addCoins: (tradeId: number, coinsAmount: number) =>
    api.post<Trade>(`/api/trades/${tradeId}/items`, { coinsAmount }).then((r) => r.data),
  send: (tradeId: number) => api.post<Trade>(`/api/trades/${tradeId}/send`).then((r) => r.data),
  accept: (tradeId: number) => api.post<Trade>(`/api/trades/${tradeId}/accept`).then((r) => r.data),
  reject: (tradeId: number) => api.post(`/api/trades/${tradeId}/reject`).then((r) => r.data),
  get: (tradeId: number) => api.get<Trade>(`/api/trades/${tradeId}`).then((r) => r.data),
  history: () => api.get<Trade[]>('/api/trades/history').then((r) => r.data),
};

export const economyApi = {
  history: () => api.get<Transaction[]>('/api/economy/history', { params: { page: 0, size: 100 } }).then((r) => r.data),
};

export const notificationsApi = {
  list: () => api.get<Notification[]>('/api/notifications').then((r) => r.data),
  markRead: (id: number) => api.post(`/api/notifications/read/${id}`).then((r) => r.data),
};

export const adminApi = {
  stats: () => api.get<{ users: number; cardDefinitions: number; quests: number }>('/api/admin/stats'),
  users: () => api.get<AdminUser[]>('/api/admin/users'),
  user: (id: number) => api.get<AdminUser>(`/api/admin/users/${id}`),
  userTransactions: (id: number) => api.get<AdminTransaction[]>(`/api/admin/users/${id}/transactions`),
  cards: () => api.get<AdminCard[]>('/api/admin/cards'),
  createCard: (body: { title: string; rarity: string; telegramUsername?: string; imageUrl?: string }) => api.post<AdminCard>('/api/admin/cards', body),
  updateCard: (id: number, body: { title: string; rarity: string; telegramUsername?: string; imageUrl?: string }) => api.put<AdminCard>(`/api/admin/cards/${id}`, body),
  deleteCard: (id: number) => api.delete(`/api/admin/cards/${id}`),
  addCoins: (userId: number, amount: number, reason: string) => api.post('/api/admin/economy/add', { userId, amount, reason }),
  removeCoins: (userId: number, amount: number, reason: string) => api.post('/api/admin/economy/remove', { userId, amount, reason }),
  questTasks: () => api.get<import('./types').QuestTask[]>('/api/admin/quest-tasks'),
  createQuestTask: (description: string, rewardCoins: number) =>
    api.post('/api/admin/quest-tasks', { description, rewardCoins }),
  deleteQuestTask: (id: number) => api.delete(`/api/admin/quest-tasks/${id}`),
  bettingEvents: () => api.get<BettingEvent[]>('/api/admin/betting/events'),
  createEvent: (title: string, optionLabels: string[]) => api.post('/api/admin/betting/events', { title, optionLabels }),
  closeEvent: (eventId: number) => api.post(`/api/admin/betting/events/${eventId}/close`),
  settleEvent: (eventId: number, winningOptionId: number) => api.post(`/api/admin/betting/events/${eventId}/settle`, { winningOptionId }),
};
