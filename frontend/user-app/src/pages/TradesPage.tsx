import { useCallback, useEffect, useMemo, useState } from 'react';
import { tradesApi, userApi } from '../shared/api/endpoints';
import type { InventoryItem, PublicUser, Trade, TradeItem } from '../shared/api/types';
import { useUserStore } from '../shared/store/userStore';
import { CardImage } from '../shared/ui/CardImage';
import { Button } from '../shared/ui/Button';
import { Loader, PageError } from '../shared/ui/Loader';

function itemsFromUser(trade: Trade, userId: number): TradeItem[] {
  return trade.items.filter((it) => it.fromUserId === userId);
}

function cardInTrade(trade: Trade, cardId: number, fromUserId: number): boolean {
  return trade.items.some(
    (it) => it.fromUserId === fromUserId && it.cardDefinitionId === cardId && (it.quantity ?? 0) > 0,
  );
}

function coinsFromUser(trade: Trade, userId: number): number {
  return trade.items
    .filter((it) => it.fromUserId === userId && it.coinsAmount)
    .reduce((s, it) => s + (it.coinsAmount ?? 0), 0);
}

function TradeOfferPanel({
  title,
  subtitle,
  items,
  side,
}: {
  title: string;
  subtitle: string;
  items: TradeItem[];
  side: 'give' | 'receive';
}) {
  const border = side === 'give' ? 'border-violet-500/50 bg-violet-950/30' : 'border-emerald-500/50 bg-emerald-950/30';
  return (
    <div className={`flex min-h-[140px] flex-1 flex-col rounded-xl border-2 ${border} p-3`}>
      <p className="text-xs font-semibold uppercase tracking-wide text-zinc-400">{title}</p>
      <p className="mb-2 text-sm text-zinc-300">{subtitle}</p>
      {items.length === 0 ? (
        <p className="flex flex-1 items-center justify-center text-center text-xs text-zinc-500">
          {side === 'give' ? 'Нажмите карточки ниже, чтобы добавить в обмен' : 'Выберите, что хотите получить'}
        </p>
      ) : (
        <ul className="flex flex-1 flex-wrap gap-2 content-start">
          {items.map((it, i) => (
            <li
              key={`${it.cardDefinitionId}-${it.coinsAmount}-${i}`}
              className="rounded-lg bg-zinc-900/80 px-2 py-1 text-xs"
            >
              {it.coinsAmount != null && it.coinsAmount > 0 ? (
                <span className="text-amber-300">🪙 {it.coinsAmount.toLocaleString()}</span>
              ) : (
                <span>
                  {it.cardTitle ?? `Карта #${it.cardDefinitionId}`}
                  {it.quantity > 1 ? ` ×${it.quantity}` : ''}
                </span>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

function InventoryPicker({
  label,
  items,
  selectedCardIds,
  onPick,
  disabledIds,
}: {
  label: string;
  items: InventoryItem[];
  selectedCardIds: Set<number>;
  onPick: (card: InventoryItem) => void;
  disabledIds?: Set<number>;
}) {
  return (
    <div>
      <p className="mb-2 text-xs text-zinc-500">{label}</p>
      <div className="flex gap-2 overflow-x-auto pb-1">
        {items.map((c) => {
          const selected = selectedCardIds.has(c.cardDefinitionId);
          const disabled = c.locked || disabledIds?.has(c.cardDefinitionId);
          return (
            <button
              key={c.cardDefinitionId}
              type="button"
              disabled={disabled}
              onClick={() => onPick(c)}
              className={`relative w-[72px] shrink-0 rounded-lg transition ${
                selected
                  ? 'ring-2 ring-emerald-400 ring-offset-2 ring-offset-zinc-900'
                  : 'opacity-90 hover:opacity-100'
              } ${disabled ? 'cursor-not-allowed opacity-35' : ''}`}
            >
              <CardImage title={c.title} imageUrl={c.imageUrl} rarity={c.rarity} className="h-[88px]" />
              {selected && (
                <span className="absolute right-0.5 top-0.5 rounded bg-emerald-600 px-1 text-[10px] font-bold">
                  ✓
                </span>
              )}
              {c.quantity > 1 && (
                <span className="absolute bottom-0.5 left-0.5 rounded bg-black/70 px-1 text-[10px]">
                  ×{c.quantity}
                </span>
              )}
            </button>
          );
        })}
        {items.length === 0 && <p className="text-xs text-zinc-500">Пусто</p>}
      </div>
    </div>
  );
}

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

  const loadHistory = useCallback(
    () =>
      tradesApi
        .history()
        .then(setHistory)
        .catch((e) => setError(e.message)),
    [],
  );

  useEffect(() => {
    loadHistory();
  }, [loadHistory]);

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

  const refreshInventories = async (p: PublicUser) => {
    if (!me) return;
    const [mine, theirs] = await Promise.all([
      userApi.inventory(me.id),
      userApi.inventory(p.id),
    ]);
    setMyInv(mine.items);
    setPartnerInv(theirs.items);
  };

  const startTrade = async () => {
    const username = partnerUsername.trim().replace(/^@/, '');
    if (!username || !me || !partner) return;
    setLoading(true);
    setError(null);
    try {
      const t = await tradesApi.create(username);
      setTrade(t);
      await refreshInventories(partner);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setLoading(false);
    }
  };

  const refreshTrade = async (t: Trade) => {
    setTrade(t);
    if (partner) await refreshInventories(partner);
  };

  const addPartnerCard = async (cardId: number) => {
    if (!trade || !partner || trade.status !== 'DRAFT') return;
    if (cardInTrade(trade, cardId, partner.id)) return;
    await refreshTrade(await tradesApi.addCard(trade.id, cardId, 1, partner.id));
  };

  const addMyCard = async (cardId: number) => {
    if (!trade || !me || trade.status !== 'DRAFT') return;
    if (cardInTrade(trade, cardId, me.id)) return;
    await refreshTrade(await tradesApi.addCard(trade.id, cardId, 1, me.id));
  };

  const addMyCoins = async () => {
    if (!trade || !coinOffer) return;
    await refreshTrade(await tradesApi.addCoins(trade.id, Number(coinOffer)));
    setCoinOffer('');
  };

  const sendTrade = async () => {
    if (!trade) return;
    await refreshTrade(await tradesApi.send(trade.id));
    loadHistory();
  };

  const accept = async (id: number) => {
    await tradesApi.accept(id);
    loadHistory();
    setTrade(null);
    setPartner(null);
  };

  const reject = async (id: number) => {
    await tradesApi.reject(id);
    loadHistory();
  };

  const pendingForMe = useMemo(
    () => history.filter((t) => t.status === 'PENDING' && t.partnerUserId === me?.id),
    [history, me?.id],
  );

  const myOffer = trade && me ? itemsFromUser(trade, me.id) : [];
  const theirOffer = trade && partner ? itemsFromUser(trade, partner.id) : [];

  const mySelectedCards = useMemo(
    () => new Set(myOffer.filter((i) => i.cardDefinitionId).map((i) => i.cardDefinitionId!)),
    [myOffer],
  );
  const theirSelectedCards = useMemo(
    () => new Set(theirOffer.filter((i) => i.cardDefinitionId).map((i) => i.cardDefinitionId!)),
    [theirOffer],
  );

  const partnerLabel = partner?.username ? `@${partner.username}` : 'партнёр';

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Обмен</h1>
      <p className="text-sm text-zinc-400">
        Как в Steam: слева — что вы отдаёте, справа — что получаете. Зелёная рамка — уже в сделке.
      </p>
      {error && <PageError message={error} />}

      {pendingForMe.length > 0 && (
        <section className="space-y-3 rounded-xl border border-amber-600/50 bg-amber-900/20 p-4">
          <h2 className="font-semibold text-amber-300">Входящие предложения</h2>
          {pendingForMe.map((t) => {
            const fromThem = itemsFromUser(t, t.initiatorUserId);
            const fromMe = itemsFromUser(t, me!.id);
            return (
              <div key={t.id} className="rounded-xl bg-zinc-900/60 p-3">
                <p className="mb-2 text-sm font-medium">
                  От @{t.initiatorUsername ?? t.initiatorUserId}
                </p>
                <div className="mb-2 flex gap-2 text-xs">
                  <div className="flex-1 rounded-lg bg-violet-950/50 p-2">
                    <p className="text-zinc-500">Они отдают</p>
                    {fromThem.map((it, i) => (
                      <p key={i}>
                        {it.coinsAmount ? `🪙 ${it.coinsAmount}` : it.cardTitle ?? `#${it.cardDefinitionId}`}
                      </p>
                    ))}
                  </div>
                  <div className="flex-1 rounded-lg bg-emerald-950/50 p-2">
                    <p className="text-zinc-500">Вы отдаёте</p>
                    {fromMe.length === 0 ? (
                      <p className="text-zinc-500">—</p>
                    ) : (
                      fromMe.map((it, i) => (
                        <p key={i}>
                          {it.coinsAmount ? `🪙 ${it.coinsAmount}` : it.cardTitle ?? `#${it.cardDefinitionId}`}
                        </p>
                      ))
                    )}
                  </div>
                </div>
                <div className="flex gap-2">
                  <Button className="flex-1" onClick={() => accept(t.id)}>
                    Принять
                  </Button>
                  <Button variant="danger" className="flex-1" onClick={() => reject(t.id)}>
                    Отклонить
                  </Button>
                </div>
              </div>
            );
          })}
        </section>
      )}

      {!trade && (
        <section className="space-y-3 rounded-xl bg-zinc-800/80 p-4">
          <p className="text-sm text-zinc-400">Username партнёра в Telegram</p>
          <div className="flex gap-2">
            <input
              type="text"
              value={partnerUsername}
              onChange={(e) => setPartnerUsername(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && lookupPartner()}
              className="flex-1 rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
              placeholder="@username"
            />
            <Button variant="secondary" onClick={lookupPartner} loading={loading}>
              Найти
            </Button>
          </div>
          {partner && (
            <p className="text-sm text-green-400">
              Найден: @{partner.username ?? partner.id} · {partnerInv.length} карт в инвентаре
            </p>
          )}
          <Button className="w-full" onClick={startTrade} disabled={!partner} loading={loading}>
            Начать обмен
          </Button>
        </section>
      )}

      {trade && trade.status === 'DRAFT' && partner && me && (
        <section className="space-y-4 rounded-xl bg-zinc-800/80 p-4">
          <div className="flex items-center justify-between">
            <p className="font-semibold">
              Обмен с {partnerLabel} <span className="text-zinc-500">#{trade.id}</span>
            </p>
            <button
              type="button"
              className="text-xs text-zinc-500 underline"
              onClick={() => {
                setTrade(null);
              }}
            >
              Отменить
            </button>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row">
            <TradeOfferPanel
              title="Вы отдаёте"
              subtitle={me.username ? `@${me.username}` : `ID ${me.id}`}
              items={myOffer}
              side="give"
            />
            <div className="flex items-center justify-center text-2xl text-zinc-600 sm:px-1">⇄</div>
            <TradeOfferPanel
              title="Вы получаете"
              subtitle={partner.username ? `@${partner.username}` : `ID ${partner.id}`}
              items={theirOffer}
              side="receive"
            />
          </div>

          {coinsFromUser(trade, me.id) > 0 && (
            <p className="text-center text-sm text-amber-300">
              В обмене ваши монеты: 🪙 {coinsFromUser(trade, me.id).toLocaleString()}
            </p>
          )}

          <InventoryPicker
            label="Ваш инвентарь — нажмите, чтобы отдать"
            items={myInv}
            selectedCardIds={mySelectedCards}
            onPick={(c) => addMyCard(c.cardDefinitionId)}
          />

          <InventoryPicker
            label={`Инвентарь ${partnerLabel} — нажмите, чтобы запросить`}
            items={partnerInv}
            selectedCardIds={theirSelectedCards}
            onPick={(c) => addPartnerCard(c.cardDefinitionId)}
          />

          <div className="flex gap-2 border-t border-zinc-700 pt-3">
            <input
              type="number"
              min={0}
              placeholder="Монеты от вас"
              value={coinOffer}
              onChange={(e) => setCoinOffer(e.target.value)}
              className="flex-1 rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
            />
            <Button variant="secondary" onClick={addMyCoins} disabled={!coinOffer}>
              Добавить 🪙
            </Button>
          </div>

          <Button
            className="w-full"
            onClick={sendTrade}
            disabled={myOffer.length === 0 && theirOffer.length === 0}
          >
            Отправить предложение
          </Button>
        </section>
      )}

      {trade && trade.status === 'PENDING' && (
        <p className="rounded-xl bg-amber-900/30 p-3 text-sm text-amber-200">
          Предложение отправлено. Ожидайте ответа @{trade.partnerUsername ?? trade.partnerUserId}.
        </p>
      )}

      <section>
        <h2 className="mb-2 font-semibold">История</h2>
        {loading && <Loader />}
        <ul className="space-y-2 text-sm">
          {history.map((t) => (
            <li key={t.id} className="rounded-xl bg-zinc-800/60 px-3 py-2">
              <span className="font-medium">#{t.id}</span> · {t.status}
              <br />
              <span className="text-zinc-400">
                @{t.initiatorUsername ?? t.initiatorUserId} ↔ @{t.partnerUsername ?? t.partnerUserId}
              </span>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
