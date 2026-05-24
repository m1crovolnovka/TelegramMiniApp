import { useEffect, useMemo, useRef, useState } from 'react';

/** European roulette wheel order (clockwise), index 0 = pocket at top after spin settles. */
export const ROULETTE_WHEEL_ORDER = [
  0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14,
  31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26,
] as const;

const RED_NUMBERS = new Set([
  1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36,
]);

function pocketColor(n: number): string {
  if (n === 0) return '#16a34a';
  return RED_NUMBERS.has(n) ? '#dc2626' : '#18181b';
}

interface Props {
  spinning: boolean;
  resultValue?: number;
  onSpinEnd?: () => void;
}

export function RouletteWheel({ spinning, resultValue, onSpinEnd }: Props) {
  const [rotation, setRotation] = useState(0);
  const prevSpinning = useRef(false);
  const segmentAngle = 360 / ROULETTE_WHEEL_ORDER.length;

  const conicGradient = useMemo(() => {
    const stops = ROULETTE_WHEEL_ORDER.map((num, i) => {
      const start = (i * 100) / ROULETTE_WHEEL_ORDER.length;
      const end = ((i + 1) * 100) / ROULETTE_WHEEL_ORDER.length;
      return `${pocketColor(num)} ${start}% ${end}%`;
    });
    return `conic-gradient(from -90deg, ${stops.join(', ')})`;
  }, []);

  useEffect(() => {
    if (spinning && !prevSpinning.current && resultValue !== undefined) {
      const index = ROULETTE_WHEEL_ORDER.indexOf(resultValue as (typeof ROULETTE_WHEEL_ORDER)[number]);
      const safeIndex = index >= 0 ? index : 0;
      const target = 360 - safeIndex * segmentAngle - segmentAngle / 2;
      setRotation((r) => r + 5 * 360 + target);
    }
    prevSpinning.current = spinning;
  }, [spinning, resultValue, segmentAngle]);

  useEffect(() => {
    if (!spinning) return;
    const t = setTimeout(() => onSpinEnd?.(), 4000);
    return () => clearTimeout(t);
  }, [spinning, onSpinEnd]);

  return (
    <div className="relative mx-auto flex h-72 w-72 items-center justify-center">
      <div className="absolute -top-3 z-10 text-3xl text-amber-400 drop-shadow">▼</div>
      <div
        className="relative h-full w-full rounded-full border-4 border-amber-500 transition-transform duration-[4000ms] ease-out"
        style={{
          transform: `rotate(${rotation}deg)`,
          background: conicGradient,
        }}
      >
        {ROULETTE_WHEEL_ORDER.map((num, i) => {
          const angle = i * segmentAngle + segmentAngle / 2 - 90;
          return (
            <span
              key={`${num}-${i}`}
              className="absolute left-1/2 top-1/2 text-[9px] font-bold text-white"
              style={{
                transform: `rotate(${angle}deg) translateY(-108px) rotate(${-angle}deg)`,
                transformOrigin: '0 0',
              }}
            >
              {num}
            </span>
          );
        })}
      </div>
      <div className="absolute h-14 w-14 rounded-full border-2 border-amber-300 bg-amber-600" />
      {!spinning && resultValue !== undefined && (
        <p className="absolute -bottom-8 w-full text-center text-sm font-bold">
          Выпало: <span className="text-amber-300">{resultValue}</span>
        </p>
      )}
    </div>
  );
}
