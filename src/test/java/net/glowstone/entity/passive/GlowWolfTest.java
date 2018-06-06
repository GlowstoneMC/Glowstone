package net.glowstone.entity.passive;

import java.util.EnumSet;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GlowWolfTest extends GlowTameableTest<GlowWolf> {
    public GlowWolfTest() {
        super(GlowWolf::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.of(Material.RAW_BEEF, Material.COOKED_BEEF,
                Material.RABBIT, Material.COOKED_RABBIT, Material.MUTTON,
                Material.COOKED_MUTTON, Material.PORK, Material.GRILLED_PORK,
                Material.RAW_CHICKEN, Material.COOKED_CHICKEN, Material.ROTTEN_FLESH));
    }
}
