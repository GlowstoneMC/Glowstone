package net.glowstone.entity.passive;

import java.util.function.Function;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Location;

public abstract class GlowTameableTest<T extends GlowTameable> extends GlowAnimalTest<T> {
    protected GlowTameableTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
