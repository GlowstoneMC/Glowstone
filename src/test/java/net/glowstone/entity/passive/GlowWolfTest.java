package net.glowstone.entity.passive;

import org.bukkit.Material;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowWolfTest extends GlowTameableTest<GlowWolf> {
    public GlowWolfTest() {
        super(GlowWolf::new);
    }

    @Test
    @Override
    public void testGetBreedingFood() {
        assertEquals(EnumSet.of(Material.RAW_BEEF, Material.COOKED_BEEF,
                Material.RABBIT, Material.COOKED_RABBIT, Material.MUTTON,
                Material.COOKED_MUTTON, Material.PORK, Material.GRILLED_PORK,
                Material.RAW_CHICKEN, Material.COOKED_CHICKEN, Material.ROTTEN_FLESH),
                entity.getBreedingFood());
    }
}
