package net.glowstone.entity.passive;

import java.util.EnumSet;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GlowPigTest extends GlowAnimalTest<GlowPig> {
    public GlowPigTest() {
        super(GlowPig::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.of(Material.POTATO_ITEM, Material.CARROT_ITEM, Material.BEETROOT));
    }
}
