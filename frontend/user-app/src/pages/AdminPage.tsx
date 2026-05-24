import { useEffect, useState } from 'react';
import { adminApi } from '../shared/api/endpoints';
import type { AdminCard, AdminUser } from '../shared/api/types';

export function AdminPage() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [cards, setCards] = useState<AdminCard[]>([]);
  const [title, setTitle] = useState('');
  const [rarity, setRarity] = useState('COMMON');
  const [telegramUsername, setTelegramUsername] = useState('');
  const [imageUrl, setImageUrl] = useState('');

  const reload = () => {
    adminApi.users().then((r) => setUsers(r.data));
    adminApi.cards().then((r) => setCards(r.data));
  };

  useEffect(() => {
    reload();
  }, []);

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Admin panel</h1>

      <section className="rounded-xl bg-zinc-800/70 p-3 space-y-2">
        <h2 className="font-semibold">Create card</h2>
        <input className="w-full rounded bg-zinc-900 p-2" placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} />
        <input className="w-full rounded bg-zinc-900 p-2" placeholder="Student username" value={telegramUsername} onChange={(e) => setTelegramUsername(e.target.value)} />
        <select className="w-full rounded bg-zinc-900 p-2" value={rarity} onChange={(e) => setRarity(e.target.value)}>
          <option value="COMMON">COMMON</option>
          <option value="RARE">RARE</option>
          <option value="LEGENDARY">LEGENDARY</option>
        </select>
        <input className="w-full rounded bg-zinc-900 p-2" placeholder="Image URL" value={imageUrl} onChange={(e) => setImageUrl(e.target.value)} />
        <button className="rounded bg-violet-600 px-4 py-2" onClick={async () => { await adminApi.createCard({ title, rarity, telegramUsername, imageUrl }); setTitle(''); setTelegramUsername(''); setImageUrl(''); reload(); }}>Create</button>
      </section>

      <section className="rounded-xl bg-zinc-800/70 p-3">
        <h2 className="mb-2 font-semibold">Users</h2>
        <ul className="space-y-1 text-sm">
          {users.map((u) => (
            <li key={u.id} className="rounded bg-zinc-900 px-3 py-2">@{u.username ?? u.id} | coins {u.balanceCoins} | students {u.uniqueStudentsOwned}</li>
          ))}
        </ul>
      </section>

      <section className="rounded-xl bg-zinc-800/70 p-3">
        <h2 className="mb-2 font-semibold">Cards ({cards.length})</h2>
        <ul className="space-y-1 text-sm">
          {cards.map((c) => (
            <li key={c.id} className="flex items-center justify-between rounded bg-zinc-900 px-3 py-2">
              <span>#{c.id} {c.title} ({c.rarity}) {c.telegramUsername ? `@${c.telegramUsername}` : ''}</span>
              <button className="rounded bg-red-800 px-2 py-1 text-xs" onClick={async () => { await adminApi.deleteCard(c.id); reload(); }}>Delete</button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
