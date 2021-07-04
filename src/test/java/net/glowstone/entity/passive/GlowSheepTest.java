package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowSheepTest extends GlowAnimalTest<GlowSheep> {
    public GlowSheepTest() {
        super(GlowSheep::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.WHEAT), entity.getBreedingFoods());
    }
}
