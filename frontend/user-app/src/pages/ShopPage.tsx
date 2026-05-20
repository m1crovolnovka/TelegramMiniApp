import { useEffect, useState } from 'react';
import { packsApi, userApi } from '../shared/api/endpoints';
import type { DroppedCard, Pack } from '../shared/api/types';
import { haptic } from '../shared/lib/telegram';
import { useUserStore } from '../shared/store/userStore';
import { RarityBadge } from '../shared/ui/Badge';
import { Button } from '../shared/ui/Button';
import { Loader, PageError } from '../shared/ui/Loader';

export function ShopPage() {
  const setUser = useUserStore((s) => s.setUser);
  const user = useUserStore((s) => s.user);
  const [packs, setPacks] = useState<Pack[]>([]);
  const [loading, setLoading] = useState(true);
  const [opening, setOpening] = useState<number | null>(null);
  const [dropped, setDropped] = useState<DroppedCard | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    packsApi
      .list()
      .then(setPacks)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const openPack = async (pack: Pack) => {
    if (opening || !user || user.balanceCoins < pack.priceCoins) return;
    setOpening(pack.id);
    setError(null);
    try {
      const key = `open-${pack.id}-${Date.now()}`;
      const res = await packsApi.open(pack.id, key);
      setDropped(res.droppedCard);
      haptic('success');
      const me = await userApi.me();
      setUser(me);
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
        {packs.map((pack) => (
          <article key={pack.id} className="rounded-2xl border border-zinc-700 bg-zinc-800/80 p-4">
            <h2 className="text-lg font-bold">{pack.name}</h2>
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
        ))}
      </div>

      {dropped && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4">
          <div className="w-full max-w-sm rounded-2xl bg-zinc-900 p-6 text-center">
            <p className="mb-2 text-lg font-bold">Выпала карта!</p>
            <p className="text-2xl">{dropped.title}</p>
            <div className="mt-2 flex justify-center">
              <RarityBadge rarity={dropped.rarity} />
            </div>
            <Button className="mt-4 w-full" onClick={() => setDropped(null)}>
              Закрыть
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
