package net.glowstone.constants;

import static org.hamcrest.MatcherAssert.assertThat;

import org.bukkit.scoreboard.DisplaySlot;
import org.hamcrest.number.OrderingComparison;
import org.testng.annotations.Test;

/**
 * Tests for the {@link GlowDisplaySlot} class.
 */
public class DisplaySlotTest {

    @Test
    public void testAllValues() {
        for (DisplaySlot slot : DisplaySlot.values()) {
            assertThat("ID missing for display slot " + slot, GlowDisplaySlot.getId(slot),
                OrderingComparison.greaterThanOrEqualTo(0));
        }
    }

}
