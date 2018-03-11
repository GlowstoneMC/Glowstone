package net.glowstone.entity.passive;

import java.util.function.Function;
import net.glowstone.entity.GlowAnimalTest;
import net.glowstone.entity.GlowEntityTest;
import org.bukkit.Location;

public abstract class GlowAbstractHorseTest<T extends GlowAbstractHorse> extends GlowAnimalTest<T> {
    protected GlowAbstractHorseTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
