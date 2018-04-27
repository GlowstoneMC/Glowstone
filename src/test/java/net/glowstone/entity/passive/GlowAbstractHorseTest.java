package net.glowstone.entity.passive;

import static org.junit.Assert.assertEquals;

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
}
