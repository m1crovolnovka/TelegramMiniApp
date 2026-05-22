import { useEffect, useState } from 'react';
import { userApi } from '../shared/api/endpoints';
import type { LeaderboardEntry } from '../shared/api/types';
import { useUserStore } from '../shared/store/userStore';
import { Loader, PageError } from '../shared/ui/Loader';

export function LeaderboardPage() {
  const me = useUserStore((s) => s.user);
  const [rows, setRows] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    userApi
      .leaderboard(50)
      .then(setRows)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loader />;
  if (error) return <PageError message={error} />;

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Рейтинг по балансу</h1>
      <ul className="space-y-2">
        {rows.map((r) => (
          <li
            key={r.userId}
            className={`flex items-center justify-between rounded-xl px-4 py-3 ${
              me?.id === r.userId ? 'bg-violet-900/50 ring-1 ring-violet-500' : 'bg-zinc-800/80'
            }`}
          >
            <div className="flex items-center gap-3">
              <span className="w-6 text-center font-bold text-amber-400">#{r.rank}</span>
              <span>{r.username}</span>
            </div>
            <span className="font-semibold text-amber-300">🪙 {r.balanceCoins.toLocaleString()}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}
