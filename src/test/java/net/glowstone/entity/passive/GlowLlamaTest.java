package net.glowstone.entity.passive;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import org.bukkit.Material;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.bukkit.Material;
import org.junit.Test;

public class GlowLlamaTest extends GlowChestedHorseTest<GlowLlama> {
    public GlowLlamaTest() {
        super(GlowLlama::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.HAY_BLOCK), entity.getBreedingFoods());
    }

    @Test
    @Override
    public void testComputeGrowthAmount() {
        entity.setBaby();
        entity.setTamed(true);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        Assertions.assertEquals(200, entity.computeGrowthAmount(Material.WHEAT));
        Assertions.assertEquals(1800, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    public void testComputeGrowthAmountUntamed() {
        entity.setBaby();
        entity.setTamed(false);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        Assertions.assertEquals(200, entity.computeGrowthAmount(Material.WHEAT));
        Assertions.assertEquals(1800, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    @Override
    public void testComputeGrowthAmountAdult() {
        entity.setAge(0);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.WHEAT));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    public void testComputeGrowthAmountAlmostAdult() {
        entity.setAge(-1);
        entity.setTamed(true);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        Assertions.assertEquals(1, entity.computeGrowthAmount(Material.WHEAT));
        Assertions.assertEquals(1, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }
}
