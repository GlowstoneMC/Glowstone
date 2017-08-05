package net.glowstone.util;

import java.util.HashSet;
import java.util.Set;

public class ExperienceSplitter {

    private static final int[] CUTS = {2477, 1237, 617, 307, 149, 73, 37, 17, 7, 3, 1};

    /**
     * Cuts experience points into smaller 'cuts'.
     *
     * @param experience the experience points to cut
     * @return an array of 'cuts' of the given experience
     */
    public static Integer[] cut(int experience) {
        Set<Integer> cuts = new HashSet<>();
        int remaining = experience;
        while (remaining > 0) {
            for (int cut : CUTS) {
                if (remaining >= cut) {
                    remaining -= cut;
                    cuts.add(cut);
                    break;
                }
            }
        }
        return cuts.toArray(new Integer[0]);
    }
}
