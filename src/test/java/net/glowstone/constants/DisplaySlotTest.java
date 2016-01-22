package net.glowstone.constants;

import org.bukkit.scoreboard.DisplaySlot;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the {@link GlowDisplaySlot} class.
 */
public class DisplaySlotTest {

    @Test
    public void testAllValues() {
        for (DisplaySlot slot : DisplaySlot.values()) {
            assertTrue("ID missing for display slot " + slot, GlowDisplaySlot.getId(slot) >= 0);
        }
    }

}
