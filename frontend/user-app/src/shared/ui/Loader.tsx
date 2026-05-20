export function Loader({ text = 'Загрузка...' }: { text?: string }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-12">
      <div className="h-8 w-8 animate-spin rounded-full border-2 border-violet-500 border-t-transparent" />
      <p className="text-sm text-zinc-400">{text}</p>
    </div>
  );
}

export function PageError({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-center">
      <p className="text-sm text-red-300">{message}</p>
      {onRetry && (
        <button type="button" onClick={onRetry} className="mt-2 text-sm text-violet-400 underline">
          Повторить
        </button>
      )}
    </div>
  );
}
