import { useEffect, useState } from 'react';
import { cardsApi } from '../shared/api/endpoints';
import type { CardRarity, Inventory, InventoryItem } from '../shared/api/types';
import { RarityBadge } from '../shared/ui/Badge';
import { Loader, PageError } from '../shared/ui/Loader';

const rarities: (CardRarity | 'ALL')[] = ['ALL', 'COMMON', 'RARE', 'LEGENDARY'];

export function InventoryPage() {
  const [inventory, setInventory] = useState<Inventory | null>(null);
  const [filter, setFilter] = useState<CardRarity | 'ALL'>('ALL');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    cardsApi
      .inventory()
      .then(setInventory)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const items: InventoryItem[] =
    inventory?.items.filter((i) => filter === 'ALL' || i.rarity === filter) ?? [];

  return (
    <div className="space-y-4">
      <div className="flex gap-2 overflow-x-auto pb-1">
        {rarities.map((r) => (
          <button
            key={r}
            type="button"
            onClick={() => setFilter(r)}
            className={`shrink-0 rounded-lg px-3 py-1 text-xs ${
              filter === r ? 'bg-violet-600' : 'bg-zinc-800'
            }`}
          >
            {r === 'ALL' ? 'Все' : r}
          </button>
        ))}
      </div>

      {loading && <Loader />}
      {error && <PageError message={error} onRetry={load} />}
      {!loading && !error && (
        <>
          <p className="text-sm text-zinc-400">Всего карт: {inventory?.totalQuantity ?? 0}</p>
          <div className="grid grid-cols-2 gap-3">
            {items.map((item) => (
              <article
                key={item.cardDefinitionId}
                className={`rounded-xl border p-3 ${
                  item.locked ? 'border-amber-500/50 opacity-70' : 'border-zinc-700'
                } bg-zinc-800/80`}
              >
                <div className="mb-2 flex items-start justify-between gap-1">
                  <h3 className="font-semibold leading-tight">{item.title}</h3>
                  <RarityBadge rarity={item.rarity} />
                </div>
                <p className="text-sm text-zinc-400">×{item.quantity}</p>
                {item.locked && <p className="mt-1 text-xs text-amber-400">🔒 В трейде</p>}
              </article>
            ))}
          </div>
          {items.length === 0 && (
            <p className="text-center text-zinc-500">Нет карточек. Откройте пак в магазине!</p>
          )}
        </>
      )}
    </div>
  );
}
