package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Arrays;

/**
 * ID number mappings for {@link DisplaySlot}s.
 */
public final class GlowDisplaySlot {

    private GlowDisplaySlot() {}

    private static final int[] ids = new int[DisplaySlot.values().length];

    /**
     * Get the id for a specified DisplaySlot.
     * @param slot the DisplaySlot.
     * @return the id number.
     */
    public static int getId(DisplaySlot slot) {
        Validate.notNull(slot, "Slot cannot be null");
        return ids[slot.ordinal()];
    }

    private static void set(DisplaySlot slot, int id) {
        ids[slot.ordinal()] = id;
    }

    static {
        Arrays.fill(ids, -1);
        set(DisplaySlot.PLAYER_LIST, 0);
        set(DisplaySlot.SIDEBAR, 1);
        set(DisplaySlot.BELOW_NAME, 2);
    }

}
