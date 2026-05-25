import { useEffect, useMemo, useRef, useState } from 'react';

export const ROULETTE_WHEEL_ORDER = [
  0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14,
  31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26,
] as const;

const RED_NUMBERS = new Set([1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36]);
const SEGMENTS = ROULETTE_WHEEL_ORDER.length;
const SEGMENT_ANGLE = 360 / SEGMENTS;
const MIN_FULL_SPINS = 6;
const SPIN_MS = 5200;

function pocketColor(n: number): string {
  if (n === 0) return '#15803d';
  return RED_NUMBERS.has(n) ? '#b91c1c' : '#0f0f14';
}

/** Always rotate forward at least MIN_FULL_SPINS full turns, then land on server value. */
function nextRotation(currentDeg: number, value: number): number {
  const index = ROULETTE_WHEEL_ORDER.indexOf(value as (typeof ROULETTE_WHEEL_ORDER)[number]);
  const safeIndex = index >= 0 ? index : 0;
  const pocketCenter = safeIndex * SEGMENT_ANGLE + SEGMENT_ANGLE / 2;
  const targetMod = (360 - pocketCenter + 360) % 360;
  const currentMod = ((currentDeg % 360) + 360) % 360;
  let delta = targetMod - currentMod;
  if (delta <= 0) delta += 360;
  return currentDeg + MIN_FULL_SPINS * 360 + delta;
}

interface Props {
  spinning: boolean;
  resultValue?: number;
  spinToken: number;
  onSpinEnd?: () => void;
}

export function RouletteWheel({ spinning, resultValue, spinToken, onSpinEnd }: Props) {
  const [rotation, setRotation] = useState(0);
  const rotationRef = useRef(0);
  const lastToken = useRef(0);

  const segments = useMemo(
    () =>
      ROULETTE_WHEEL_ORDER.map((num, i) => {
        const start = i * SEGMENT_ANGLE - 90;
        const end = (i + 1) * SEGMENT_ANGLE - 90;
        const mid = (start + end) / 2;
        const rad = (mid * Math.PI) / 180;
        const labelR = 118;
        return {
          num,
          start,
          end,
          x: 150 + labelR * Math.cos(rad),
          y: 150 + labelR * Math.sin(rad),
          mid,
        };
      }),
    [],
  );

  useEffect(() => {
    if (!spinning || resultValue === undefined || spinToken === lastToken.current) return;
    lastToken.current = spinToken;
    const next = nextRotation(rotationRef.current, resultValue);
    rotationRef.current = next;
    setRotation(next);
  }, [spinning, resultValue, spinToken]);

  useEffect(() => {
    if (!spinning) return;
    const t = setTimeout(() => onSpinEnd?.(), SPIN_MS);
    return () => clearTimeout(t);
  }, [spinning, spinToken, onSpinEnd]);

  return (
    <div className="relative mx-auto h-[320px] w-[320px]">
      <div className="absolute left-1/2 top-0 z-20 -translate-x-1/2">
        <div className="h-0 w-0 border-x-[14px] border-b-[26px] border-x-transparent border-b-amber-400 drop-shadow-[0_2px_8px_rgba(0,0,0,0.5)]" />
      </div>

      <div
        className="absolute inset-4 rounded-full shadow-[0_0_48px_rgba(251,191,36,0.3)] will-change-transform"
        style={{
          transform: `rotate(${rotation}deg)`,
          transition: spinning
            ? `transform ${SPIN_MS}ms cubic-bezier(0.17, 0.67, 0.12, 0.99)`
            : 'none',
        }}
      >
        <svg viewBox="0 0 300 300" className="h-full w-full">
          <defs>
            <radialGradient id="hubGlow" cx="50%" cy="50%" r="50%">
              <stop offset="0%" stopColor="#fcd34d" />
              <stop offset="100%" stopColor="#92400e" />
            </radialGradient>
          </defs>
          <circle cx="150" cy="150" r="148" fill="#1c1917" stroke="#fbbf24" strokeWidth="7" />
          {segments.map(({ num, start, end }) => (
            <path
              key={`${num}-${start}`}
              d={describeArc(150, 150, 140, start, end)}
              fill={pocketColor(num)}
              stroke="#292524"
              strokeWidth="0.6"
            />
          ))}
          {segments.map(({ num, x, y, mid }) => (
            <text
              key={`lbl-${num}`}
              x={x}
              y={y}
              fill="white"
              fontSize="9"
              fontWeight="700"
              textAnchor="middle"
              dominantBaseline="middle"
              transform={`rotate(${mid + 90}, ${x}, ${y})`}
            >
              {num}
            </text>
          ))}
          <circle cx="150" cy="150" r="38" fill="url(#hubGlow)" stroke="#fde68a" strokeWidth="3" />
        </svg>
      </div>

      <div className="pointer-events-none absolute inset-0 rounded-full ring-4 ring-amber-500/35" />
    </div>
  );
}

function describeArc(cx: number, cy: number, r: number, startAngle: number, endAngle: number): string {
  const start = polar(cx, cy, r, endAngle);
  const end = polar(cx, cy, r, startAngle);
  const large = endAngle - startAngle <= 180 ? 0 : 1;
  return `M ${cx} ${cy} L ${start.x} ${start.y} A ${r} ${r} 0 ${large} 0 ${end.x} ${end.y} Z`;
}

function polar(cx: number, cy: number, r: number, angleDeg: number) {
  const rad = (angleDeg * Math.PI) / 180;
  return { x: cx + r * Math.cos(rad), y: cy + r * Math.sin(rad) };
}
