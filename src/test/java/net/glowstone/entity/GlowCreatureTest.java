package net.glowstone.entity;

import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowCreatureTest<T extends GlowCreature> extends GlowLivingEntityTest<T> {
    protected GlowCreatureTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
