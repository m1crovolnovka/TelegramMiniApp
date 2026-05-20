import { useEffect, useState } from 'react';
import { economyApi } from '../shared/api/endpoints';
import type { Transaction } from '../shared/api/types';
import { useUserStore } from '../shared/store/userStore';
import { Loader, PageError } from '../shared/ui/Loader';

export function ProfilePage() {
  const user = useUserStore((s) => s.user);
  const [tx, setTx] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    economyApi
      .history()
      .then(setTx)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-4">
      <section className="rounded-2xl bg-zinc-800/80 p-5">
        <p className="text-2xl font-bold">{user?.username ?? `User #${user?.id}`}</p>
        <p className="text-sm text-zinc-400">Telegram ID: {user?.telegramId}</p>
        <p className="mt-2 text-xl text-amber-300">🪙 {user?.balanceCoins.toLocaleString()}</p>
        <p className="mt-1 text-xs text-zinc-500">Роль: {user?.role}</p>
      </section>

      <section>
        <h2 className="mb-2 font-semibold">История транзакций</h2>
        {loading && <Loader />}
        {error && <PageError message={error} />}
        {!loading && !error && (
          <ul className="space-y-2">
            {tx.length === 0 && <p className="text-sm text-zinc-500">Пока нет транзакций</p>}
            {tx.map((t) => (
              <li
                key={t.id}
                className="flex justify-between rounded-lg bg-zinc-800/60 px-3 py-2 text-sm"
              >
                <span>
                  {t.transactionType} — {t.reason}
                </span>
                <span className={t.amount >= 0 ? 'text-green-400' : 'text-red-400'}>
                  {t.amount >= 0 ? '+' : ''}
                  {t.amount}
                </span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}
