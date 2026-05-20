import type { CardRarity } from '../api/types';

const rarityColors: Record<CardRarity, string> = {
  COMMON: 'bg-zinc-600 text-zinc-200',
  RARE: 'bg-blue-600/80 text-blue-100',
  LEGENDARY: 'bg-amber-500/90 text-amber-950',
};

export function RarityBadge({ rarity }: { rarity: CardRarity }) {
  return (
    <span className={`rounded-md px-2 py-0.5 text-xs font-bold uppercase ${rarityColors[rarity]}`}>
      {rarity}
    </span>
  );
}

export function StatusBadge({ label, color }: { label: string; color: string }) {
  return <span className={`rounded-md px-2 py-0.5 text-xs font-medium ${color}`}>{label}</span>;
}
