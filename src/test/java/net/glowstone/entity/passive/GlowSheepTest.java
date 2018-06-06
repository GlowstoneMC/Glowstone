package net.glowstone.entity.passive;

import java.util.EnumSet;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GlowSheepTest extends GlowAnimalTest<GlowSheep> {
    public GlowSheepTest() {
        super(GlowSheep::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.of(Material.WHEAT));
    }
}
