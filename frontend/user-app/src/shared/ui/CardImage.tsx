import type { CardRarity } from '../api/types';

const rarityBorder: Record<CardRarity, string> = {
  COMMON: 'border-zinc-600',
  RARE: 'border-blue-500',
  LEGENDARY: 'border-amber-400',
};

interface Props {
  title: string;
  imageUrl?: string | null;
  rarity?: CardRarity;
  grayscale?: boolean;
  className?: string;
}

export function CardImage({ title, imageUrl, rarity = 'COMMON', grayscale, className = '' }: Props) {
  return (
    <div
      className={`overflow-hidden rounded-xl border-2 bg-zinc-900 ${rarityBorder[rarity]} ${grayscale ? 'opacity-50 grayscale' : ''} ${className}`}
    >
      {imageUrl ? (
        <img src={imageUrl} alt={title} className="h-full w-full object-cover" loading="lazy" />
      ) : (
        <div className="flex h-full min-h-[100px] items-center justify-center text-4xl">🃏</div>
      )}
    </div>
  );
}
