package com.casino.betting.service;

/** Parimutuel-style display odds: more stake on an option → lower coefficient. */
public final class BettingOddsCalculator {

    private static final double BASE_COEFFICIENT = 2.0;
    private static final long VIRTUAL_LIQUIDITY_PER_OPTION = 50;

    private BettingOddsCalculator() {}

    public static double coefficient(long optionStakeCoins, long totalStakeCoins, int optionCount) {
        if (optionCount <= 0) {
            return BASE_COEFFICIENT;
        }
        long virtualTotal = totalStakeCoins + VIRTUAL_LIQUIDITY_PER_OPTION * optionCount;
        long virtualOption = optionStakeCoins + VIRTUAL_LIQUIDITY_PER_OPTION;
        double raw = virtualTotal / (double) virtualOption;
        return Math.round(raw * 100.0) / 100.0;
    }

    /** Payout for a winning stake when the event is settled. */
    public static long payoutForWinningStake(long stakeCoins, long winningOptionStake, long totalPool) {
        if (stakeCoins <= 0 || winningOptionStake <= 0 || totalPool <= 0) {
            return 0;
        }
        return (long) Math.floor((double) stakeCoins * totalPool / winningOptionStake);
    }
}
