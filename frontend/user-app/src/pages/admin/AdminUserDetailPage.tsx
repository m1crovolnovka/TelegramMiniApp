import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { adminApi } from '../../shared/api/endpoints';
import type { AdminTransaction, AdminUser } from '../../shared/api/types';
import { Button } from '../../shared/ui/Button';

export function AdminUserDetailPage() {
  const { id } = useParams();
  const userId = Number(id);
  const [user, setUser] = useState<AdminUser | null>(null);
  const [tx, setTx] = useState<AdminTransaction[]>([]);
  const [amount, setAmount] = useState('1000');
  const [reason, setReason] = useState('admin_adjust');
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');

  const reload = async () => {
    const [u, t] = await Promise.all([adminApi.user(userId), adminApi.userTransactions(userId)]);
    setUser(u.data);
    setTx(t.data);
  };

  useEffect(() => {
    if (!userId) return;
    reload().catch((e) => setError(e instanceof Error ? e.message : 'Ошибка'));
  }, [userId]);

  const adjust = async (add: boolean) => {
    try {
      const fn = add ? adminApi.addCoins : adminApi.removeCoins;
      await fn(userId, Number(amount), reason);
      await reload();
      setMsg(add ? 'Баланс пополнен' : 'Баланс списан');
    } catch (e) {
      setMsg(e instanceof Error ? e.message : 'Ошибка');
    }
  };

  if (error) return <p className="text-red-400">{error}</p>;
  if (!user) return <p className="text-zinc-400">Загрузка...</p>;

  return (
    <div className="space-y-4">
      <Link to="/admin/users" className="text-sm text-zinc-400">
        ← К списку
      </Link>
      <section className="rounded-xl bg-zinc-800 p-4">
        <h2 className="text-xl font-bold">@{user.username ?? user.id}</h2>
        <p className="text-sm text-zinc-400">Telegram ID: {user.telegramId}</p>
        <p className="mt-2 text-2xl text-amber-300">🪙 {user.balanceCoins.toLocaleString()}</p>
        <p className="text-sm text-zinc-500">Уникальных студентов: {user.uniqueStudentsOwned}</p>
      </section>

      <section className="space-y-2 rounded-xl bg-zinc-800 p-4">
        <h3 className="font-semibold">Изменить баланс</h3>
        <input
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
          placeholder="Сумма"
        />
        <input
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
          placeholder="Причина"
        />
        <div className="flex gap-2">
          <Button className="flex-1" onClick={() => adjust(true)}>
            Начислить
          </Button>
          <Button variant="danger" className="flex-1" onClick={() => adjust(false)}>
            Списать
          </Button>
        </div>
        {msg && <p className="text-sm text-zinc-400">{msg}</p>}
      </section>

      <section>
        <h3 className="mb-2 font-semibold">История транзакций</h3>
        <ul className="max-h-80 space-y-1 overflow-y-auto text-sm">
          {tx.map((t) => (
            <li key={t.id} className="flex justify-between rounded-lg bg-zinc-800/80 px-3 py-2">
              <span>{t.description ?? t.reason}</span>
              <span className={t.amount >= 0 ? 'text-green-400' : 'text-red-400'}>
                {t.amount >= 0 ? '+' : ''}
                {t.amount}
              </span>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
