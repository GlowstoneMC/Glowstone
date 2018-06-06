package net.glowstone.entity.passive;

import java.util.EnumSet;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GlowOcelotTest extends GlowTameableTest<GlowOcelot> {
    public GlowOcelotTest() {
        super(GlowOcelot::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.of(Material.RAW_FISH));
    }
}
