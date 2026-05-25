import { useEffect, useState } from 'react';
import { adminApi } from '../../shared/api/endpoints';
import type { QuestTask } from '../../shared/api/types';
import { Button } from '../../shared/ui/Button';

export function AdminQuestsPage() {
  const [tasks, setTasks] = useState<QuestTask[]>([]);
  const [description, setDescription] = useState('');
  const [reward, setReward] = useState('100');
  const [msg, setMsg] = useState('');

  const reload = () => adminApi.questTasks().then((r) => setTasks(r.data));

  useEffect(() => {
    reload();
  }, []);

  return (
    <div className="space-y-4">
      <section className="rounded-xl border border-violet-500/30 bg-violet-900/20 p-4 text-sm text-zinc-300">
        <p>
          Квесты выполняются в Telegram-боте Mini App. Здесь — шаблоны заданий (текст и награда в монетах).
          Модерация — в боте (кнопки подтвердить/отклонить).
        </p>
        <p className="mt-2 text-xs text-zinc-500">
          Админ бота: <code className="text-zinc-400">QUEST_BOT_ADMIN_USERNAMES</code> (username без @).
          Админ должен написать боту /start, чтобы получать уведомления.
        </p>
      </section>

      <section className="space-y-2 rounded-xl bg-zinc-800 p-4">
        <h2 className="font-semibold">Добавить шаблон квеста</h2>
        <textarea
          placeholder="Описание задания"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
          rows={3}
        />
        <input
          placeholder="Награда (монеты)"
          value={reward}
          onChange={(e) => setReward(e.target.value)}
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <Button
          onClick={async () => {
            try {
              await adminApi.createQuestTask(description, Number(reward));
              setDescription('');
              setReward('100');
              setMsg('Шаблон добавлен');
              reload();
            } catch (e) {
              setMsg(e instanceof Error ? e.message : 'Ошибка');
            }
          }}
        >
          Создать
        </Button>
        {msg && <p className="text-sm text-zinc-400">{msg}</p>}
      </section>

      <section>
        <h2 className="mb-2 font-semibold">Шаблоны ({tasks.length})</h2>
        <ul className="space-y-2 text-sm">
          {tasks.map((t) => (
            <li key={t.id} className="flex items-start justify-between gap-2 rounded-xl bg-zinc-800 p-3">
              <div>
                <p className="font-medium">#{t.id}</p>
                <p className="text-zinc-300">{t.description}</p>
                <p className="text-amber-300">🪙 {t.rewardCoins}</p>
              </div>
              <button
                type="button"
                className="shrink-0 rounded bg-red-800 px-2 py-1 text-xs"
                onClick={async () => {
                  if (!confirm('Удалить шаблон?')) return;
                  await adminApi.deleteQuestTask(t.id);
                  reload();
                }}
              >
                Удалить
              </button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
