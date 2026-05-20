import { useState } from 'react';
import { casinoApi, userApi } from '../shared/api/endpoints';
import { haptic } from '../shared/lib/telegram';
import { useUserStore } from '../shared/store/userStore';
import { Button } from '../shared/ui/Button';
import { PageError } from '../shared/ui/Loader';

type Tab = 'slots' | 'roulette';

export function CasinoPage() {
  const [tab, setTab] = useState<Tab>('slots');
  const [bet, setBet] = useState(100);
  const [spinning, setSpinning] = useState(false);
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const setUser = useUserStore((s) => s.setUser);

  const refreshBalance = async () => {
    const me = await userApi.me();
    setUser(me);
  };

  const spinSlots = async () => {
    if (spinning) return;
    setSpinning(true);
    setError(null);
    setResult(null);
    try {
      const payout = await casinoApi.spin(bet);
      setResult(payout > 0 ? `Выигрыш: +${payout} 🪙` : 'Без выигрыша');
      haptic(payout > 0 ? 'success' : 'light');
      await refreshBalance();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
      haptic('error');
    } finally {
      setSpinning(false);
    }
  };

  const betRoulette = async (betType: string, numberValue?: number) => {
    if (spinning) return;
    setSpinning(true);
    setError(null);
    try {
      const res = await casinoApi.rouletteBet({ betType, numberValue, stake: bet });
      setResult(`Выпало: ${res.rolledValue}. Выплата: ${res.payoutCoins} 🪙`);
      haptic(res.payoutCoins > 0 ? 'success' : 'light');
      await refreshBalance();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setSpinning(false);
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
            className={`flex-1 rounded-lg py-2 text-sm font-medium ${
              tab === t ? 'bg-violet-600' : 'bg-zinc-800'
            }`}
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
        <div className="rounded-2xl bg-zinc-800/80 p-6 text-center">
          <p className="mb-4 text-4xl">🎰 🎰 🎰</p>
          <Button className="w-full" loading={spinning} onClick={spinSlots}>
            Крутить
          </Button>
        </div>
      ) : (
        <div className="space-y-2">
          <div className="grid grid-cols-2 gap-2">
            <Button variant="secondary" loading={spinning} onClick={() => betRoulette('RED')}>
              Красное
            </Button>
            <Button variant="secondary" loading={spinning} onClick={() => betRoulette('BLACK')}>
              Чёрное
            </Button>
            <Button variant="secondary" loading={spinning} onClick={() => betRoulette('ODD')}>
              Нечёт
            </Button>
            <Button variant="secondary" loading={spinning} onClick={() => betRoulette('EVEN')}>
              Чёт
            </Button>
          </div>
          <Button
            variant="ghost"
            loading={spinning}
            onClick={() => betRoulette('NUMBER', Math.floor(Math.random() * 37))}
          >
            Случайное число (0-36)
          </Button>
        </div>
      )}

      {result && <p className="rounded-lg bg-violet-900/40 p-3 text-center font-semibold">{result}</p>}
    </div>
  );
}
