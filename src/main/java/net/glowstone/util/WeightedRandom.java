package net.glowstone.util;

import java.util.Random;

public final class WeightedRandom {
    public static <T extends Choice> T getRandom(Random random, Iterable<T> possibilities) {
        int weights = 0;
        for (T possibility : possibilities)
            weights += possibility.getWeight();

        int restWeight = random.nextInt(weights);

        for (T t : possibilities) {
            restWeight -= t.getWeight();
            if (restWeight < 0) {
                return t;
            }
        }

        return null;
    }

    public interface Choice {
        int getWeight();
    }
}
