import { useEffect, useState } from 'react';
import { tradesApi } from '../shared/api/endpoints';
import type { Trade } from '../shared/api/types';
import { Button } from '../shared/ui/Button';
import { Loader, PageError } from '../shared/ui/Loader';

export function TradesPage() {
  const [history, setHistory] = useState<Trade[]>([]);
  const [partnerId, setPartnerId] = useState('');
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    tradesApi
      .history()
      .then(setHistory)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const createTrade = async () => {
    const id = Number(partnerId);
    if (!id) return;
    setBusy(true);
    try {
      await tradesApi.create(id);
      setPartnerId('');
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setBusy(false);
    }
  };

  const accept = async (tradeId: number) => {
    setBusy(true);
    try {
      await tradesApi.accept(tradeId);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setBusy(false);
    }
  };

  const reject = async (tradeId: number) => {
    setBusy(true);
    try {
      await tradesApi.reject(tradeId);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Трейды</h1>
      {error && <PageError message={error} onRetry={load} />}

      <section className="rounded-xl bg-zinc-800/80 p-4">
        <p className="mb-2 text-sm text-zinc-400">ID партнёра для нового трейда</p>
        <input
          type="number"
          value={partnerId}
          onChange={(e) => setPartnerId(e.target.value)}
          placeholder="User ID"
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <Button className="mt-2 w-full" loading={busy} onClick={createTrade}>
          Создать трейд
        </Button>
      </section>

      {loading ? (
        <Loader />
      ) : (
        <ul className="space-y-2">
          {history.length === 0 && <p className="text-zinc-500">История пуста</p>}
          {history.map((t) => (
            <li key={t.id} className="rounded-xl bg-zinc-800/60 p-3 text-sm">
              <p>
                #{t.id} — {t.status}
              </p>
              <p className="text-zinc-500">
                {t.initiatorUserId} → {t.partnerUserId}
              </p>
              {t.status === 'PENDING' && (
                <div className="mt-2 flex gap-2">
                  <Button className="flex-1" onClick={() => accept(t.id)} loading={busy}>
                    Принять
                  </Button>
                  <Button variant="danger" className="flex-1" onClick={() => reject(t.id)} loading={busy}>
                    Отклонить
                  </Button>
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
