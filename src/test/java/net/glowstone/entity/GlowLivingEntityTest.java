package net.glowstone.entity;

import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowLivingEntityTest<T extends GlowLivingEntity> extends GlowEntityTest<T> {
    protected GlowLivingEntityTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
