export type UserRole = 'USER' | 'ADMIN';
export type CardRarity = 'COMMON' | 'RARE' | 'LEGENDARY';
export type QuestStatus = 'ACTIVE' | 'DISABLED' | 'ARCHIVED';
export type SubmissionStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export type EventStatus = 'DRAFT' | 'ACTIVE' | 'CLOSED' | 'SETTLED' | 'CANCELLED';
export type TradeStatus = 'DRAFT' | 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED' | 'EXPIRED';
export type NotificationType = 'SYSTEM' | 'TRADE' | 'QUEST' | 'BETTING' | 'CASINO' | 'REWARD' | 'ADMIN';
export type RouletteBetType = 'RED' | 'BLACK' | 'ODD' | 'EVEN' | 'NUMBER';

export interface User {
  id: number;
  telegramId: number;
  username: string | null;
  role: UserRole;
  balanceCoins: number;
}

export interface TokenResponse { accessToken: string }

export interface CardDefinition {
  id: number;
  title: string;
  rarity: CardRarity;
  telegramUsername: string | null;
  imageStorageKey: string;
  imageUrl: string | null;
}

export interface InventoryItem {
  cardDefinitionId: number;
  title: string;
  rarity: CardRarity;
  imageUrl: string | null;
  quantity: number;
  locked: boolean;
  lockedTradeId: number | null;
}

export interface Inventory { items: InventoryItem[]; totalQuantity: number }

export interface CollectionProgress {
  ownedDefinitions: number;
  totalDefinitions: number;
  percentByDefinitions: number;
  ownedUniqueStudents: number;
  totalUniqueStudents: number;
  percentUniqueStudents: number;
  byRarity: { commonOwned: number; rareOwned: number; legendaryOwned: number };
}

export interface Pack {
  id: number;
  name: string;
  priceCoins: number;
  packKind: 'SINGLE' | 'BUNDLE';
  bundleSummary: string | null;
}
export interface DroppedCard { cardDefinitionId: number; title: string; rarity: CardRarity; imageUrl: string | null }
export interface OpenPackResponse {
  packKind: 'SINGLE' | 'BUNDLE';
  droppedCard: DroppedCard;
  droppedCards: DroppedCard[];
}
export interface PackHistoryItem { id: number; packId: number; droppedCard: DroppedCard; openedAt: string }
export interface Quest { id: number; title: string; rewardCoins: number; status: QuestStatus }
export interface QuestSubmission { id: number; questId: number; status: SubmissionStatus; proofText: string }
export interface BettingOption { id: number; label: string; totalStakeCoins: number; winning: boolean }
export interface BettingEvent { id: number; title: string; status: EventStatus; options: BettingOption[] }
export interface TradeItem {
  id: number;
  fromUserId: number;
  fromUsername: string | null;
  cardDefinitionId: number | null;
  cardTitle: string | null;
  quantity: number;
  coinsAmount: number | null;
}
export interface Trade {
  id: number;
  initiatorUserId: number;
  initiatorUsername: string | null;
  partnerUserId: number;
  partnerUsername: string | null;
  status: TradeStatus;
  items: TradeItem[];
}
export interface QuestTask { id: number; description: string; rewardCoins: number }
export interface PublicUser { id: number; username: string | null }
export interface LeaderboardEntry { rank: number; userId: number; username: string; balanceCoins: number; uniqueStudentsOwned: number }
export interface Notification { id: number; message: string; type: NotificationType; read: boolean }
export interface Transaction { id: number; amount: number; operationId: string; transactionType: string; reason: string; description: string; createdAt: string }
export interface RouletteResult { rolledValue: number; payoutCoins: number }
export interface SlotSpin { id: number; userId: number; gameType: string; betAmount: number; payoutAmount: number }
export interface ApiError { code: string; message: string }

export interface AdminUser {
  id: number;
  username: string | null;
  telegramId: number;
  balanceCoins: number;
  uniqueStudentsOwned: number;
}
export interface AdminCard {
  id: number;
  title: string;
  rarity: string;
  telegramUsername: string | null;
  imageUrl: string | null;
}
export interface AdminTransaction {
  id: number;
  amount: number;
  transactionType: string;
  reason: string;
  description: string;
  createdAt: string;
}
