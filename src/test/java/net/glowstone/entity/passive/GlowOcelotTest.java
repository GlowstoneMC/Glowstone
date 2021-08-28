package net.glowstone.entity.passive;

import org.bukkit.Material;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowOcelotTest extends GlowTameableTest<GlowOcelot> {
    public GlowOcelotTest() {
        super(GlowOcelot::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.SALMON), entity.getBreedingFoods());
    }
}
