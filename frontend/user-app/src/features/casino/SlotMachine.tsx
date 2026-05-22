import { useEffect, useState } from 'react';

const SYMBOLS = ['BAR', '🍒', '🔔', '7'];

interface Props {
  spinning: boolean;
  resultSymbols?: string[];
}

export function SlotMachine({ spinning, resultSymbols }: Props) {
  const [reels, setReels] = useState(['BAR', '🍒', '🔔']);

  useEffect(() => {
    if (!spinning) {
      if (resultSymbols?.length === 3) setReels(resultSymbols);
      return;
    }
    const id = setInterval(() => {
      setReels([
        SYMBOLS[Math.floor(Math.random() * SYMBOLS.length)],
        SYMBOLS[Math.floor(Math.random() * SYMBOLS.length)],
        SYMBOLS[Math.floor(Math.random() * SYMBOLS.length)],
      ]);
    }, 80);
    return () => clearInterval(id);
  }, [spinning, resultSymbols]);

  return (
    <div className="flex justify-center gap-3 rounded-2xl bg-zinc-950 p-6">
      {reels.map((s, i) => (
        <div
          key={i}
          className={`flex h-20 w-20 items-center justify-center rounded-xl border-2 border-violet-600 bg-zinc-800 text-2xl font-bold ${spinning ? 'animate-bounce' : ''}`}
          style={{ animationDelay: `${i * 0.1}s` }}
        >
          {s}
        </div>
      ))}
    </div>
  );
}
