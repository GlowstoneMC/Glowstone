package net.glowstone.entity;

import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowHangingEntityTest<T extends GlowHangingEntity> extends GlowEntityTest<T> {
    protected GlowHangingEntityTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
