package net.glowstone.entity.passive;

import java.util.EnumSet;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GlowChickenTest extends GlowAnimalTest<GlowChicken> {

    public GlowChickenTest() {
        super(GlowChicken::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.of(Material.SEEDS, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS, Material.BEETROOT_SEEDS));
    }
}
