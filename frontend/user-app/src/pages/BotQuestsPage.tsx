const botUrl = import.meta.env.VITE_QUESTS_BOT_URL ?? 'https://t.me/your_quest_bot';

export function BotQuestsPage() {
  return (
    <div className="space-y-4 rounded-2xl bg-zinc-800/80 p-6 text-center">
      <p className="text-4xl">🤖</p>
      <h1 className="text-xl font-bold">Квесты в боте</h1>
      <p className="text-sm text-zinc-400">
        Выполнение и отправка квестов перенесены в Telegram-бот. В Mini App — только казино, карты и
        трейды.
      </p>
      <a
        href={botUrl}
        target="_blank"
        rel="noreferrer"
        className="inline-block rounded-xl bg-violet-600 px-6 py-3 font-semibold"
      >
        Открыть бота
      </a>
    </div>
  );
}
