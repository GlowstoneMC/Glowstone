package net.glowstone.entity.passive;

import net.glowstone.entity.GlowEntityTest;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GlowBatTest extends GlowEntityTest<GlowBat> {
    public GlowBatTest() {
        super(GlowBat::new);
    }

    @Test
    public void testSleep() {
        entity.setAwake(false);
        assertFalse(entity.isAwake());
    }

    @Test
    public void testWakeUp() {
        entity.setAwake(true);
        assertTrue(entity.isAwake());
    }
}
