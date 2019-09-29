package net.glowstone.util;

import java.util.function.IntConsumer;

public class ExperienceSplitter {

    private static final int[] CUTS = {2477, 1237, 617, 307, 149, 73, 37, 17, 7, 3, 1};

    /**
     * Cuts experience points into smaller 'cuts', and supplies them to {@code consumer}.
     *
     * @param experience the experience points to cut
     * @param consumer the action to do for each cut
     */
    public static void forEachCut(int experience, final IntConsumer consumer) {
        int remaining = experience;
        while (remaining > 0) {
            for (int cut : CUTS) {
                if (remaining >= cut) {
                    remaining -= cut;
                    consumer.accept(cut);
                    break;
                }
            }
        }
    }
}
