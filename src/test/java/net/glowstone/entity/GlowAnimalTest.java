package net.glowstone.entity;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public abstract class GlowAnimalTest<T extends GlowAnimal> extends GlowAgeableTest<T> {
    protected GlowAnimalTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Test
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.noneOf(Material.class), entity.getBreedingFoods());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetBreedingFoodsReturnsImmutableSet() {
        entity.getBreedingFoods().add(Material.SANDSTONE);
    }

    @Test
    @Override
    public void testComputeGrowthAmount() {
        entity.setAge(-21000);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        for (Material food : entity.getBreedingFoods()) {
            Assertions.assertEquals(2100, entity.computeGrowthAmount(food), food.name());
        }
    }

    @Test
    public void testComputeGrowthAmountAdult() {
        entity.setAge(0);
        Assertions.assertEquals(0, entity.computeGrowthAmount(null));
        Assertions.assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        for (Material food : entity.getBreedingFoods()) {
            Assertions.assertEquals(0, entity.computeGrowthAmount(food), food.name());
        }
    }
}
