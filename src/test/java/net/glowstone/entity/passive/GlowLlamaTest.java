package net.glowstone.entity.passive;

import java.util.EnumSet;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class GlowLlamaTest extends GlowChestedHorseTest<GlowLlama> {
    public GlowLlamaTest() {
        super(GlowLlama::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.of(Material.HAY_BLOCK));
    }

    @Test
    @Override
    public void testComputeGrowthAmount() {
        entity.setBaby();
        entity.setTamed(true);
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(null));
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        AssertJUnit.assertEquals(200, entity.computeGrowthAmount(Material.WHEAT));
        AssertJUnit.assertEquals(1800, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    public void testComputeGrowthAmountUntamed() {
        entity.setBaby();
        entity.setTamed(false);
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(null));
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        AssertJUnit.assertEquals(200, entity.computeGrowthAmount(Material.WHEAT));
        AssertJUnit.assertEquals(1800, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    @Override
    public void testComputeGrowthAmountAdult() {
        entity.setAge(0);
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(null));
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(Material.WHEAT));
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    public void testComputeGrowthAmountAlmostAdult() {
        entity.setAge(-1);
        entity.setTamed(true);
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(null));
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(Material.WHEAT));
        AssertJUnit.assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }
}
