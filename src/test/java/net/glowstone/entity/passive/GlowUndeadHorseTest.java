package net.glowstone.entity.passive;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import java.util.EnumSet;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class GlowUndeadHorseTest<T extends GlowUndeadHorse> extends GlowAbstractHorseTest<T> {
    protected GlowUndeadHorseTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Test
    @Override
    public void testSetAgeAdult() {
        entity.setAge(0);
        assertEquals(0, entity.getAge());
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testSetBreedTrueBaby() {
        entity.setBaby();
        entity.setBreed(true);
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testSetBreedTrueAdult() {
        entity.setAge(1);
        assertFalse(entity.canBreed());
        entity.setBreed(true);
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.noneOf(Material.class));
    }
}
