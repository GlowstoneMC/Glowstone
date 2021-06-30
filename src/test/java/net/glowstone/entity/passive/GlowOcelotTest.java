package net.glowstone.entity.passive;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import org.bukkit.Material;
import org.junit.Test;

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
