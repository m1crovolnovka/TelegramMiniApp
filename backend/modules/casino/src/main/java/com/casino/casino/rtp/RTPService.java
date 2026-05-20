package com.casino.casino.rtp;

import com.casino.common.rng.WeightedRandomSelector;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class RTPService {

    /** Payout multipliers: 0 = loss, otherwise bet * multiplier. Weights sum to 100. */
    public double rollSlotMultiplier() {
        List<Double> mults = List.of(0.0, 1.5, 3.0, 10.0);
        List<Integer> weights = List.of(70, 20, 8, 2);
        return WeightedRandomSelector.pick(mults, weights);
    }

    public int rollRouletteValue() {
        return ThreadLocalRandom.current().nextInt(0, 37);
    }
}
