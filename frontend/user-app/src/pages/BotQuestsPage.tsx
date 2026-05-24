const botUsername = import.meta.env.VITE_QUESTS_BOT_USERNAME ?? 'your_quest_bot';
const botUrl = import.meta.env.VITE_QUESTS_BOT_URL ?? `https://t.me/${botUsername}`;

export function BotQuestsPage() {
  return (
    <div className="space-y-4 rounded-2xl bg-zinc-800/80 p-6 text-center">
      <p className="text-4xl">🤖</p>
      <h1 className="text-xl font-bold">Квесты в Telegram</h1>
      <p className="text-sm text-zinc-400">
        Парные квесты с QR, случайным партнёром и модерацией — в боте @{botUsername}. После одобрения
        админом монеты и карточки начисляются в Casino автоматически.
      </p>
      <ol className="mx-auto max-w-sm list-decimal space-y-1 text-left text-sm text-zinc-400">
        <li>Откройте бота и нажмите «Открыть Casino» или /start</li>
        <li>Выберите QR или случайного партнёра</li>
        <li>Выполните задание и отправьте фото/видео</li>
      </ol>
      <a
        href={botUrl}
        target="_blank"
        rel="noreferrer"
        className="inline-block rounded-xl bg-violet-600 px-6 py-3 font-semibold"
      >
        Открыть @{botUsername}
      </a>
      <p className="text-xs text-zinc-500">
        Backend: задайте QUEST_BOT_TOKEN и QUEST_BOT_USERNAME. В dev шаблоны квестов создаются при первом
        запуске.
      </p>
    </div>
  );
}
