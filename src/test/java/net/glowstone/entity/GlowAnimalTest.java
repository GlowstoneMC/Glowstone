package net.glowstone.entity;

import java.util.function.Function;
import org.bukkit.Location;

public abstract class GlowAnimalTest<T extends GlowAnimal> extends GlowAgeableTest<T> {
    protected GlowAnimalTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
