import { useEffect, useState } from 'react';
import { cardsApi, packsApi, userApi } from '../shared/api/endpoints';
import type { CardDefinition, DroppedCard, Pack } from '../shared/api/types';
import { PackOpenAnimation } from '../features/pack/PackOpenAnimation';
import { haptic } from '../shared/lib/telegram';
import { useUserStore } from '../shared/store/userStore';
import { Button } from '../shared/ui/Button';
import { Loader, PageError } from '../shared/ui/Loader';

export function ShopPage() {
  const setUser = useUserStore((s) => s.setUser);
  const user = useUserStore((s) => s.user);
  const [packs, setPacks] = useState<Pack[]>([]);
  const [catalog, setCatalog] = useState<CardDefinition[]>([]);
  const [loading, setLoading] = useState(true);
  const [opening, setOpening] = useState<number | null>(null);
  const [animResult, setAnimResult] = useState<DroppedCard | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([packsApi.list(), cardsApi.catalog()])
      .then(([p, c]) => {
        setPacks(p);
        setCatalog(c);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const openPack = async (pack: Pack) => {
    if (opening || !user || user.balanceCoins < pack.priceCoins) return;
    setOpening(pack.id);
    setError(null);
    setAnimResult(null);
    try {
      const spinMs = 2800;
      const [res] = await Promise.all([
        packsApi.open(pack.id, `open-${pack.id}-${Date.now()}`),
        new Promise((r) => setTimeout(r, spinMs)),
      ]);
      setAnimResult(res.droppedCard);
      haptic('success');
      setUser(await userApi.me());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
      haptic('error');
    } finally {
      setOpening(null);
    }
  };

  if (loading) return <Loader />;

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Магазин паков</h1>
      {error && <PageError message={error} />}
      <div className="grid gap-4">
        {packs.map((pack) => {
          const premium = pack.name.toLowerCase().includes('премиум') || pack.priceCoins >= 400;
          return (
            <article key={pack.id} className="rounded-2xl border border-zinc-700 bg-zinc-800/80 p-4">
              <div className="flex items-center justify-between">
                <h2 className="text-lg font-bold">{pack.name}</h2>
                {premium && (
                  <span className="rounded bg-amber-500/20 px-2 py-0.5 text-xs text-amber-300">
                    ↑ редкие
                  </span>
                )}
              </div>
              <p className="text-amber-300">🪙 {pack.priceCoins}</p>
              <Button
                className="mt-3 w-full"
                loading={opening === pack.id}
                disabled={!user || user.balanceCoins < pack.priceCoins}
                onClick={() => openPack(pack)}
              >
                Открыть
              </Button>
            </article>
          );
        })}
      </div>
      {opening && catalog.length > 0 && !animResult && (
        <PackOpenAnimation
          catalog={catalog}
          result={{ cardDefinitionId: 0, title: '...', rarity: 'COMMON', imageUrl: null }}
          spinningOnly
          onClose={() => {}}
        />
      )}
      {animResult && catalog.length > 0 && (
        <PackOpenAnimation catalog={catalog} result={animResult} onClose={() => setAnimResult(null)} />
      )}
    </div>
  );
}
