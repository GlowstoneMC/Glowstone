package net.glowstone.entity;

import static org.testng.AssertJUnit.assertEquals;

import java.util.EnumSet;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class GlowAnimalTest<T extends GlowAnimal> extends GlowAgeableTest<T> {
    protected GlowAnimalTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Test
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.noneOf(Material.class));
    }

    @Test
    public void testGetBreedingFoodsReturnsImmutableSet() {
        entity.getBreedingFoods().add(Material.SANDSTONE);
    }

    @Test
    @Override
    public void testComputeGrowthAmount() {
        entity.setAge(-21000);
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        for (Material food : entity.getBreedingFoods()) {
            assertEquals(food.name(), 2100, entity.computeGrowthAmount(food));
        }
    }

    @Test
    public void testComputeGrowthAmountAdult() {
        entity.setAge(0);
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        for (Material food : entity.getBreedingFoods()) {
            assertEquals( food.name(), 0, entity.computeGrowthAmount(food));
        }
    }
}
