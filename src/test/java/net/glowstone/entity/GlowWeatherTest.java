package net.glowstone.entity;

import java.util.function.Function;
import org.bukkit.Location;

public abstract class GlowWeatherTest<T extends GlowWeather> extends GlowEntityTest<T> {
    protected GlowWeatherTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
