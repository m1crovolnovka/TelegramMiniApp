import { useEffect, useState } from 'react';
import { adminApi } from '../../shared/api/endpoints';
import type { BettingEvent } from '../../shared/api/types';
import { Button } from '../../shared/ui/Button';

const STATUS_LABEL: Record<string, string> = {
  ACTIVE: 'Приём ставок',
  CLOSED: 'Закрыто',
  SETTLED: 'Рассчитано',
  DRAFT: 'Черновик',
  CANCELLED: 'Отменено',
};

export function AdminEventsPage() {
  const [events, setEvents] = useState<BettingEvent[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [title, setTitle] = useState('');
  const [options, setOptions] = useState('A,B');
  const [msg, setMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const reload = () =>
    adminApi.bettingEvents().then((r) => {
      setEvents(r.data);
      if (selectedId != null && !r.data.some((e) => e.id === selectedId)) {
        setSelectedId(null);
      }
    });

  useEffect(() => {
    reload().catch((e) => setMsg(e instanceof Error ? e.message : 'Ошибка загрузки'));
  }, []);

  const selected = events.find((e) => e.id === selectedId);

  const run = async (fn: () => Promise<unknown>, ok: string) => {
    setLoading(true);
    setMsg('');
    try {
      await fn();
      setMsg(ok);
      await reload();
    } catch (e) {
      setMsg(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-4">
      <section className="space-y-2 rounded-xl bg-zinc-800 p-4">
        <h2 className="font-semibold">Создать событие</h2>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Название"
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <input
          value={options}
          onChange={(e) => setOptions(e.target.value)}
          placeholder="Варианты через запятую"
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <Button
          disabled={loading}
          onClick={() =>
            run(
              () =>
                adminApi.createEvent(
                  title,
                  options.split(',').map((s) => s.trim()).filter(Boolean),
                ),
              'Событие создано',
            )
          }
        >
          Создать
        </Button>
      </section>

      <section className="rounded-xl bg-zinc-800 p-4">
        <h2 className="mb-3 font-semibold">События ({events.length})</h2>
        {events.length === 0 ? (
          <p className="text-sm text-zinc-500">Пока нет событий</p>
        ) : (
          <ul className="space-y-2">
            {events.map((e) => (
              <li key={e.id}>
                <button
                  type="button"
                  onClick={() => setSelectedId(e.id)}
                  className={`w-full rounded-lg px-3 py-2 text-left text-sm transition ${
                    selectedId === e.id
                      ? 'bg-violet-600 text-white'
                      : 'bg-zinc-900/80 text-zinc-200 hover:bg-zinc-900'
                  }`}
                >
                  <span className="font-medium">#{e.id} {e.title}</span>
                  <span className="ml-2 text-xs opacity-80">{STATUS_LABEL[e.status] ?? e.status}</span>
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>

      {selected && (
        <section className="space-y-3 rounded-xl border border-violet-500/30 bg-violet-950/20 p-4">
          <h2 className="font-semibold">
            #{selected.id} {selected.title}
          </h2>
          <p className="text-sm text-zinc-400">Статус: {STATUS_LABEL[selected.status] ?? selected.status}</p>

          <div className="space-y-2">
            <p className="text-xs text-zinc-500">Варианты и ставки:</p>
            {selected.options.map((o) => (
              <div
                key={o.id}
                className="flex items-center justify-between rounded-lg bg-zinc-900/80 px-3 py-2 text-sm"
              >
                <span>
                  {o.label}
                  {o.winning ? ' ✓' : ''}
                </span>
                <span className="text-amber-300">🪙 {o.totalStakeCoins.toLocaleString()}</span>
              </div>
            ))}
          </div>

          {selected.status === 'ACTIVE' && (
            <Button
              variant="secondary"
              className="w-full"
              disabled={loading}
              onClick={() => run(() => adminApi.closeEvent(selected.id), 'Приём ставок закрыт')}
            >
              Закрыть приём ставок
            </Button>
          )}

          {(selected.status === 'ACTIVE' || selected.status === 'CLOSED') && (
            <div className="space-y-2">
              <p className="text-sm font-medium text-zinc-300">Выберите победителя — выплаты 2× ставки:</p>
              <div className="flex flex-wrap gap-2">
                {selected.options.map((o) => (
                  <Button
                    key={o.id}
                    disabled={loading}
                    onClick={() =>
                      run(
                        () => adminApi.settleEvent(selected.id, o.id),
                        `Рассчитано: победил «${o.label}»`,
                      )
                    }
                  >
                    {o.label}
                  </Button>
                ))}
              </div>
            </div>
          )}

          {selected.status === 'SETTLED' && (
            <p className="text-sm text-green-400">Событие уже рассчитано.</p>
          )}
        </section>
      )}

      {msg && <p className="text-sm text-zinc-400">{msg}</p>}
    </div>
  );
}
