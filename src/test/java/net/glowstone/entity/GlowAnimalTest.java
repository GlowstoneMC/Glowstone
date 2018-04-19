package net.glowstone.entity;

import java.util.EnumSet;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
