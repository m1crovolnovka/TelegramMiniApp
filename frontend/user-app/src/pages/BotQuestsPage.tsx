export function BotQuestsPage() {
  return (
    <div className="space-y-4 rounded-2xl bg-zinc-800/80 p-6">
      <p className="text-center text-4xl">🎯</p>
      <h1 className="text-center text-xl font-bold">Парные квесты</h1>
      <p className="text-center text-sm text-zinc-400">
        Квесты проходят в Telegram-боте этого Mini App (тот же бот, что открывает приложение).
        После одобрения админом монеты и карточки начисляются автоматически.
      </p>
      <ol className="mx-auto max-w-sm list-decimal space-y-2 text-sm text-zinc-300">
        <li>Откройте бота Casino и нажмите /start</li>
        <li>Выберите QR или случайного партнёра</li>
        <li>Выполните задание и отправьте фото/видео</li>
        <li>Админ подтверждает в боте — награда в Mini App</li>
      </ol>
      <p className="text-center text-xs text-zinc-500">
        Админ бота: переменная <code className="text-zinc-400">QUEST_BOT_ADMIN_USERNAMES</code> (username без @).
        Админ должен хотя бы раз написать /start боту, чтобы получать уведомления.
      </p>
    </div>
  );
}
