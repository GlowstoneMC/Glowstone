package net.glowstone.entity.monster;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class GlowSnowmanTest extends GlowMonsterTest<GlowSnowman> {
    public GlowSnowmanTest() {
        super(GlowSnowman::new);
    }

    @Test
    public void testSetDerpTrue() {
        entity.setDerp(true);
        assertTrue(entity.isDerp());
    }

    @Test
    public void testSetDerpFalse() {
        entity.setDerp(false);
        assertFalse(entity.isDerp());
    }
}
