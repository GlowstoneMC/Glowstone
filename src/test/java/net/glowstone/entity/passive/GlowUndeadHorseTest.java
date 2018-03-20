package net.glowstone.entity.passive;

import java.util.function.Function;
import org.bukkit.Location;

public abstract class GlowUndeadHorseTest<T extends GlowUndeadHorse> extends GlowAbstractHorseTest<T> {
    protected GlowUndeadHorseTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
