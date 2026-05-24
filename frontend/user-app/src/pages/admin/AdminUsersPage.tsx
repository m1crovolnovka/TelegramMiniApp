import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { adminApi } from '../../shared/api/endpoints';
import type { AdminUser } from '../../shared/api/types';

export function AdminUsersPage() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    adminApi
      .users()
      .then((r) => setUsers(r.data))
      .catch((e) => setError(e instanceof Error ? e.message : 'Ошибка'));
  }, []);

  if (error) return <p className="text-red-400">{error}</p>;

  return (
    <div>
      <h2 className="mb-3 font-semibold">Пользователи</h2>
      <ul className="space-y-2">
        {users.map((u) => (
          <li key={u.id} className="flex items-center justify-between rounded-xl bg-zinc-800 px-4 py-3">
            <div>
              <p className="font-medium">@{u.username ?? u.id}</p>
              <p className="text-xs text-zinc-500">
                ID {u.id} · студентов {u.uniqueStudentsOwned}
              </p>
            </div>
            <div className="text-right">
              <p className="text-amber-300">🪙 {u.balanceCoins.toLocaleString()}</p>
              <Link to={`/admin/users/${u.id}`} className="text-xs text-violet-400">
                Профиль →
              </Link>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
