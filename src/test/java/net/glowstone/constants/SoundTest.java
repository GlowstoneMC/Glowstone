package net.glowstone.constants;

import org.bukkit.Sound;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests for the GlowSound class.
 */
public class SoundTest {

    @Test
    public void testGetName() {
        for (Sound sound : Sound.values()) {
            assertTrue("Name missing for sound " + sound, GlowSound.getName(sound) != null);
        }
    }

}
