import { NavLink, Outlet } from 'react-router-dom';
import { useUserStore } from '../store/userStore';
import { useAuthStore } from '../store/authStore';
import { useEffect, useState } from 'react';
import { userApi } from '../api/endpoints';

export function AppLayout() {
  const user = useUserStore((s) => s.user);
  const setUser = useUserStore((s) => s.setUser);
  const token = useAuthStore((s) => s.token);
  const [isAdmin, setIsAdmin] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    userApi.isAdmin().then(setIsAdmin).catch(() => setIsAdmin(false));
  }, []);

  const refreshBalance = async () => {
    if (!token) return;
    setRefreshing(true);
    try {
      setUser(await userApi.me());
    } finally {
      setRefreshing(false);
    }
  };

  const nav = [
    { to: '/', label: 'Home', icon: 'H' },
    { to: '/shop', label: 'Shop', icon: 'S' },
    { to: '/casino', label: 'Casino', icon: 'C' },
    { to: '/collection', label: 'Cards', icon: 'K' },
    { to: '/profile', label: 'Profile', icon: 'P' },
    ...(isAdmin ? [{ to: '/admin', label: 'Admin', icon: 'A' }] : []),
  ];

  return (
    <div className="flex min-h-full flex-col pb-20">
      <header className="sticky top-0 z-10 border-b border-zinc-800 bg-[#0f0f14]/95 px-4 py-3 backdrop-blur">
        <div className="flex items-center justify-between gap-2">
          <h1 className="text-lg font-bold text-violet-400">Casino Students</h1>
          {user && (
            <div className="flex items-center gap-1.5">
              <div className="rounded-full bg-amber-500/20 px-3 py-1 text-sm font-semibold text-amber-300">
                🪙 {user.balanceCoins.toLocaleString()}
              </div>
              <button
                type="button"
                onClick={refreshBalance}
                disabled={refreshing}
                title="Обновить баланс"
                className="rounded-full border border-amber-500/40 bg-zinc-800 px-2.5 py-1 text-sm text-amber-300 hover:bg-zinc-700 disabled:opacity-50"
              >
                {refreshing ? '…' : '↻'}
              </button>
            </div>
          )}
        </div>
      </header>

      <main className="flex-1 px-4 py-4">
        <Outlet />
      </main>

      <nav className="fixed bottom-0 left-0 right-0 z-10 border-t border-zinc-800 bg-[#14141c] px-1">
        <div className="flex justify-around">
          {nav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                `flex flex-1 flex-col items-center gap-0.5 py-2 text-xs ${isActive ? 'text-violet-400' : 'text-zinc-500'}`
              }
            >
              <span className="text-lg">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </div>
      </nav>
    </div>
  );
}
