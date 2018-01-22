package net.glowstone.entity;

import net.glowstone.entity.annotation.EntityProperties;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Weather;

/**
 * Represents a Weather related entity, such as a storm.
 */
@EntityProperties(shouldSave = false)
public abstract class GlowWeather extends GlowEntity implements Weather {

    public GlowWeather(Location location) {
        super(location);
    }

    @Override
    public EntityType getType() {
        return EntityType.WEATHER;
    }
}
