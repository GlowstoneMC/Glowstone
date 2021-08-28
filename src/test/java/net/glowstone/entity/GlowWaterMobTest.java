package net.glowstone.entity;

import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowWaterMobTest<T extends GlowWaterMob> extends GlowCreatureTest<T> {
    protected GlowWaterMobTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
