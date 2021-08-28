package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.Test;

import java.util.EnumSet;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class GlowAbstractHorseTest<T extends GlowAbstractHorse> extends GlowAnimalTest<T> {
    protected GlowAbstractHorseTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Test
    @Override
    public void testSetAgeAdult() {
        entity.setTamed(true);
        super.testSetAgeAdult();
    }

    @Test
    public void testCannotBreedIfUntamed() {
        entity.setAge(0);
        entity.setTamed(false);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testSetBreedTrueAdult() {
        entity.setTamed(true);
        super.testSetBreedTrueAdult();
    }

    @Test
    @Override
    public void testSetBreedTrueBaby() {
        entity.setTamed(true);
        super.testSetBreedTrueBaby();
    }

    @Test
    public void testSetBreedTrueAdultUntamed() {
        entity.setTamed(false);
        entity.setBreed(true);
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    public void testSetAgeAdultCannotBreedTamed() {
        entity.setTamed(true);
        testSetAgeAdultCannotBreed();
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.GOLDEN_APPLE, Material.GOLDEN_CARROT),
                entity.getBreedingFoods());
    }

    @Test
    @Override
    public void testFoodSetsLoveMode() {
        entity.setTamed(true);
        super.testFoodSetsLoveMode();
    }

    @Test
    @Override
    public void testFoodDoesNotSetLoveModeAfterBreeding() {
        entity.setTamed(true);
        super.testFoodDoesNotSetLoveModeAfterBreeding();
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
