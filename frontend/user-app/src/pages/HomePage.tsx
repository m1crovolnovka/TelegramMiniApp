import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { bettingApi, cardsApi, notificationsApi } from '../shared/api/endpoints';
import type { BettingEvent, CollectionProgress, Notification } from '../shared/api/types';
import { useUserStore } from '../shared/store/userStore';
import { Loader, PageError } from '../shared/ui/Loader';

const quickLinks = [
  { to: '/shop', label: 'Открыть пак', emoji: '📦' },
  { to: '/casino', label: 'Казино', emoji: '🎰' },
  { to: '/quests', label: 'Квесты (бот)', emoji: '🤖' },
  { to: '/collection', label: 'Коллекция', emoji: '📚' },
  { to: '/bets', label: 'Ставки', emoji: '🏆' },
  { to: '/trades', label: 'Трейды', emoji: '🔄' },
  { to: '/leaderboard', label: 'Рейтинг', emoji: '📊' },
];

export function HomePage() {
  const user = useUserStore((s) => s.user);
  const [progress, setProgress] = useState<CollectionProgress | null>(null);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [events, setEvents] = useState<BettingEvent[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      cardsApi.collectionProgress(),
      notificationsApi.list(),
      bettingApi.events(),
    ])
      .then(([p, n, e]) => {
        setProgress(p);
        setNotifications(n.slice(0, 5));
        setEvents(e.filter((ev) => ev.status === 'ACTIVE').slice(0, 3));
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loader />;
  if (error) return <PageError message={error} onRetry={() => window.location.reload()} />;

  return (
    <div className="space-y-4">
      <section className="rounded-2xl bg-gradient-to-br from-violet-700 to-indigo-900 p-5">
        <p className="text-sm text-violet-200">Привет, {user?.username ?? 'игрок'}!</p>
        <p className="mt-1 text-3xl font-bold text-amber-300">
          🪙 {(user?.balanceCoins ?? 0).toLocaleString()}
        </p>
        {progress && (
          <p className="mt-2 text-sm text-violet-200">
            Коллекция: {progress.ownedDefinitions}/{progress.totalDefinitions} (
            {progress.percentByDefinitions.toFixed(0)}%)
          </p>
        )}
      </section>

      <section>
        <h2 className="mb-2 text-sm font-semibold text-zinc-400">Быстрые действия</h2>
        <div className="grid grid-cols-3 gap-2">
          {quickLinks.map((l) => (
            <Link
              key={l.to}
              to={l.to}
              className="flex flex-col items-center rounded-xl bg-zinc-800/80 p-3 text-center text-xs hover:bg-zinc-700"
            >
              <span className="text-2xl">{l.emoji}</span>
              {l.label}
            </Link>
          ))}
        </div>
      </section>

      {events.length > 0 && (
        <section className="rounded-xl bg-zinc-800/60 p-4">
          <h2 className="mb-2 text-sm font-semibold">Активные события</h2>
          {events.map((ev) => (
            <Link
              key={ev.id}
              to="/bets"
              className="block rounded-lg bg-zinc-900/80 px-3 py-2 text-sm hover:bg-zinc-700"
            >
              {ev.title} →
            </Link>
          ))}
        </section>
      )}

      {notifications.length > 0 && (
        <section className="rounded-xl bg-zinc-800/60 p-4">
          <h2 className="mb-2 text-sm font-semibold">Уведомления</h2>
          <ul className="space-y-1">
            {notifications.map((n) => (
              <li key={n.id} className={`text-sm ${n.read ? 'text-zinc-500' : 'text-zinc-200'}`}>
                {n.message}
              </li>
            ))}
          </ul>
        </section>
      )}
    </div>
  );
}
