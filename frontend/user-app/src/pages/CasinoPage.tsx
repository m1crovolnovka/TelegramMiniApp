import { useState } from 'react';
import { casinoApi, userApi } from '../shared/api/endpoints';
import { RouletteWheel } from '../features/casino/RouletteWheel';
import { SlotMachine } from '../features/casino/SlotMachine';
import { haptic } from '../shared/lib/telegram';
import { useUserStore } from '../shared/store/userStore';
import { Button } from '../shared/ui/Button';
import { PageError } from '../shared/ui/Loader';

type Tab = 'slots' | 'roulette';

function payoutToSymbols(payout: number, bet: number): string[] {
  if (payout <= 0) return ['BAR', '🍒', '🔔'];
  const mult = payout / bet;
  if (mult >= 10) return ['7', '7', '7'];
  if (mult >= 3) return ['🔔', '🔔', '🔔'];
  return ['🍒', '🍒', '🍒'];
}

export function CasinoPage() {
  const [tab, setTab] = useState<Tab>('slots');
  const [bet, setBet] = useState(100);
  const [busy, setBusy] = useState(false);
  const [slotSpinning, setSlotSpinning] = useState(false);
  const [slotSymbols, setSlotSymbols] = useState<string[] | undefined>();
  const [rouletteSpinning, setRouletteSpinning] = useState(false);
  const [rouletteValue, setRouletteValue] = useState<number | undefined>();
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
    const spinMs = 3000;
    const start = Date.now();
    try {
      const payoutPromise = casinoApi.spin(bet);
      await new Promise((r) => setTimeout(r, spinMs));
      const payout = await payoutPromise;
      setSlotSymbols(payoutToSymbols(payout, bet));
      setResult(payout > 0 ? `Выигрыш: +${payout} 🪙` : 'Без выигрыша');
      haptic(payout > 0 ? 'success' : 'light');
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
      setRouletteValue(res.rolledValue);
      setRoulettePayout(res.payoutCoins);
      setRouletteSpinning(true);
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
        `Выпало: ${rouletteValue}. ${roulettePayout && roulettePayout > 0 ? `Выигрыш +${roulettePayout} 🪙` : 'Без выигрыша'}`,
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
          <SlotMachine spinning={slotSpinning} resultSymbols={slotSymbols} />
          <Button className="w-full" loading={busy} onClick={spinSlots}>
            Крутить (3 сек)
          </Button>
        </div>
      ) : (
        <div className="space-y-4 pb-10">
          <RouletteWheel
            spinning={rouletteSpinning}
            resultValue={rouletteValue}
            onSpinEnd={onRouletteEnd}
          />
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
          </div>
        </div>
      )}
      {result && <p className="rounded-lg bg-violet-900/40 p-3 text-center font-semibold">{result}</p>}
    </div>
  );
}
