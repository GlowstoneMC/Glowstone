package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Sound;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlowSound}.
 */
@RunWith(Parameterized.class)
public class SoundTest {

    private final Sound sound;

    public SoundTest(Sound sound) {
        this.sound = sound;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ParameterUtils.enumCases(Sound.values());
    }

    @Test
    public void testGetName() {
        assertTrue("Name missing for sound " + sound, GlowSound.getName(sound) != null);
    }

}
