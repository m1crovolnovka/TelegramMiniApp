package com.casino.packs.service;

import com.casino.common.rng.WeightedRandomSelector;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RandomService {

    public <T> T weightedPick(List<T> items, List<Integer> weights) {
        return WeightedRandomSelector.pick(items, weights);
    }
}
