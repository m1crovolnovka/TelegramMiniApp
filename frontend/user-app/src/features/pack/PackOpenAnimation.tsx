import { useEffect, useState } from 'react';
import type { CardDefinition, DroppedCard } from '../../shared/api/types';
import { CardImage } from '../../shared/ui/CardImage';
import { RarityBadge } from '../../shared/ui/Badge';
import { Button } from '../../shared/ui/Button';

interface Props {
  catalog: CardDefinition[];
  result: DroppedCard;
  onClose: () => void;
  spinningOnly?: boolean;
}

export function PackOpenAnimation({ catalog, result, onClose, spinningOnly }: Props) {
  const [spinning, setSpinning] = useState(!result.cardDefinitionId || spinningOnly);
  const [index, setIndex] = useState(0);

  useEffect(() => {
    const tick = setInterval(() => {
      setIndex((i) => (i + 1) % Math.max(catalog.length, 1));
    }, 120);
    if (spinningOnly) return;
    const stop = setTimeout(() => setSpinning(false), 800);
    return () => {
      clearInterval(tick);
      clearTimeout(stop);
    };
  }, [catalog.length, spinningOnly]);

  useEffect(() => {
    if (!spinningOnly && result.cardDefinitionId) {
      setSpinning(false);
    }
  }, [result.cardDefinitionId, spinningOnly]);

  const preview = spinning
    ? catalog[index] ?? { id: 0, title: '...', rarity: result.rarity, imageStorageKey: '', imageUrl: null }
    : null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/85 p-4">
      <div className="w-full max-w-sm rounded-2xl bg-zinc-900 p-6 text-center">
        <p className="mb-4 text-lg font-bold">{spinning ? 'Открываем пак...' : 'Выпала карта!'}</p>
        <div className={`mx-auto h-48 w-36 ${spinning ? 'animate-pulse' : ''}`}>
          {spinning && preview ? (
            <CardImage title={preview.title} imageUrl={preview.imageUrl} rarity={preview.rarity} />
          ) : (
            <CardImage title={result.title} imageUrl={result.imageUrl} rarity={result.rarity} />
          )}
        </div>
        {!spinning && (
          <>
            <p className="mt-3 text-xl font-semibold">{result.title}</p>
            <div className="mt-2 flex justify-center">
              <RarityBadge rarity={result.rarity} />
            </div>
          </>
        )}
        {!spinning && !spinningOnly && (
          <Button className="mt-6 w-full" onClick={onClose}>
            Забрать
          </Button>
        )}
      </div>
    </div>
  );
}
