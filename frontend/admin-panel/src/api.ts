import axios from 'axios';

const ADMIN_INIT = 'dev-admin-login-token-2026';

export const api = axios.create({ baseURL: 'https://p01--telegramminiapp--jrk5phf8xmp4.code.run' });

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

export const adminApi = {
  stats: () => api.get<{ users: number; cardDefinitions: number; quests: number }>('/api/admin/stats'),
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
