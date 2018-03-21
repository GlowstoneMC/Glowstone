package net.glowstone.entity.monster;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class GlowBlazeTest extends net.glowstone.entity.monster.GlowMonsterTest<GlowBlaze> {

    public GlowBlazeTest() {
        super(GlowBlaze::new);
    }

    @Test
    void setOnFire() {
        entity.setOnFire(true);
        assertTrue(entity.isOnFire());
    }

    @Test
    void setNotOnFire() {
        entity.setOnFire(false);
        assertFalse(entity.isOnFire());
    }
}
