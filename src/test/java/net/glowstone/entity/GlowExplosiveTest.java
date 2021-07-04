package net.glowstone.entity;

import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowExplosiveTest<T extends GlowExplosive> extends GlowEntityTest<T> {
    protected GlowExplosiveTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
