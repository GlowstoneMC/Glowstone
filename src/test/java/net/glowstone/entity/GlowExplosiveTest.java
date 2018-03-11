package net.glowstone.entity;

import java.util.function.Function;
import org.bukkit.Location;

public abstract class GlowExplosiveTest<T extends GlowExplosive> extends GlowEntityTest<T> {
    protected GlowExplosiveTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
