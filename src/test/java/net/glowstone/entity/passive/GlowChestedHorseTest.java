package net.glowstone.entity.passive;

import java.util.function.Function;
import org.bukkit.Location;

public abstract class GlowChestedHorseTest<T extends GlowChestedHorse> extends GlowAbstractHorseTest<T> {
    protected GlowChestedHorseTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
