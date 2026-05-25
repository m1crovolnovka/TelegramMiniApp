import { useEffect, useState } from 'react';
import { bettingApi, userApi } from '../shared/api/endpoints';
import type { BettingEvent } from '../shared/api/types';
import { useUserStore } from '../shared/store/userStore';
import { Button } from '../shared/ui/Button';
import { Loader, PageError } from '../shared/ui/Loader';

export function BetsPage() {
  const setUser = useUserStore((s) => s.setUser);
  const [events, setEvents] = useState<BettingEvent[]>([]);
  const [stakes, setStakes] = useState<Record<number, number>>({});
  const [loading, setLoading] = useState(true);
  const [placing, setPlacing] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  const loadEvents = () =>
    bettingApi
      .events()
      .then(setEvents)
      .catch((e) => setError(e instanceof Error ? e.message : 'Ошибка'));

  useEffect(() => {
    loadEvents().finally(() => setLoading(false));
  }, []);

  const place = async (optionId: number) => {
    const stake = stakes[optionId] ?? 100;
    setPlacing(optionId);
    setError(null);
    try {
      await bettingApi.place(optionId, stake);
      const [updatedEvents, me] = await Promise.all([bettingApi.events(), userApi.me()]);
      setEvents(updatedEvents);
      setUser(me);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setPlacing(null);
    }
  };

  if (loading) return <Loader />;

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Ставки на события</h1>
      {error && <PageError message={error} />}
      {events.length === 0 && <p className="text-zinc-500">Нет активных событий</p>}
      {events.map((ev) => (
        <article key={ev.id} className="rounded-xl bg-zinc-800/80 p-4">
          <h2 className="font-semibold">{ev.title}</h2>
          <p className="text-xs text-zinc-500">{ev.status}</p>
          <ul className="mt-3 space-y-2">
            {ev.options.map((opt) => (
              <li key={opt.id} className="rounded-lg bg-zinc-900/80 p-3">
                <p className="font-medium">{opt.label}</p>
                <p className="text-xs text-amber-300/90">Пул: {opt.totalStakeCoins.toLocaleString()} 🪙</p>
                <input
                  type="number"
                  min={1}
                  value={stakes[opt.id] ?? 100}
                  onChange={(e) => setStakes({ ...stakes, [opt.id]: Number(e.target.value) })}
                  className="mt-2 w-full rounded border border-zinc-700 bg-zinc-900 px-2 py-1 text-sm"
                />
                <Button
                  className="mt-2 w-full"
                  loading={placing === opt.id}
                  onClick={() => place(opt.id)}
                >
                  Поставить
                </Button>
              </li>
            ))}
          </ul>
        </article>
      ))}
    </div>
  );
}
