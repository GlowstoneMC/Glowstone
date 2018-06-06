package net.glowstone.entity.passive;

import net.glowstone.entity.GlowEntityTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GlowBatTest extends GlowEntityTest<GlowBat> {
    public GlowBatTest() {
        super(GlowBat::new);
    }

    @Test
    public void testSleep() {
        entity.setAwake(false);
        Assert.assertFalse(entity.isAwake());
    }

    @Test
    public void testWakeUp() {
        entity.setAwake(true);
        Assert.assertTrue(entity.isAwake());
    }
}
