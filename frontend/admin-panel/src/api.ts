import axios from 'axios';

const apiBase = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '');
const ADMIN_INIT =
  import.meta.env.VITE_ADMIN_INIT_DATA ?? 'dev-admin-login-token-2026';

export const api = axios.create({
  baseURL: apiBase,
  headers: { 'Content-Type': 'application/json' },
});

export function setToken(token: string | null) {
  if (token) {
    api.defaults.headers.common.Authorization = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common.Authorization;
  }
}

export async function adminLogin(initData = ADMIN_INIT) {
  const { data } = await api.post<{ accessToken: string }>('/api/auth/telegram', { initData });
  setToken(data.accessToken);
  return data.accessToken;
}

export interface AdminUser {
  id: number;
  username: string | null;
  telegramId: number;
  balanceCoins: number;
}

export interface AdminCard {
  id: number;
  title: string;
  rarity: string;
  telegramUsername: string | null;
  imageUrl: string | null;
}

export interface AdminTransaction {
  id: number;
  amount: number;
  transactionType: string;
  reason: string;
  description: string;
  createdAt: string;
}

export const adminApi = {
  stats: () =>
    api.get<{ users: number; cardDefinitions: number; quests: number }>('/api/admin/stats'),
  users: () => api.get<AdminUser[]>('/api/admin/users'),
  user: (id: number) => api.get<AdminUser>(`/api/admin/users/${id}`),
  userTransactions: (id: number) =>
    api.get<AdminTransaction[]>(`/api/admin/users/${id}/transactions`),
  cards: () => api.get<AdminCard[]>('/api/admin/cards'),
  createCard: (body: {
    title: string;
    rarity: string;
    telegramUsername?: string;
    imageUrl?: string;
  }) => api.post<AdminCard>('/api/admin/cards', body),
  updateCard: (
    id: number,
    body: { title: string; rarity: string; telegramUsername?: string; imageUrl?: string },
  ) => api.put<AdminCard>(`/api/admin/cards/${id}`, body),
  deleteCard: (id: number) => api.delete(`/api/admin/cards/${id}`),
  addCoins: (userId: number, amount: number, reason: string) =>
    api.post('/api/admin/economy/add', { userId, amount, reason }),
  removeCoins: (userId: number, amount: number, reason: string) =>
    api.post('/api/admin/economy/remove', { userId, amount, reason }),
  createQuest: (title: string, rewardCoins: number) =>
    api.post('/api/admin/quests', { title, rewardCoins }),
  approveSubmission: (submissionId: number) =>
    api.post(`/api/admin/quests/submissions/${submissionId}/approve`),
  rejectSubmission: (submissionId: number) =>
    api.post(`/api/admin/quests/submissions/${submissionId}/reject`),
  createEvent: (title: string, optionLabels: string[]) =>
    api.post('/api/admin/betting/events', { title, optionLabels }),
  closeEvent: (eventId: number) => api.post(`/api/admin/betting/events/${eventId}/close`),
  settleEvent: (eventId: number, winningOptionId: number) =>
    api.post(`/api/admin/betting/events/${eventId}/settle`, { winningOptionId }),
};
