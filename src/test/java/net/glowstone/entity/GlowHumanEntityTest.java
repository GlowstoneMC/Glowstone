package net.glowstone.entity;

import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowHumanEntityTest<T extends GlowHumanEntity> extends GlowLivingEntityTest<T> {
    protected GlowHumanEntityTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
