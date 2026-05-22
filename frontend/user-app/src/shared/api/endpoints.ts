import { api } from './client';
import type {
  BettingEvent,
  CardDefinition,
  CollectionProgress,
  Inventory,
  Notification,
  OpenPackResponse,
  Pack,
  PackHistoryItem,
  Quest,
  QuestSubmission,
  LeaderboardEntry,
  PublicUser,
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
  leaderboard: (limit = 50) =>
    api.get<LeaderboardEntry[]>('/api/users/leaderboard', { params: { limit } }).then((r) => r.data),
  byId: (id: number) => api.get<PublicUser>(`/api/users/${id}`).then((r) => r.data),
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
    api
      .post<OpenPackResponse>('/api/packs/open', { packId, idempotencyKey })
      .then((r) => r.data),
  history: () => api.get<PackHistoryItem[]>('/api/packs/history').then((r) => r.data),
};

export const casinoApi = {
  spin: (bet: number) => api.post<number>('/api/casino/slots/spin', { bet }).then((r) => r.data),
  slotsHistory: () => api.get('/api/casino/slots/history').then((r) => r.data),
  rouletteBet: (body: { betType: string; numberValue?: number; stake: number }) =>
    api.post<RouletteResult>('/api/casino/roulette/bet', body).then((r) => r.data),
  rouletteHistory: () => api.get('/api/casino/roulette/history').then((r) => r.data),
};

export const questsApi = {
  list: () => api.get<Quest[]>('/api/quests').then((r) => r.data),
  submit: (questId: number, proofText: string) =>
    api.post<QuestSubmission>('/api/quests/submit', { questId, proofText }).then((r) => r.data),
  mySubmissions: () =>
    api.get<QuestSubmission[]>('/api/quests/my-submissions').then((r) => r.data),
};

export const bettingApi = {
  events: () => api.get<BettingEvent[]>('/api/betting/events').then((r) => r.data),
  place: (optionId: number, stakeCoins: number) =>
    api.post<number>('/api/betting/place', { optionId, stakeCoins }).then((r) => r.data),
  history: () => api.get('/api/betting/history').then((r) => r.data),
};

export const tradesApi = {
  create: (partnerUserId: number) =>
    api.post<Trade>('/api/trades', { partnerUserId }).then((r) => r.data),
  addCard: (
    tradeId: number,
    cardDefinitionId: number,
    quantity = 1,
    fromUserId?: number,
  ) =>
    api
      .post<Trade>(`/api/trades/${tradeId}/items`, {
        cardDefinitionId,
        quantity,
        ...(fromUserId != null ? { fromUserId } : {}),
      })
      .then((r) => r.data),
  addCoins: (tradeId: number, coinsAmount: number) =>
    api.post<Trade>(`/api/trades/${tradeId}/items`, { coinsAmount }).then((r) => r.data),
  send: (tradeId: number) => api.post<Trade>(`/api/trades/${tradeId}/send`).then((r) => r.data),
  accept: (tradeId: number) =>
    api.post<Trade>(`/api/trades/${tradeId}/accept`).then((r) => r.data),
  reject: (tradeId: number) => api.post(`/api/trades/${tradeId}/reject`).then((r) => r.data),
  get: (tradeId: number) => api.get<Trade>(`/api/trades/${tradeId}`).then((r) => r.data),
  history: () => api.get<Trade[]>('/api/trades/history').then((r) => r.data),
};

export const economyApi = {
  history: () => api.get<Transaction[]>('/api/economy/history').then((r) => r.data),
};

export const notificationsApi = {
  list: () => api.get<Notification[]>('/api/notifications').then((r) => r.data),
  markRead: (id: number) => api.post(`/api/notifications/read/${id}`).then((r) => r.data),
};
