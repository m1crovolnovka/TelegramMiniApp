import { useEffect, useState } from 'react';
import { cardsApi, packsApi, userApi } from '../shared/api/endpoints';
import type { CardDefinition, DroppedCard, Pack } from '../shared/api/types';
import { PackOpenAnimation } from '../features/pack/PackOpenAnimation';
import { haptic } from '../shared/lib/telegram';
import { useUserStore } from '../shared/store/userStore';
import { Button } from '../shared/ui/Button';
import { CardImage } from '../shared/ui/CardImage';
import { Loader, PageError } from '../shared/ui/Loader';

export function ShopPage() {
  const setUser = useUserStore((s) => s.setUser);
  const user = useUserStore((s) => s.user);
  const [packs, setPacks] = useState<Pack[]>([]);
  const [catalog, setCatalog] = useState<CardDefinition[]>([]);
  const [loading, setLoading] = useState(true);
  const [opening, setOpening] = useState<number | null>(null);
  const [animResult, setAnimResult] = useState<DroppedCard | null>(null);
  const [bundleResults, setBundleResults] = useState<DroppedCard[] | null>(null);
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
    setBundleResults(null);
    try {
      const spinMs = pack.packKind === 'BUNDLE' ? 1800 : 2800;
      const [res] = await Promise.all([
        packsApi.open(pack.id, `open-${pack.id}-${Date.now()}`),
        new Promise((r) => setTimeout(r, spinMs)),
      ]);
      if (res.packKind === 'BUNDLE' && res.droppedCards.length > 0) {
        setBundleResults(res.droppedCards);
      } else {
        setAnimResult(res.droppedCard);
      }
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
          const premium =
            pack.packKind === 'SINGLE' &&
            (pack.name.toLowerCase().includes('премиум') || pack.priceCoins >= 400);
          const bundle = pack.packKind === 'BUNDLE';
          return (
            <article key={pack.id} className="rounded-2xl border border-zinc-700 bg-zinc-800/80 p-4">
              <div className="flex items-center justify-between">
                <h2 className="text-lg font-bold">{pack.name}</h2>
                {bundle && (
                  <span className="rounded bg-violet-500/20 px-2 py-0.5 text-xs text-violet-300">
                    набор
                  </span>
                )}
                {premium && (
                  <span className="rounded bg-amber-500/20 px-2 py-0.5 text-xs text-amber-300">
                    ↑ редкие
                  </span>
                )}
              </div>
              {pack.bundleSummary && (
                <p className="mt-1 text-sm text-zinc-400">{pack.bundleSummary}</p>
              )}
              <p className="text-amber-300">🪙 {pack.priceCoins}</p>
              <Button
                className="mt-3 w-full"
                loading={opening === pack.id}
                disabled={!user || user.balanceCoins < pack.priceCoins}
                onClick={() => openPack(pack)}
              >
                {bundle ? 'Открыть набор' : 'Открыть'}
              </Button>
            </article>
          );
        })}
      </div>
      {opening && catalog.length > 0 && !animResult && !bundleResults && (
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
      {bundleResults && bundleResults.length > 0 && (
        <section className="rounded-2xl border border-violet-600/40 bg-zinc-900/90 p-4">
          <h2 className="mb-3 text-lg font-bold text-violet-200">Выпало из набора</h2>
          <div className="grid grid-cols-3 gap-2 sm:grid-cols-4">
            {bundleResults.map((c) => (
              <div key={`${c.cardDefinitionId}-${c.title}`} className="text-center">
                <CardImage title={c.title} imageUrl={c.imageUrl} rarity={c.rarity} className="h-[88px]" />
                <p className="mt-1 truncate text-[10px] text-zinc-400">{c.title}</p>
              </div>
            ))}
          </div>
          <Button className="mt-4 w-full" variant="secondary" onClick={() => setBundleResults(null)}>
            Закрыть
          </Button>
        </section>
      )}
    </div>
  );
}
