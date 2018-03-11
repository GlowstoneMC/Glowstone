package net.glowstone.entity;

import java.util.function.Function;
import org.bukkit.Location;

public abstract class GlowWaterMobTest<T extends GlowWaterMob> extends GlowCreatureTest<T> {
    protected GlowWaterMobTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
