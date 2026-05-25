import { useState } from 'react';
import { casinoApi, userApi } from '../shared/api/endpoints';
import { RouletteWheel } from '../features/casino/RouletteWheel';
import { SlotMachine, type SlotVariant } from '../features/casino/SlotMachine';
import { haptic } from '../shared/lib/telegram';
import { useUserStore } from '../shared/store/userStore';
import { Button } from '../shared/ui/Button';
import { PageError } from '../shared/ui/Loader';

type Tab = 'slots' | 'roulette';

const SLOT_VARIANTS: { id: SlotVariant; label: string }[] = [
  { id: 'sweet-bonanza', label: 'Sweet Bonanza' },
  { id: 'dog-house', label: 'Dog House' },
  { id: 'gates-olympus', label: 'Gates of Olympus' },
];

export function CasinoPage() {
  const [tab, setTab] = useState<Tab>('slots');
  const [slotVariant, setSlotVariant] = useState<SlotVariant>('sweet-bonanza');
  const [bet, setBet] = useState(100);
  const [busy, setBusy] = useState(false);
  const [slotSpinning, setSlotSpinning] = useState(false);
  const [slotSymbols, setSlotSymbols] = useState<string[] | undefined>();
  const [rouletteSpinning, setRouletteSpinning] = useState(false);
  const [rouletteValue, setRouletteValue] = useState<number | undefined>();
  const [rouletteSpinToken, setRouletteSpinToken] = useState(0);
  const [roulettePayout, setRoulettePayout] = useState<number | null>(null);
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const setUser = useUserStore((s) => s.setUser);

  const spinSlots = async () => {
    if (busy) return;
    setBusy(true);
    setError(null);
    setResult(null);
    setSlotSpinning(true);
    setSlotSymbols(undefined);
    const spinMs = 2800;
    const start = Date.now();
    try {
      const resPromise = casinoApi.spin(bet, slotVariant);
      await new Promise((r) => setTimeout(r, spinMs));
      const res = await resPromise;
      setSlotSymbols(res.symbols);
      setResult(res.payout > 0 ? `Выигрыш: +${res.payout} 🪙` : 'Без выигрыша');
      haptic(res.payout > 0 ? 'success' : 'light');
      setUser(await userApi.me());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      const elapsed = Date.now() - start;
      if (elapsed < spinMs) await new Promise((r) => setTimeout(r, spinMs - elapsed));
      setSlotSpinning(false);
      setBusy(false);
    }
  };

  const betRoulette = async (betType: string, numberValue?: number) => {
    if (busy) return;
    setBusy(true);
    setError(null);
    setResult(null);
    setRoulettePayout(null);
    setRouletteValue(undefined);
    setRouletteSpinning(false);
    try {
      const res = await casinoApi.rouletteBet({ betType, numberValue, stake: bet });
      setRouletteSpinToken((t) => t + 1);
      setRouletteValue(res.rolledValue);
      setRoulettePayout(res.payoutCoins);
      requestAnimationFrame(() => setRouletteSpinning(true));
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
      setBusy(false);
    }
  };

  const onRouletteEnd = () => {
    setRouletteSpinning(false);
    setBusy(false);
    if (rouletteValue !== undefined) {
      setResult(
        `Выпало: ${rouletteValue}. ${
          roulettePayout && roulettePayout > 0 ? `Выигрыш +${roulettePayout} 🪙` : 'Без выигрыша'
        }`,
      );
      userApi.me().then(setUser);
    }
  };

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Казино</h1>
      <div className="flex gap-2">
        {(['slots', 'roulette'] as Tab[]).map((t) => (
          <button
            key={t}
            type="button"
            onClick={() => setTab(t)}
            className={`flex-1 rounded-lg py-2 text-sm font-medium ${tab === t ? 'bg-violet-600' : 'bg-zinc-800'}`}
          >
            {t === 'slots' ? 'Слоты' : 'Рулетка'}
          </button>
        ))}
      </div>
      <label className="block text-sm text-zinc-400">
        Ставка
        <input
          type="number"
          min={1}
          value={bet}
          onChange={(e) => setBet(Number(e.target.value))}
          className="mt-1 w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
      </label>
      {error && <PageError message={error} />}
      {tab === 'slots' ? (
        <div className="space-y-4">
          <div className="flex gap-1 overflow-x-auto pb-1">
            {SLOT_VARIANTS.map((v) => (
              <button
                key={v.id}
                type="button"
                onClick={() => setSlotVariant(v.id)}
                className={`shrink-0 rounded-lg px-3 py-1.5 text-xs font-medium ${
                  slotVariant === v.id ? 'bg-violet-600 text-white' : 'bg-zinc-800 text-zinc-400'
                }`}
              >
                {v.label}
              </button>
            ))}
          </div>
          <SlotMachine variant={slotVariant} spinning={slotSpinning} resultSymbols={slotSymbols} />
          <Button className="w-full" loading={busy} onClick={spinSlots}>
            Крутить
          </Button>
        </div>
      ) : (
        <div className="space-y-4 pb-10">
          <RouletteWheel
            spinning={rouletteSpinning}
            resultValue={rouletteValue}
            spinToken={rouletteSpinToken}
            onSpinEnd={onRouletteEnd}
          />
          <div className="text-center text-lg font-bold text-amber-300 min-h-7">
            {rouletteValue !== undefined && !rouletteSpinning ? `Выпало: ${rouletteValue}` : rouletteSpinning ? 'Крутим...' : ''}
          </div>
          <div className="grid grid-cols-2 gap-2">
            <Button variant="secondary" loading={busy} onClick={() => betRoulette('RED')}>
              Красное
            </Button>
            <Button variant="secondary" loading={busy} onClick={() => betRoulette('BLACK')}>
              Чёрное
            </Button>
            <Button variant="secondary" loading={busy} onClick={() => betRoulette('ODD')}>
              Нечёт
            </Button>
            <Button variant="secondary" loading={busy} onClick={() => betRoulette('EVEN')}>
              Чёт
            </Button>
            <Button variant="secondary" loading={busy} onClick={() => betRoulette('NUMBER', 0)}>
              0 (zero)
            </Button>
          </div>
          <div className="max-h-40 overflow-y-auto rounded-lg border border-zinc-700 p-2">
            <p className="mb-2 text-xs text-zinc-400">Ставка на число (0–36)</p>
            <div className="grid grid-cols-6 gap-1">
              {Array.from({ length: 37 }, (_, n) => (
                <button
                  key={n}
                  type="button"
                  disabled={busy}
                  onClick={() => betRoulette('NUMBER', n)}
                  className={`rounded px-1 py-1 text-xs font-semibold ${
                    n === 0
                      ? 'bg-green-700'
                      : [1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36].includes(n)
                        ? 'bg-red-800'
                        : 'bg-zinc-800'
                  }`}
                >
                  {n}
                </button>
              ))}
            </div>
          </div>
        </div>
      )}
      {result && <p className="rounded-lg bg-violet-900/40 p-3 text-center font-semibold">{result}</p>}
    </div>
  );
}
