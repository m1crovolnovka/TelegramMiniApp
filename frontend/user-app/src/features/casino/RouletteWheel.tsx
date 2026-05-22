import { useEffect, useState } from 'react';

const SEGMENTS = [
  '#ef4444',
  '#18181b',
  '#ef4444',
  '#18181b',
  '#ef4444',
  '#18181b',
  '#ef4444',
  '#18181b',
];

interface Props {
  spinning: boolean;
  resultValue?: number;
  onSpinEnd?: () => void;
}

export function RouletteWheel({ spinning, resultValue, onSpinEnd }: Props) {
  const [rotation, setRotation] = useState(0);

  useEffect(() => {
    if (!spinning) return;
    setRotation((r) => r + 1440 + (resultValue ?? 0) * (360 / 37));
    const t = setTimeout(() => onSpinEnd?.(), 4000);
    return () => clearTimeout(t);
  }, [spinning, resultValue, onSpinEnd]);

  return (
    <div className="relative mx-auto flex h-56 w-56 items-center justify-center">
      <div className="absolute -top-2 z-10 text-2xl">▼</div>
      <div
        className="h-full w-full rounded-full border-4 border-amber-500 transition-transform duration-[4000ms] ease-out"
        style={{
          transform: `rotate(${rotation}deg)`,
          background: `conic-gradient(${SEGMENTS.map((c, i) => `${c} ${(i * 100) / SEGMENTS.length}% ${((i + 1) * 100) / SEGMENTS.length}%`).join(', ')})`,
        }}
      />
      <div className="absolute h-12 w-12 rounded-full bg-amber-600" />
      {!spinning && resultValue !== undefined && (
        <p className="absolute -bottom-8 w-full text-center text-sm font-bold">Выпало: {resultValue}</p>
      )}
    </div>
  );
}
