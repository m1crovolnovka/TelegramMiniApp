import { useEffect, useMemo, useState } from 'react';

export type SlotVariant = 'sweet-bonanza' | 'dog-house' | 'gates-olympus';

const THEMES: Record<
  SlotVariant,
  { title: string; symbols: string[]; frame: string; reel: string; glow: string }
> = {
  'sweet-bonanza': {
    title: 'Sweet Bonanza',
    symbols: ['🍇', '🍬', '💣', '🍭', '💎'],
    frame: 'from-pink-900/80 via-purple-900/60 to-fuchsia-900/80',
    reel: 'border-pink-400 bg-gradient-to-b from-pink-950 to-purple-950',
    glow: 'shadow-[0_0_24px_rgba(236,72,153,0.45)]',
  },
  'dog-house': {
    title: 'The Dog House',
    symbols: ['🐕', '🦴', '🏠', '🐾', '💎'],
    frame: 'from-amber-900/80 via-orange-900/60 to-yellow-900/70',
    reel: 'border-amber-400 bg-gradient-to-b from-amber-950 to-orange-950',
    glow: 'shadow-[0_0_24px_rgba(251,191,36,0.4)]',
  },
  'gates-olympus': {
    title: 'Gates of Olympus',
    symbols: ['⚡', '👑', '🏛️', '💎', '🔱'],
    frame: 'from-indigo-900/80 via-violet-900/70 to-blue-900/80',
    reel: 'border-violet-300 bg-gradient-to-b from-indigo-950 to-violet-950',
    glow: 'shadow-[0_0_28px_rgba(167,139,250,0.5)]',
  },
};

interface Props {
  variant: SlotVariant;
  spinning: boolean;
  resultSymbols?: string[];
}

export function SlotMachine({ variant, spinning, resultSymbols }: Props) {
  const theme = THEMES[variant];
  const [reels, setReels] = useState(['?', '?', '?']);

  const pool = useMemo(() => theme.symbols, [theme.symbols]);

  useEffect(() => {
    if (!spinning) {
      if (resultSymbols?.length === 3) setReels(resultSymbols);
      return;
    }
    const id = setInterval(() => {
      setReels([
        pool[Math.floor(Math.random() * pool.length)],
        pool[Math.floor(Math.random() * pool.length)],
        pool[Math.floor(Math.random() * pool.length)],
      ]);
    }, 70);
    return () => clearInterval(id);
  }, [spinning, resultSymbols, pool]);

  return (
    <div className={`rounded-2xl bg-gradient-to-br p-4 ${theme.frame} ${theme.glow}`}>
      <p className="mb-3 text-center text-sm font-bold tracking-wide text-white/90">{theme.title}</p>
      <div className="flex justify-center gap-2 sm:gap-3">
        {reels.map((s, i) => (
          <div
            key={i}
            className={`flex h-24 w-20 items-center justify-center rounded-xl border-2 text-3xl font-bold sm:h-28 sm:w-24 sm:text-4xl ${theme.reel} ${
              spinning ? 'animate-pulse' : ''
            }`}
            style={{ animationDelay: `${i * 0.08}s` }}
          >
            {s}
          </div>
        ))}
      </div>
    </div>
  );
}
