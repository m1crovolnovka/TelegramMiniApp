import { useEffect, useState } from 'react';
import { Link, NavLink, Route, Routes, useParams } from 'react-router-dom';
import {
  adminApi,
  adminLogin,
  setToken,
  type AdminCard,
  type AdminTransaction,
  type AdminUser,
} from './api';

function Shell({ children }: { children: React.ReactNode }) {
  const nav = [
    { to: '/', label: 'Обзор' },
    { to: '/users', label: 'Пользователи' },
    { to: '/cards', label: 'Карточки' },
    { to: '/quests', label: 'Квесты' },
    { to: '/events', label: 'События' },
  ];
  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-100">
      <header className="border-b border-zinc-800 bg-zinc-900 px-6 py-4">
        <h1 className="text-xl font-bold text-red-400">Админ-панель Casino</h1>
        <p className="text-xs text-zinc-500">Доступ только для Telegram-ников из CASINO_ADMIN_ALLOWED_USERNAMES</p>
        <nav className="mt-3 flex flex-wrap gap-4 text-sm">
          {nav.map((n) => (
            <NavLink
              key={n.to}
              to={n.to}
              end={n.to === '/'}
              className={({ isActive }) => (isActive ? 'text-white' : 'text-zinc-500')}
            >
              {n.label}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="mx-auto max-w-4xl p-6">{children}</main>
    </div>
  );
}

function Dashboard() {
  const [stats, setStats] = useState<{ users: number; cardDefinitions: number; quests: number } | null>(
    null,
  );
  const [error, setError] = useState('');

  useEffect(() => {
    adminApi
      .stats()
      .then((r) => setStats(r.data))
      .catch((e) => setError(e instanceof Error ? e.message : 'Ошибка'));
  }, []);

  if (error) return <p className="text-red-400">{error}</p>;
  if (!stats) return <p>Загрузка...</p>;
  return (
    <div className="grid grid-cols-3 gap-4">
      {[
        ['Пользователи', stats.users],
        ['Карточки', stats.cardDefinitions],
        ['Квесты', stats.quests],
      ].map(([label, val]) => (
        <div key={label as string} className="rounded-xl bg-zinc-800 p-4">
          <p className="text-sm text-zinc-400">{label}</p>
          <p className="text-2xl font-bold">{val}</p>
        </div>
      ))}
    </div>
  );
}

function UsersPage() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    adminApi
      .users()
      .then((r) => setUsers(r.data))
      .catch((e) => setError(e.message));
  }, []);

  if (error) return <p className="text-red-400">{error}</p>;

  return (
    <div>
      <h2 className="mb-4 text-lg font-semibold">Пользователи</h2>
      <ul className="space-y-2">
        {users.map((u) => (
          <li key={u.id} className="flex items-center justify-between rounded-lg bg-zinc-800 px-4 py-3">
            <div>
              <p className="font-medium">{u.username ?? `User #${u.id}`}</p>
              <p className="text-xs text-zinc-500">ID {u.id} · TG {u.telegramId}</p>
            </div>
            <div className="text-right">
              <p className="text-amber-300">🪙 {u.balanceCoins.toLocaleString()}</p>
              <Link to={`/users/${u.id}`} className="text-xs text-violet-400">
                Профиль →
              </Link>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

function UserDetailPage() {
  const { id } = useParams();
  const userId = Number(id);
  const [user, setUser] = useState<AdminUser | null>(null);
  const [tx, setTx] = useState<AdminTransaction[]>([]);
  const [amount, setAmount] = useState('1000');
  const [reason, setReason] = useState('admin_adjust');
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (!userId) return;
    Promise.all([adminApi.user(userId), adminApi.userTransactions(userId)])
      .then(([u, t]) => {
        setUser(u.data);
        setTx(t.data);
      })
      .catch((e) => setError(e.message));
  }, [userId]);

  const adjust = async (add: boolean) => {
    try {
      const fn = add ? adminApi.addCoins : adminApi.removeCoins;
      await fn(userId, Number(amount), reason);
      const u = await adminApi.user(userId);
      setUser(u.data);
      const t = await adminApi.userTransactions(userId);
      setTx(t.data);
      setMsg(add ? 'Баланс пополнен' : 'Баланс списан');
    } catch (e) {
      setMsg(e instanceof Error ? e.message : 'Ошибка');
    }
  };

  if (error) return <p className="text-red-400">{error}</p>;
  if (!user) return <p>Загрузка...</p>;

  return (
    <div className="space-y-6">
      <Link to="/users" className="text-sm text-zinc-400">
        ← К списку
      </Link>
      <section className="rounded-xl bg-zinc-800 p-4">
        <h2 className="text-xl font-bold">{user.username ?? `User #${user.id}`}</h2>
        <p className="text-sm text-zinc-400">Telegram ID: {user.telegramId}</p>
        <p className="mt-2 text-2xl text-amber-300">🪙 {user.balanceCoins.toLocaleString()}</p>
      </section>

      <section className="space-y-2 rounded-xl bg-zinc-800 p-4">
        <h3 className="font-semibold">Изменить баланс</h3>
        <input
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
          placeholder="Сумма"
        />
        <input
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
          placeholder="Причина"
        />
        <div className="flex gap-2">
          <button type="button" onClick={() => adjust(true)} className="rounded bg-green-600 px-4 py-2">
            Начислить
          </button>
          <button type="button" onClick={() => adjust(false)} className="rounded bg-red-600 px-4 py-2">
            Списать
          </button>
        </div>
        {msg && <p className="text-sm text-zinc-400">{msg}</p>}
      </section>

      <section>
        <h3 className="mb-2 font-semibold">История транзакций</h3>
        <ul className="max-h-96 space-y-1 overflow-y-auto text-sm">
          {tx.map((t) => (
            <li key={t.id} className="flex justify-between rounded bg-zinc-800/60 px-3 py-2">
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

function CardsPage() {
  const [cards, setCards] = useState<AdminCard[]>([]);
  const [title, setTitle] = useState('');
  const [rarity, setRarity] = useState('COMMON');
  const [telegramUsername, setTelegramUsername] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [msg, setMsg] = useState('');

  const reload = () => adminApi.cards().then((r) => setCards(r.data));

  useEffect(() => {
    reload();
  }, []);

  const resetForm = () => {
    setTitle('');
    setRarity('COMMON');
    setTelegramUsername('');
    setImageUrl('');
    setEditingId(null);
  };

  const startEdit = (c: AdminCard) => {
    setEditingId(c.id);
    setTitle(c.title);
    setRarity(c.rarity);
    setTelegramUsername(c.telegramUsername ?? '');
    setImageUrl(c.imageUrl ?? '');
  };

  const save = async () => {
    try {
      const body = {
        title,
        rarity,
        telegramUsername: telegramUsername || undefined,
        imageUrl: imageUrl || undefined,
      };
      if (editingId != null) {
        await adminApi.updateCard(editingId, body);
        setMsg('Карточка обновлена');
      } else {
        await adminApi.createCard(body);
        setMsg('Карточка создана');
      }
      resetForm();
      reload();
    } catch (e) {
      setMsg(e instanceof Error ? e.message : 'Ошибка');
    }
  };

  return (
    <div className="space-y-6">
      <section className="space-y-2 rounded-xl bg-zinc-800 p-4">
        <h2 className="font-semibold">{editingId != null ? 'Редактировать карточку' : 'Добавить карточку'}</h2>
        <input
          placeholder="Название"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <input
          placeholder="Telegram username студента"
          value={telegramUsername}
          onChange={(e) => setTelegramUsername(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <select
          value={rarity}
          onChange={(e) => setRarity(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        >
          <option value="COMMON">Обычная</option>
          <option value="RARE">Редкая</option>
          <option value="LEGENDARY">Легендарная</option>
        </select>
        <input
          placeholder="URL изображения (https://...)"
          value={imageUrl}
          onChange={(e) => setImageUrl(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <div className="flex gap-2">
          <button type="button" className="rounded bg-violet-600 px-4 py-2" onClick={save}>
            {editingId != null ? 'Сохранить' : 'Создать'}
          </button>
          {editingId != null && (
            <button type="button" className="rounded bg-zinc-600 px-4 py-2" onClick={resetForm}>
              Отмена
            </button>
          )}
        </div>
        {msg && <p className="text-sm text-zinc-400">{msg}</p>}
      </section>

      <section>
        <h2 className="mb-2 font-semibold">Каталог ({cards.length})</h2>
        <ul className="grid grid-cols-2 gap-2 text-sm">
          {cards.map((c) => (
            <li key={c.id} className="rounded-lg bg-zinc-800 p-2">
              {c.imageUrl && (
                <img src={c.imageUrl} alt="" className="mb-1 h-20 w-full rounded object-cover" />
              )}
              <p className="font-medium">{c.title}</p>
              <p className="text-xs text-zinc-500">
                #{c.id} · {c.rarity}
                {c.telegramUsername ? ` · @${c.telegramUsername}` : ''}
              </p>
              <div className="mt-2 flex gap-2">
                <button
                  type="button"
                  className="rounded bg-zinc-700 px-2 py-1 text-xs"
                  onClick={() => startEdit(c)}
                >
                  Изменить
                </button>
                <button
                  type="button"
                  className="rounded bg-red-800 px-2 py-1 text-xs"
                  onClick={async () => {
                    if (!confirm(`Удалить карточку «${c.title}»?`)) return;
                    await adminApi.deleteCard(c.id);
                    setMsg('Удалено');
                    reload();
                  }}
                >
                  Удалить
                </button>
              </div>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}

function QuestsPage() {
  const [title, setTitle] = useState('');
  const [reward, setReward] = useState('100');
  const [submissionId, setSubmissionId] = useState('');
  const [msg, setMsg] = useState('');

  return (
    <div className="space-y-6">
      <section className="space-y-2">
        <h2 className="font-semibold">Создать квест</h2>
        <input
          placeholder="Название"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <input
          placeholder="Награда (монеты)"
          value={reward}
          onChange={(e) => setReward(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <button
          type="button"
          className="rounded bg-violet-600 px-4 py-2"
          onClick={async () => {
            try {
              await adminApi.createQuest(title, Number(reward));
              setMsg('Квест создан');
            } catch (e) {
              setMsg(e instanceof Error ? e.message : 'Ошибка');
            }
          }}
        >
          Создать
        </button>
      </section>
      <section className="space-y-2">
        <h2 className="font-semibold">Модерация заявки</h2>
        <input
          placeholder="ID заявки"
          value={submissionId}
          onChange={(e) => setSubmissionId(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <div className="flex gap-2">
          <button
            type="button"
            className="rounded bg-green-600 px-4 py-2"
            onClick={() =>
              adminApi.approveSubmission(Number(submissionId)).then(() => setMsg('Одобрено'))
            }
          >
            Одобрить
          </button>
          <button
            type="button"
            className="rounded bg-red-600 px-4 py-2"
            onClick={() =>
              adminApi.rejectSubmission(Number(submissionId)).then(() => setMsg('Отклонено'))
            }
          >
            Отклонить
          </button>
        </div>
      </section>
      {msg && <p className="text-sm text-zinc-400">{msg}</p>}
    </div>
  );
}

function EventsPage() {
  const [title, setTitle] = useState('');
  const [options, setOptions] = useState('A,B');
  const [eventId, setEventId] = useState('');
  const [winningOptionId, setWinningOptionId] = useState('');
  const [msg, setMsg] = useState('');

  return (
    <div className="space-y-6">
      <section className="space-y-2">
        <h2 className="font-semibold">Создать событие</h2>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Название"
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <input
          value={options}
          onChange={(e) => setOptions(e.target.value)}
          placeholder="Варианты через запятую"
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <button
          type="button"
          className="rounded bg-violet-600 px-4 py-2"
          onClick={async () => {
            try {
              const labels = options.split(',').map((s) => s.trim()).filter(Boolean);
              await adminApi.createEvent(title, labels);
              setMsg('Событие создано');
            } catch (e) {
              setMsg(e instanceof Error ? e.message : 'Ошибка');
            }
          }}
        >
          Создать
        </button>
      </section>
      <section className="space-y-2">
        <h2 className="font-semibold">Закрыть / рассчитать</h2>
        <input
          value={eventId}
          onChange={(e) => setEventId(e.target.value)}
          placeholder="ID события"
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <button
          type="button"
          className="rounded bg-zinc-600 px-4 py-2"
          onClick={() => adminApi.closeEvent(Number(eventId)).then(() => setMsg('Закрыто'))}
        >
          Закрыть приём ставок
        </button>
        <input
          value={winningOptionId}
          onChange={(e) => setWinningOptionId(e.target.value)}
          placeholder="ID выигравшего варианта"
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <button
          type="button"
          className="rounded bg-amber-600 px-4 py-2"
          onClick={() =>
            adminApi.settleEvent(Number(eventId), Number(winningOptionId)).then(() => setMsg('Рассчитано'))
          }
        >
          Рассчитать выплаты
        </button>
      </section>
      {msg && <p className="text-sm text-zinc-400">{msg}</p>}
    </div>
  );
}

export default function App() {
  const [ready, setReady] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('admin-token');
    if (token) setToken(token);
    adminLogin()
      .then((t) => {
        localStorage.setItem('admin-token', t);
        setReady(true);
      })
      .catch((e) => setError(e instanceof Error ? e.message : 'Вход не удался'));
  }, []);

  if (error) {
    return (
      <p className="p-8 text-red-400">
        Нет доступа к админке: {error}. Проверьте CASINO_ADMIN_ALLOWED_USERNAMES и VITE_API_URL.
      </p>
    );
  }
  if (!ready) return <p className="p-8">Вход...</p>;

  return (
    <Shell>
      <Routes>
        <Route index element={<Dashboard />} />
        <Route path="users" element={<UsersPage />} />
        <Route path="users/:id" element={<UserDetailPage />} />
        <Route path="cards" element={<CardsPage />} />
        <Route path="quests" element={<QuestsPage />} />
        <Route path="events" element={<EventsPage />} />
      </Routes>
    </Shell>
  );
}
