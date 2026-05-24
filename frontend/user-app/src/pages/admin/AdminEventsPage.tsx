import { useState } from 'react';
import { adminApi } from '../../shared/api/endpoints';
import { Button } from '../../shared/ui/Button';

export function AdminEventsPage() {
  const [title, setTitle] = useState('');
  const [options, setOptions] = useState('A,B');
  const [eventId, setEventId] = useState('');
  const [winningOptionId, setWinningOptionId] = useState('');
  const [msg, setMsg] = useState('');

  return (
    <div className="space-y-4">
      <section className="space-y-2 rounded-xl bg-zinc-800 p-4">
        <h2 className="font-semibold">Создать событие</h2>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Название"
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <input
          value={options}
          onChange={(e) => setOptions(e.target.value)}
          placeholder="Варианты через запятую"
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <Button
          onClick={async () => {
            try {
              const labels = options.split(',').map((s) => s.trim()).filter(Boolean);
              await adminApi.createEvent(title, labels);
              setMsg('Событие создано');
            } catch (e) {
              setMsg(e instanceof Error ? e.message : 'Ошибка');
            }
          }}
        >
          Создать
        </Button>
      </section>

      <section className="space-y-2 rounded-xl bg-zinc-800 p-4">
        <h2 className="font-semibold">Закрыть / рассчитать</h2>
        <input
          value={eventId}
          onChange={(e) => setEventId(e.target.value)}
          placeholder="ID события"
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <Button variant="secondary" onClick={() => adminApi.closeEvent(Number(eventId)).then(() => setMsg('Закрыто'))}>
          Закрыть приём ставок
        </Button>
        <input
          value={winningOptionId}
          onChange={(e) => setWinningOptionId(e.target.value)}
          placeholder="ID выигравшего варианта"
          className="w-full rounded-lg border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <Button
          onClick={() =>
            adminApi.settleEvent(Number(eventId), Number(winningOptionId)).then(() => setMsg('Рассчитано'))
          }
        >
          Рассчитать выплаты
        </Button>
      </section>
      {msg && <p className="text-sm text-zinc-400">{msg}</p>}
    </div>
  );
}
