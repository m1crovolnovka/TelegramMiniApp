import { Link } from 'react-router-dom';

export function LeaderboardPage() {
  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Рейтинг</h1>
      <p className="rounded-xl bg-zinc-800/80 p-4 text-sm text-zinc-400">
        Таблица лидеров будет доступна после подключения API{' '}
        <code className="text-violet-400">GET /users/leaderboard</code> на бэкенде.
      </p>
      <p className="text-sm">
        Пока смотрите баланс в{' '}
        <Link to="/profile" className="text-violet-400 underline">
          профиле
        </Link>
        .
      </p>
    </div>
  );
}
