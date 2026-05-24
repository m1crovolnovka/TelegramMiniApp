import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuthBootstrap } from '../shared/hooks/useAuth';
import { AppLayout } from '../shared/layouts/AppLayout';
import { Loader, PageError } from '../shared/ui/Loader';
import { HomePage } from '../pages/HomePage';
import { ProfilePage } from '../pages/ProfilePage';
import { ShopPage } from '../pages/ShopPage';
import { CasinoPage } from '../pages/CasinoPage';
import { TradesPage } from '../pages/TradesPage';
import { BotQuestsPage } from '../pages/BotQuestsPage';
import { CollectionPage } from '../pages/CollectionPage';
import { BetsPage } from '../pages/BetsPage';
import { LeaderboardPage } from '../pages/LeaderboardPage';
import { AdminPage } from '../pages/AdminPage';

export default function App() {
  const { user, error, booting, login } = useAuthBootstrap();

  if (booting) return <Loader text="¬ход..." />;
  if (error && !user) return <PageError message={error} onRetry={login} />;
  if (!user) return <Loader />;

  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<HomePage />} />
        <Route path="profile" element={<ProfilePage />} />
        <Route path="inventory" element={<CollectionPage />} />
        <Route path="collection" element={<CollectionPage />} />
        <Route path="shop" element={<ShopPage />} />
        <Route path="casino" element={<CasinoPage />} />
        <Route path="trades" element={<TradesPage />} />
        <Route path="quests" element={<BotQuestsPage />} />
        <Route path="bets" element={<BetsPage />} />
        <Route path="leaderboard" element={<LeaderboardPage />} />
        <Route path="admin" element={<AdminPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
