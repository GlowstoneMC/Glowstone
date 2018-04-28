package net.glowstone.entity.passive;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.junit.Test;

public class GlowMooshroomTest extends GlowAnimalTest<GlowMooshroom> {
    public GlowMooshroomTest() {
        super(GlowMooshroom::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.WHEAT), entity.getBreedingFoods());
    }
}
