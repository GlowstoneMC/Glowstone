package net.glowstone.entity.passive;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.glowstone.entity.GlowEntityTest;
import org.junit.Test;

public class GlowBatTest extends GlowEntityTest<GlowBat> {
    public GlowBatTest() {
        super(GlowBat::new);
    }

    @Test
    public void testSleep() {
        GlowBat bat = entityCreator.apply(location);
        bat.setAwake(false);
        assertFalse(bat.isAwake());
    }

    @Test
    public void testWakeUp() {
        GlowBat bat = entityCreator.apply(location);
        bat.setAwake(true);
        assertTrue(bat.isAwake());
    }
}