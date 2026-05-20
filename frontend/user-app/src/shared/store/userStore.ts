import { create } from 'zustand';
import type { User } from '../api/types';

interface UserState {
  user: User | null;
  loading: boolean;
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  updateBalance: (balance: number) => void;
}

export const useUserStore = create<UserState>((set) => ({
  user: null,
  loading: false,
  setUser: (user) => set({ user }),
  setLoading: (loading) => set({ loading }),
  updateBalance: (balanceCoins) =>
    set((s) => (s.user ? { user: { ...s.user, balanceCoins } } : {})),
}));
