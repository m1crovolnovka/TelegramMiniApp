import { NavLink, Outlet } from 'react-router-dom';

const nav = [
  { to: '/admin', label: 'Обзор', end: true },
  { to: '/admin/users', label: 'Пользователи' },
  { to: '/admin/cards', label: 'Карточки' },
  { to: '/admin/quests', label: 'Квесты' },
  { to: '/admin/events', label: 'События' },
];

export function AdminLayout() {
  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-bold text-red-400">Админ-панель</h1>
        <p className="text-xs text-zinc-500">Доступ только для администраторов Casino</p>
      </div>
      <nav className="flex flex-wrap gap-3 text-sm">
        {nav.map((n) => (
          <NavLink
            key={n.to}
            to={n.to}
            end={n.end}
            className={({ isActive }) =>
              `rounded-lg px-3 py-1.5 ${isActive ? 'bg-violet-600 text-white' : 'bg-zinc-800 text-zinc-400'}`
            }
          >
            {n.label}
          </NavLink>
        ))}
      </nav>
      <Outlet />
    </div>
  );
}
