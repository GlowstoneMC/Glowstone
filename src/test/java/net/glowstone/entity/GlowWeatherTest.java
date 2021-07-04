package net.glowstone.entity;

import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowWeatherTest<T extends GlowWeather> extends GlowEntityTest<T> {
    protected GlowWeatherTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
