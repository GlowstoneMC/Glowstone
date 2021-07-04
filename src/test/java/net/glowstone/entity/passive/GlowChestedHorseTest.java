package net.glowstone.entity.passive;

import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowChestedHorseTest<T extends GlowChestedHorse> extends GlowAbstractHorseTest<T> {
    protected GlowChestedHorseTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
