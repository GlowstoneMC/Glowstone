package net.glowstone.constants;

import org.bukkit.scoreboard.DisplaySlot;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ID number mappings for {@link DisplaySlot}s.
 */
public final class GlowDisplaySlot {

    private static final int[] ids = new int[DisplaySlot.values().length];

    static {
        Arrays.fill(ids, -1);
        set(DisplaySlot.PLAYER_LIST, 0);
        set(DisplaySlot.SIDEBAR, 1);
        set(DisplaySlot.BELOW_NAME, 2);
    }

    private GlowDisplaySlot() {
    }

    /**
     * Get the id for a specified DisplaySlot.
     *
     * @param slot the DisplaySlot.
     * @return the id number.
     */
    public static int getId(DisplaySlot slot) {
        checkNotNull(slot, "Slot cannot be null");
        return ids[slot.ordinal()];
    }

    private static void set(DisplaySlot slot, int id) {
        ids[slot.ordinal()] = id;
    }

}
