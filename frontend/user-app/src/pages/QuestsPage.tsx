import { useEffect, useState } from 'react';
import { questsApi } from '../shared/api/endpoints';
import type { Quest, QuestSubmission } from '../shared/api/types';
import { Button } from '../shared/ui/Button';
import { Loader, PageError } from '../shared/ui/Loader';

export function QuestsPage() {
  const [quests, setQuests] = useState<Quest[]>([]);
  const [submissions, setSubmissions] = useState<QuestSubmission[]>([]);
  const [proof, setProof] = useState<Record<number, string>>({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    Promise.all([questsApi.list(), questsApi.mySubmissions()])
      .then(([q, s]) => {
        setQuests(q);
        setSubmissions(s);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const submit = async (questId: number) => {
    const text = proof[questId]?.trim();
    if (!text) return;
    setSubmitting(questId);
    try {
      await questsApi.submit(questId, text);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Ошибка');
    } finally {
      setSubmitting(null);
    }
  };

  const statusFor = (questId: number) =>
    submissions.find((s) => s.questId === questId)?.status;

  if (loading) return <Loader />;

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Квесты</h1>
      {error && <PageError message={error} />}
      {quests.map((q) => (
        <article key={q.id} className="rounded-xl bg-zinc-800/80 p-4">
          <h2 className="font-semibold">{q.title}</h2>
          <p className="text-sm text-amber-300">Награда: {q.rewardCoins} 🪙</p>
          {statusFor(q.id) ? (
            <p className="mt-2 text-sm text-zinc-400">Статус: {statusFor(q.id)}</p>
          ) : (
            <>
              <textarea
                placeholder="Доказательство выполнения"
                value={proof[q.id] ?? ''}
                onChange={(e) => setProof({ ...proof, [q.id]: e.target.value })}
                className="mt-2 w-full rounded-lg border border-zinc-700 bg-zinc-900 p-2 text-sm"
                rows={2}
              />
              <Button
                className="mt-2 w-full"
                loading={submitting === q.id}
                onClick={() => submit(q.id)}
              >
                Отправить
              </Button>
            </>
          )}
        </article>
      ))}
    </div>
  );
}
