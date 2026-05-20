import { useEffect, useState } from 'react';
import { NavLink, Route, Routes } from 'react-router-dom';
import { adminApi, adminLogin, setToken } from './api';

function Shell({ children }: { children: React.ReactNode }) {
  const nav = [
    { to: '/', label: 'Dashboard' },
    { to: '/economy', label: 'Economy' },
    { to: '/quests', label: 'Quests' },
    { to: '/events', label: 'Events' },
  ];
  return (
    <div className="min-h-screen">
      <header className="border-b border-zinc-800 bg-zinc-900 px-6 py-4">
        <h1 className="text-xl font-bold text-red-400">Casino Admin</h1>
        <nav className="mt-3 flex gap-4 text-sm">
          {nav.map((n) => (
            <NavLink
              key={n.to}
              to={n.to}
              end={n.to === '/'}
              className={({ isActive }) => (isActive ? 'text-white' : 'text-zinc-500')}
            >
              {n.label}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="mx-auto max-w-3xl p-6">{children}</main>
    </div>
  );
}

function Dashboard() {
  const [stats, setStats] = useState<{ users: number; cardDefinitions: number; quests: number } | null>(
    null,
  );
  const [error, setError] = useState('');

  useEffect(() => {
    adminApi
      .stats()
      .then((r) => setStats(r.data))
      .catch((e) => setError(e.message));
  }, []);

  if (error) return <p className="text-red-400">{error}</p>;
  if (!stats) return <p>Loading...</p>;
  return (
    <div className="grid grid-cols-3 gap-4">
      {[
        ['Users', stats.users],
        ['Cards', stats.cardDefinitions],
        ['Quests', stats.quests],
      ].map(([label, val]) => (
        <div key={label as string} className="rounded-xl bg-zinc-800 p-4">
          <p className="text-sm text-zinc-400">{label}</p>
          <p className="text-2xl font-bold">{val}</p>
        </div>
      ))}
    </div>
  );
}

function EconomyPage() {
  const [userId, setUserId] = useState('');
  const [amount, setAmount] = useState('1000');
  const [reason, setReason] = useState('admin_adjust');
  const [msg, setMsg] = useState('');

  const run = async (add: boolean) => {
    try {
      const fn = add ? adminApi.addCoins : adminApi.removeCoins;
      await fn(Number(userId), Number(amount), reason);
      setMsg(add ? 'Coins added' : 'Coins removed');
    } catch (e) {
      setMsg(e instanceof Error ? e.message : 'Error');
    }
  };

  return (
    <div className="space-y-3">
      <h2 className="text-lg font-semibold">Balance adjustment</h2>
      <input
        placeholder="User ID"
        value={userId}
        onChange={(e) => setUserId(e.target.value)}
        className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
      />
      <input
        placeholder="Amount"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
      />
      <input
        placeholder="Reason"
        value={reason}
        onChange={(e) => setReason(e.target.value)}
        className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
      />
      <div className="flex gap-2">
        <button type="button" onClick={() => run(true)} className="rounded bg-green-600 px-4 py-2">
          Add
        </button>
        <button type="button" onClick={() => run(false)} className="rounded bg-red-600 px-4 py-2">
          Remove
        </button>
      </div>
      {msg && <p className="text-sm text-zinc-400">{msg}</p>}
    </div>
  );
}

function QuestsPage() {
  const [title, setTitle] = useState('');
  const [reward, setReward] = useState('100');
  const [submissionId, setSubmissionId] = useState('');
  const [msg, setMsg] = useState('');

  return (
    <div className="space-y-6">
      <section className="space-y-2">
        <h2 className="font-semibold">Create quest</h2>
        <input
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <input
          placeholder="Reward coins"
          value={reward}
          onChange={(e) => setReward(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <button
          type="button"
          className="rounded bg-violet-600 px-4 py-2"
          onClick={async () => {
            try {
              await adminApi.createQuest(title, Number(reward));
              setMsg('Quest created');
            } catch (e) {
              setMsg(e instanceof Error ? e.message : 'Error');
            }
          }}
        >
          Create
        </button>
      </section>
      <section className="space-y-2">
        <h2 className="font-semibold">Moderate submission</h2>
        <input
          placeholder="Submission ID"
          value={submissionId}
          onChange={(e) => setSubmissionId(e.target.value)}
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <div className="flex gap-2">
          <button
            type="button"
            className="rounded bg-green-600 px-4 py-2"
            onClick={() => adminApi.approveSubmission(Number(submissionId)).then(() => setMsg('Approved'))}
          >
            Approve
          </button>
          <button
            type="button"
            className="rounded bg-red-600 px-4 py-2"
            onClick={() => adminApi.rejectSubmission(Number(submissionId)).then(() => setMsg('Rejected'))}
          >
            Reject
          </button>
        </div>
      </section>
      {msg && <p className="text-sm text-zinc-400">{msg}</p>}
    </div>
  );
}

function EventsPage() {
  const [title, setTitle] = useState('');
  const [options, setOptions] = useState('A,B');
  const [eventId, setEventId] = useState('');
  const [winningOptionId, setWinningOptionId] = useState('');
  const [msg, setMsg] = useState('');

  return (
    <div className="space-y-6">
      <section className="space-y-2">
        <h2 className="font-semibold">Create event</h2>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Title"
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <input
          value={options}
          onChange={(e) => setOptions(e.target.value)}
          placeholder="Options comma-separated"
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <button
          type="button"
          className="rounded bg-violet-600 px-4 py-2"
          onClick={async () => {
            try {
              const labels = options.split(',').map((s) => s.trim()).filter(Boolean);
              await adminApi.createEvent(title, labels);
              setMsg('Event created');
            } catch (e) {
              setMsg(e instanceof Error ? e.message : 'Error');
            }
          }}
        >
          Create
        </button>
      </section>
      <section className="space-y-2">
        <h2 className="font-semibold">Close / Settle</h2>
        <input
          value={eventId}
          onChange={(e) => setEventId(e.target.value)}
          placeholder="Event ID"
          className="w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <button
          type="button"
          className="mr-2 rounded bg-zinc-600 px-4 py-2"
          onClick={() => adminApi.closeEvent(Number(eventId)).then(() => setMsg('Closed'))}
        >
          Close
        </button>
        <input
          value={winningOptionId}
          onChange={(e) => setWinningOptionId(e.target.value)}
          placeholder="Winning option ID"
          className="mt-2 w-full rounded border border-zinc-700 bg-zinc-900 px-3 py-2"
        />
        <button
          type="button"
          className="mt-2 rounded bg-amber-600 px-4 py-2"
          onClick={() =>
            adminApi.settleEvent(Number(eventId), Number(winningOptionId)).then(() => setMsg('Settled'))
          }
        >
          Settle
        </button>
      </section>
      {msg && <p className="text-sm text-zinc-400">{msg}</p>}
    </div>
  );
}

export default function App() {
  const [ready, setReady] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('admin-token');
    if (token) setToken(token);
    adminLogin()
      .then((t) => {
        localStorage.setItem('admin-token', t);
        setReady(true);
      })
      .catch((e) => setError(e instanceof Error ? e.message : 'Login failed'));
  }, []);

  if (error) return <p className="p-8 text-red-400">Admin login failed: {error}</p>;
  if (!ready) return <p className="p-8">Logging in...</p>;

  return (
    <Shell>
      <Routes>
        <Route index element={<Dashboard />} />
        <Route path="economy" element={<EconomyPage />} />
        <Route path="quests" element={<QuestsPage />} />
        <Route path="events" element={<EventsPage />} />
      </Routes>
    </Shell>
  );
}
