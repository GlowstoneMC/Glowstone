package net.glowstone.entity.passive;

import java.util.EnumSet;
import java.util.function.Function;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public abstract class GlowAbstractHorseTest<T extends GlowAbstractHorse> extends GlowAnimalTest<T> {
    protected GlowAbstractHorseTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Test
    @Override
    public void testGetBreedingFood() {
        assertEquals(EnumSet.of(Material.GOLDEN_APPLE, Material.GOLDEN_CARROT),
                entity.getBreedingFood());
    }
}
