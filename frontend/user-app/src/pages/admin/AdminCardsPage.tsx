import { useEffect, useState } from 'react';
import { adminApi } from '../../shared/api/endpoints';
import type { AdminCard } from '../../shared/api/types';
import { Button } from '../../shared/ui/Button';

export function AdminCardsPage() {
  const [cards, setCards] = useState<AdminCard[]>([]);
  const [title, setTitle] = useState('');
  const [rarity, setRarity] = useState('COMMON');
  const [telegramUsername, setTelegramUsername] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [msg, setMsg] = useState('');

  const reload = () => adminApi.cards().then((r) => setCards(r.data));

  useEffect(() => {
    reload();
  }, []);

  const resetForm = () => {
    setTitle('');
    setRarity('COMMON');
    setTelegramUsername('');
    setImageUrl('');
    setEditingId(null);
  };

  const startEdit = (c: AdminCard) => {
    setEditingId(c.id);
    setTitle(c.title);
    setRarity(c.rarity);
    setTelegramUsername(c.telegramUsername ?? '');
    setImageUrl(c.imageUrl ?? '');
  };

  const save = async () => {
    try {
      const body = {
        title,
        rarity,
        telegramUsername: telegramUsername || undefined,
        imageUrl: imageUrl || undefined,
      };
      if (editingId != null) {
        await adminApi.updateCard(editingId, body);
        setMsg('Карточка обновлена');
      } else {
        await adminApi.createCard(body);
        setMsg('Карточка создана');
      }
      resetForm();
      reload();
    } catch (e) {
      setMsg(e instanceof Error ? e.message : 'Ошибка');
    }
  };

  return (
    <div className="space-y-4">
      <section className="space-y-2 rounded-xl bg-zinc-800 p-4">
        <h2 className="font-semibold">{editingId != null ? 'Редактировать карточку' : 'Добавить карточку'}</h2>
        <input
          placeholder="Название"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <input
          placeholder="Telegram username студента"
          value={telegramUsername}
          onChange={(e) => setTelegramUsername(e.target.value)}
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <select
          value={rarity}
          onChange={(e) => setRarity(e.target.value)}
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        >
          <option value="COMMON">Обычная</option>
          <option value="RARE">Редкая</option>
          <option value="LEGENDARY">Легендарная</option>
        </select>
        <input
          placeholder="URL изображения"
          value={imageUrl}
          onChange={(e) => setImageUrl(e.target.value)}
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <div className="flex gap-2">
          <Button onClick={save}>{editingId != null ? 'Сохранить' : 'Создать'}</Button>
          {editingId != null && (
            <Button variant="secondary" onClick={resetForm}>
              Отмена
            </Button>
          )}
        </div>
        {msg && <p className="text-sm text-zinc-400">{msg}</p>}
      </section>

      <section>
        <h2 className="mb-2 font-semibold">Каталог ({cards.length})</h2>
        <ul className="grid grid-cols-2 gap-2 text-sm">
          {cards.map((c) => (
            <li key={c.id} className="rounded-lg bg-zinc-800 p-2">
              {c.imageUrl && (
                <img src={c.imageUrl} alt="" className="mb-1 h-20 w-full rounded object-cover" />
              )}
              <p className="font-medium">{c.title}</p>
              <p className="text-xs text-zinc-500">
                #{c.id} · {c.rarity}
                {c.telegramUsername ? ` · @${c.telegramUsername}` : ''}
              </p>
              <div className="mt-2 flex gap-2">
                <button type="button" className="rounded bg-zinc-700 px-2 py-1 text-xs" onClick={() => startEdit(c)}>
                  Изменить
                </button>
                <button
                  type="button"
                  className="rounded bg-red-800 px-2 py-1 text-xs"
                  onClick={async () => {
                    if (!confirm(`Удалить «${c.title}»?`)) return;
                    await adminApi.deleteCard(c.id);
                    setMsg('Удалено');
                    reload();
                  }}
                >
                  Удалить
                </button>
              </div>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
