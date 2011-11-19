package net.glowstone.entity;

import net.glowstone.GlowServer;

import org.bukkit.entity.LightningStrike;

import net.glowstone.util.Position;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnLightningStrikeMessage;
import net.glowstone.GlowWorld;

/**
 * A GlowLightning strike is an entity produced during thunderstorms.
 */
public class GlowLightningStrike extends GlowWeather implements LightningStrike {

    /**
     * Whether the lightning strike is just for effect.
     */
    private boolean effect;
    
    /**
     * How long this lightning strike has to remain in the world.
     */
    private final int ticksToLive;

    public GlowLightningStrike(GlowServer server, GlowWorld world, boolean effect) {
        super(server, world);
        this.effect = effect;
        this.ticksToLive = 30;
    }

    public boolean isEffect() {
        return effect;
    }

    @Override
    public void pulse() {
        super.pulse();
        if (getTicksLived() >= ticksToLive) {
            remove();
        }
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return new SpawnLightningStrikeMessage(id, x, y, z);
    }

    @Override
    public Message createUpdateMessage() {
        return null;
    }
    
}
