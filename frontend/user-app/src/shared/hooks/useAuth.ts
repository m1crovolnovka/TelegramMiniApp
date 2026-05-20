import { useCallback, useEffect, useState } from 'react';
import { authApi, userApi } from '../api/endpoints';
import { getInitData, initTelegram } from '../lib/telegram';
import { useAuthStore } from '../store/authStore';
import { useUserStore } from '../store/userStore';

export function useAuthBootstrap() {
  const token = useAuthStore((s) => s.token);
  const setToken = useAuthStore((s) => s.setToken);
  const { user, setUser, setLoading } = useUserStore();
  const [error, setError] = useState<string | null>(null);
  const [booting, setBooting] = useState(true);

  const login = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      initTelegram();
      const initData = getInitData();
      const { accessToken } = await authApi.telegram(initData);
      setToken(accessToken);
      const me = await userApi.me();
      setUser(me);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Auth failed');
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, [setToken, setUser, setLoading]);

  const refreshUser = useCallback(async () => {
    if (!useAuthStore.getState().token) return;
    try {
      const me = await userApi.me();
      setUser(me);
    } catch {
      /* ignore */
    }
  }, [setUser]);

  useEffect(() => {
    (async () => {
      if (token && !user) {
        try {
          setLoading(true);
          const me = await userApi.me();
          setUser(me);
        } catch {
          await login();
        } finally {
          setLoading(false);
          setBooting(false);
        }
      } else if (!token) {
        await login();
        setBooting(false);
      } else {
        setBooting(false);
      }
    })();
  }, []);

  return { user, error, booting, login, refreshUser };
}
