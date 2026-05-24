import { useEffect, useState } from 'react';
import { adminApi } from '../../shared/api/endpoints';

export function AdminDashboardPage() {
  const [stats, setStats] = useState<{ users: number; cardDefinitions: number; quests: number } | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    adminApi
      .stats()
      .then((r) => setStats(r.data))
      .catch((e) => setError(e instanceof Error ? e.message : 'Ошибка'));
  }, []);

  if (error) return <p className="text-red-400">{error}</p>;
  if (!stats) return <p className="text-zinc-400">Загрузка...</p>;

  return (
    <div className="grid grid-cols-3 gap-3">
      {[
        ['Пользователи', stats.users],
        ['Карточки', stats.cardDefinitions],
        ['Шаблоны квестов', stats.quests],
      ].map(([label, val]) => (
        <div key={label as string} className="rounded-xl bg-zinc-800 p-4">
          <p className="text-sm text-zinc-400">{label}</p>
          <p className="text-2xl font-bold">{val}</p>
        </div>
      ))}
    </div>
  );
}
