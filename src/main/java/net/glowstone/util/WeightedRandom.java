package net.glowstone.util;

import java.util.Random;

public final class WeightedRandom {

    /**
     * Selects a random item from a weighted distribution.
     *
     * @param random the PRNG to use
     * @param possibilities the distribution to sample from
     * @param <T> the type of each of the {@code possibilities}
     * @return a random item
     */
    public static <T extends Choice> T getRandom(Random random, Iterable<T> possibilities) {
        int weights = 0;
        for (T possibility : possibilities) {
            weights += possibility.getWeight();
        }

        int restWeight = random.nextInt(weights);

        for (T t : possibilities) {
            restWeight -= t.getWeight();
            if (restWeight < 0) {
                return t;
            }
        }
        return null;
    }

    @FunctionalInterface
    public interface Choice {

        int getWeight();
    }
}
