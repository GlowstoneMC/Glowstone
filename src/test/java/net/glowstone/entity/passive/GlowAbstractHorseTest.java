package net.glowstone.entity.passive;

import java.util.EnumSet;
import java.util.function.Function;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertEquals;

public abstract class GlowAbstractHorseTest<T extends GlowAbstractHorse> extends GlowAnimalTest<T> {
    protected GlowAbstractHorseTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.GOLDEN_APPLE, Material.GOLDEN_CARROT),
                entity.getBreedingFoods());
    }

    @Test
    @Override
    public void testComputeGrowthAmount() {
        entity.setBaby();
        entity.setTamed(true);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        Assertions.assertEquals(600, entity.computeGrowthAmount(Material.SUGAR));
        Assertions.assertEquals(400, entity.computeGrowthAmount(Material.WHEAT));
        Assertions.assertEquals(1200, entity.computeGrowthAmount(Material.APPLE));
        Assertions.assertEquals(1200, entity.computeGrowthAmount(Material.GOLDEN_CARROT));
        Assertions.assertEquals(4800, entity.computeGrowthAmount(Material.GOLDEN_APPLE));
        Assertions.assertEquals(3600, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    public void testComputeGrowthAmountUntamed() {
        entity.setBaby();
        entity.setTamed(false);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        Assertions.assertEquals(600, entity.computeGrowthAmount(Material.SUGAR));
        Assertions.assertEquals(400, entity.computeGrowthAmount(Material.WHEAT));
        Assertions.assertEquals(1200, entity.computeGrowthAmount(Material.APPLE));
        Assertions.assertEquals(1200, entity.computeGrowthAmount(Material.GOLDEN_CARROT));
        Assertions.assertEquals(4800, entity.computeGrowthAmount(Material.GOLDEN_APPLE));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }


    @Test
    @Override
    public void testComputeGrowthAmountAdult() {
        entity.setAge(0);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SUGAR));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.WHEAT));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.APPLE));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.GOLDEN_CARROT));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.GOLDEN_APPLE));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    public void testComputeGrowthAmountAlmostAdult() {
        entity.setAge(-1);
        entity.setTamed(true);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        Assertions.assertEquals(1, entity.computeGrowthAmount(Material.SUGAR));
        Assertions.assertEquals(1, entity.computeGrowthAmount(Material.WHEAT));
        Assertions.assertEquals(1, entity.computeGrowthAmount(Material.APPLE));
        Assertions.assertEquals(1, entity.computeGrowthAmount(Material.GOLDEN_CARROT));
        Assertions.assertEquals(1, entity.computeGrowthAmount(Material.GOLDEN_APPLE));
        Assertions.assertEquals(1, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }
}
