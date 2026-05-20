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

export interface TokenResponse {
  accessToken: string;
}

export interface CardDefinition {
  id: number;
  title: string;
  rarity: CardRarity;
  imageStorageKey: string;
}

export interface InventoryItem {
  cardDefinitionId: number;
  title: string;
  rarity: CardRarity;
  quantity: number;
  locked: boolean;
  lockedTradeId: number | null;
}

export interface Inventory {
  items: InventoryItem[];
  totalQuantity: number;
}

export interface CollectionProgress {
  ownedDefinitions: number;
  totalDefinitions: number;
  percentByDefinitions: number;
  byRarity: { commonOwned: number; rareOwned: number; legendaryOwned: number };
}

export interface Pack {
  id: number;
  name: string;
  priceCoins: number;
}

export interface DroppedCard {
  cardDefinitionId: number;
  title: string;
  rarity: CardRarity;
}

export interface OpenPackResponse {
  droppedCard: DroppedCard;
}

export interface PackHistoryItem {
  id: number;
  packId: number;
  droppedCard: DroppedCard;
  openedAt: string;
}

export interface Quest {
  id: number;
  title: string;
  rewardCoins: number;
  status: QuestStatus;
}

export interface QuestSubmission {
  id: number;
  questId: number;
  status: SubmissionStatus;
  proofText: string;
}

export interface BettingOption {
  id: number;
  label: string;
  totalStakeCoins: number;
  winning: boolean;
}

export interface BettingEvent {
  id: number;
  title: string;
  status: EventStatus;
  options: BettingOption[];
}

export interface Trade {
  id: number;
  initiatorUserId: number;
  partnerUserId: number;
  status: TradeStatus;
}

export interface Notification {
  id: number;
  message: string;
  type: NotificationType;
  read: boolean;
}

export interface Transaction {
  id: number;
  amount: number;
  operationId: string;
  transactionType: string;
  reason: string;
  createdAt: string;
}

export interface RouletteResult {
  rolledValue: number;
  payoutCoins: number;
}

export interface SlotSpin {
  id: number;
  userId: number;
  gameType: string;
  betAmount: number;
  payoutAmount: number;
}

export interface ApiError {
  code: string;
  message: string;
}
