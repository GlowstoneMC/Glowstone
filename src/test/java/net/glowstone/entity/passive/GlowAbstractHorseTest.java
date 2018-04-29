package net.glowstone.entity.passive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.function.Function;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.Test;

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
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        assertEquals(600, entity.computeGrowthAmount(Material.SUGAR));
        assertEquals(400, entity.computeGrowthAmount(Material.WHEAT));
        assertEquals(1200, entity.computeGrowthAmount(Material.APPLE));
        assertEquals(1200, entity.computeGrowthAmount(Material.GOLDEN_CARROT));
        assertEquals(4800, entity.computeGrowthAmount(Material.GOLDEN_APPLE));
        assertEquals(3600, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    public void testComputeGrowthAmountUntamed() {
        entity.setBaby();
        entity.setTamed(false);
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        assertEquals(600, entity.computeGrowthAmount(Material.SUGAR));
        assertEquals(400, entity.computeGrowthAmount(Material.WHEAT));
        assertEquals(1200, entity.computeGrowthAmount(Material.APPLE));
        assertEquals(1200, entity.computeGrowthAmount(Material.GOLDEN_CARROT));
        assertEquals(4800, entity.computeGrowthAmount(Material.GOLDEN_APPLE));
        assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }


    @Test
    @Override
    public void testComputeGrowthAmountAdult() {
        entity.setAge(0);
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        assertEquals(0, entity.computeGrowthAmount(Material.SUGAR));
        assertEquals(0, entity.computeGrowthAmount(Material.WHEAT));
        assertEquals(0, entity.computeGrowthAmount(Material.APPLE));
        assertEquals(0, entity.computeGrowthAmount(Material.GOLDEN_CARROT));
        assertEquals(0, entity.computeGrowthAmount(Material.GOLDEN_APPLE));
        assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }

    @Test
    public void testComputeGrowthAmountAlmostAdult() {
        entity.setAge(-1);
        entity.setTamed(true);
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        assertEquals(0, entity.computeGrowthAmount(Material.SUGAR));
        assertEquals(0, entity.computeGrowthAmount(Material.WHEAT));
        assertEquals(0, entity.computeGrowthAmount(Material.APPLE));
        assertEquals(0, entity.computeGrowthAmount(Material.GOLDEN_CARROT));
        assertEquals(0, entity.computeGrowthAmount(Material.GOLDEN_APPLE));
        assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
    }
}
