import { useEffect, useState } from 'react';
import { tradesApi, userApi } from '../shared/api/endpoints';
import type { InventoryItem, PublicUser, Trade } from '../shared/api/types';
import { useUserStore } from '../shared/store/userStore';
import { CardImage } from '../shared/ui/CardImage';
import { Button } from '../shared/ui/Button';
import { Loader, PageError } from '../shared/ui/Loader';

export function TradesPage() {
  const me = useUserStore((s) => s.user);
  const [partnerUsername, setPartnerUsername] = useState('');
  const [partner, setPartner] = useState<PublicUser | null>(null);
  const [myInv, setMyInv] = useState<InventoryItem[]>([]);
  const [partnerInv, setPartnerInv] = useState<InventoryItem[]>([]);
  const [trade, setTrade] = useState<Trade | null>(null);
  const [history, setHistory] = useState<Trade[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [coinOffer, setCoinOffer] = useState('');

  const loadHistory = () =>
    tradesApi
      .history()
      .then(setHistory)
      .catch((e) => setError(e.message));

  useEffect(() => {
    loadHistory();
  }, []);

  const lookupPartner = async () => {
    const username = partnerUsername.trim().replace(/^@/, '');
    if (!username) return;
    setLoading(true);
    setError(null);
    try {
      const u = await userApi.byUsername(username);
      const inv = await userApi.inventory(u.id);
      setPartner(u);
      setPartnerInv(inv.items);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Пользователь не найден');
      setPartner(null);
    } finally {
      setLoading(false);
    }
  };

  const startTrade = async () => {
    const username = partnerUsername.trim().replace(/^@/, '');
    if (!username || !me) return;
    setLoading(true);
    try {
      const t = await tradesApi.create(username);
      setTrade(t);
      const mine = await userApi.inventory(me.id);
      setMyInv(mine.items);
      if (partner) {
        const inv = await userApi.inventory(partner.id);
        setPartnerInv(inv.items);
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setLoading(false);
    }
  };

  const addPartnerCard = async (cardId: number) => {
    if (!trade || !partner) return;
    const t = await tradesApi.addCard(trade.id, cardId, 1, partner.id);
    setTrade(t);
  };

  const addMyCard = async (cardId: number) => {
    if (!trade || !me) return;
    const t = await tradesApi.addCard(trade.id, cardId, 1, me.id);
    setTrade(t);
  };

  const addMyCoins = async () => {
    if (!trade || !coinOffer) return;
    const t = await tradesApi.addCoins(trade.id, Number(coinOffer));
    setTrade(t);
    setCoinOffer('');
  };

  const sendTrade = async () => {
    if (!trade) return;
    const t = await tradesApi.send(trade.id);
    setTrade(t);
    loadHistory();
  };

  const accept = async (id: number) => {
    await tradesApi.accept(id);
    loadHistory();
    setTrade(null);
  };

  const reject = async (id: number) => {
    await tradesApi.reject(id);
    loadHistory();
  };

  const pendingForMe = history.filter(
    (t) => t.status === 'PENDING' && t.partnerUserId === me?.id,
  );

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Трейды</h1>
      {error && <PageError message={error} />}

      {pendingForMe.length > 0 && (
        <section className="rounded-xl border border-amber-600/50 bg-amber-900/20 p-4">
          <h2 className="mb-2 font-semibold text-amber-300">Входящие предложения</h2>
          {pendingForMe.map((t) => (
            <div key={t.id} className="mb-2 text-sm">
              <p>Трейд #{t.id} от user {t.initiatorUserId}</p>
              <ul className="text-xs text-zinc-400">
                {t.items.map((it, i) => (
                  <li key={i}>
                    {it.coinsAmount
                      ? `${it.coinsAmount} 🪙 от #${it.fromUserId}`
                      : `Карта #${it.cardDefinitionId} от #${it.fromUserId}`}
                  </li>
                ))}
              </ul>
              <div className="mt-2 flex gap-2">
                <Button className="flex-1" onClick={() => accept(t.id)}>
                  Принять
                </Button>
                <Button variant="danger" className="flex-1" onClick={() => reject(t.id)}>
                  Отклонить
                </Button>
              </div>
            </div>
          ))}
        </section>
      )}

      {!trade && (
        <section className="space-y-2 rounded-xl bg-zinc-800/80 p-4">
          <p className="text-sm text-zinc-400">Telegram username партнёра</p>
          <div className="flex gap-2">
            <input
              type="text"
              value={partnerUsername}
              onChange={(e) => setPartnerUsername(e.target.value)}
              className="flex-1 rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
              placeholder="@username"
            />
            <Button variant="secondary" onClick={lookupPartner} loading={loading}>
              Найти
            </Button>
          </div>
          {partner && (
            <p className="text-sm text-green-400">@{partner.username ?? partner.id}</p>
          )}
          {partnerInv.length > 0 && (
            <div>
              <p className="mb-2 text-xs text-zinc-500">Инвентарь партнёра (что хотите получить)</p>
              <div className="flex gap-2 overflow-x-auto pb-2">
                {partnerInv.map((c) => (
                  <div key={c.cardDefinitionId} className="w-20 shrink-0">
                    <CardImage
                      title={c.title}
                      imageUrl={c.imageUrl}
                      rarity={c.rarity}
                      className="h-24"
                    />
                  </div>
                ))}
              </div>
            </div>
          )}
          <Button className="w-full" onClick={startTrade} disabled={!partner}>
            Создать трейд
          </Button>
        </section>
      )}

      {trade && trade.status === 'DRAFT' && partner && me && (
        <section className="space-y-3 rounded-xl bg-zinc-800/80 p-4">
          <p className="font-semibold">Трейд #{trade.id}</p>
          <div>
            <p className="mb-1 text-xs text-amber-400">Запросить у партнёра</p>
            <div className="flex gap-2 overflow-x-auto">
              {partnerInv.map((c) => (
                <button
                  key={c.cardDefinitionId}
                  type="button"
                  onClick={() => addPartnerCard(c.cardDefinitionId)}
                  className="w-20 shrink-0"
                >
                  <CardImage title={c.title} imageUrl={c.imageUrl} rarity={c.rarity} className="h-24" />
                </button>
              ))}
            </div>
          </div>
          <div>
            <p className="mb-1 text-xs text-violet-400">Отдать с вашей стороны</p>
            <div className="flex gap-2 overflow-x-auto">
              {myInv.map((c) => (
                <button
                  key={c.cardDefinitionId}
                  type="button"
                  disabled={c.locked}
                  onClick={() => addMyCard(c.cardDefinitionId)}
                  className="w-20 shrink-0 disabled:opacity-40"
                >
                  <CardImage title={c.title} imageUrl={c.imageUrl} rarity={c.rarity} className="h-24" />
                </button>
              ))}
            </div>
          </div>
          <div className="flex gap-2">
            <input
              type="number"
              placeholder="Коины от вас"
              value={coinOffer}
              onChange={(e) => setCoinOffer(e.target.value)}
              className="flex-1 rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
            />
            <Button variant="secondary" onClick={addMyCoins}>
              + монеты
            </Button>
          </div>
          {trade.items.length > 0 && (
            <ul className="text-xs text-zinc-400">
              {trade.items.map((it, i) => (
                <li key={i}>
                  {it.coinsAmount
                    ? `${it.coinsAmount} 🪙 от #${it.fromUserId}`
                    : `Карта #${it.cardDefinitionId} от #${it.fromUserId}`}
                </li>
              ))}
            </ul>
          )}
          <Button className="w-full" onClick={sendTrade}>
            Отправить партнёру
          </Button>
        </section>
      )}

      <section>
        <h2 className="mb-2 font-semibold">История</h2>
        {loading && <Loader />}
        <ul className="space-y-2">
          {history.map((t) => (
            <li key={t.id} className="rounded-xl bg-zinc-800/60 p-3 text-sm">
              #{t.id} {t.status} — {t.initiatorUserId} ↔ {t.partnerUserId}
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
