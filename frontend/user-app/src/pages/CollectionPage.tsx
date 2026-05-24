import { useEffect, useMemo, useState } from 'react';
import { cardsApi } from '../shared/api/endpoints';
import type { CardDefinition, InventoryItem } from '../shared/api/types';
import { CardImage } from '../shared/ui/CardImage';
import { RarityBadge } from '../shared/ui/Badge';
import { Loader, PageError } from '../shared/ui/Loader';

export function CollectionPage() {
  const [catalog, setCatalog] = useState<CardDefinition[]>([]);
  const [owned, setOwned] = useState<Map<number, InventoryItem>>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([cardsApi.catalog(), cardsApi.inventory()])
      .then(([c, inv]) => {
        setCatalog(c);
        const m = new Map<number, InventoryItem>();
        inv.items.forEach((i) => m.set(i.cardDefinitionId, i));
        setOwned(m);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const uniqueStudents = useMemo(() => {
    const usernames = new Set<string>();
    catalog.forEach((card) => {
      if (owned.has(card.id) && card.telegramUsername) usernames.add(card.telegramUsername);
    });
    return usernames.size;
  }, [catalog, owned]);

  if (loading) return <Loader />;
  if (error) return <PageError message={error} onRetry={() => window.location.reload()} />;

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Коллекция</h1>
      <p className="text-sm text-zinc-400">Собрано {owned.size} / {catalog.length} карт · Уникальных студентов: {uniqueStudents}</p>
      <div className="grid grid-cols-2 gap-3">
        {catalog.map((card) => {
          const item = owned.get(card.id);
          const has = !!item;
          return (
            <article key={card.id} className="rounded-xl bg-zinc-800/60 p-2">
              <CardImage title={card.title} imageUrl={card.imageUrl} rarity={card.rarity} grayscale={!has} className="h-32 w-full" />
              <div className="mt-2 flex items-start justify-between gap-1">
                <p className="text-sm font-semibold leading-tight">{card.title}</p>
                <RarityBadge rarity={card.rarity} />
              </div>
              {has && <p className="text-xs text-zinc-500">×{item.quantity}</p>}
            </article>
          );
        })}
      </div>
    </div>
  );
}
