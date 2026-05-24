import { NavLink, Outlet } from 'react-router-dom';
import { useUserStore } from '../store/userStore';
import { useEffect, useState } from 'react';
import { userApi } from '../api/endpoints';

export function AppLayout() {
  const user = useUserStore((s) => s.user);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    userApi.isAdmin().then(setIsAdmin).catch(() => setIsAdmin(false));
  }, []);

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
        <div className="flex items-center justify-between">
          <h1 className="text-lg font-bold text-violet-400">Casino Students</h1>
          {user && <div className="rounded-full bg-amber-500/20 px-3 py-1 text-sm font-semibold text-amber-300">Coins {user.balanceCoins.toLocaleString()}</div>}
        </div>
      </header>

      <main className="flex-1 px-4 py-4"><Outlet /></main>

      <nav className="fixed bottom-0 left-0 right-0 z-10 border-t border-zinc-800 bg-[#14141c] px-1">
        <div className="flex justify-around">
          {nav.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.to === '/'} className={({ isActive }) => `flex flex-1 flex-col items-center gap-0.5 py-2 text-xs ${isActive ? 'text-violet-400' : 'text-zinc-500'}`}>
              <span className="text-lg">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </div>
      </nav>
    </div>
  );
}
