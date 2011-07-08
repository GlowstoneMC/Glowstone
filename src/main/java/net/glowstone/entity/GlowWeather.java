package net.glowstone.entity;

import org.bukkit.entity.Weather;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;

/**
 * Represents a Weather related entity, such as a storm.
 */
public abstract class GlowWeather extends GlowEntity implements Weather {

    public GlowWeather(GlowServer server, GlowWorld world) {
        super(server, world);
    }
    
}
