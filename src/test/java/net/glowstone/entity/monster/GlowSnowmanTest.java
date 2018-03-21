package net.glowstone.entity.monster;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

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
