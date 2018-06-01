package net.glowstone.entity.monster;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class GlowBlazeTest extends GlowMonsterTest<GlowBlaze> {

    public GlowBlazeTest() {
        super(GlowBlaze::new);
    }

    @Test
    public void setOnFire() {
        entity.setOnFire(true);
        assertTrue(entity.isOnFire());
    }

    @Test
    public void setNotOnFire() {
        entity.setOnFire(false);
        assertFalse(entity.isOnFire());
    }
}
