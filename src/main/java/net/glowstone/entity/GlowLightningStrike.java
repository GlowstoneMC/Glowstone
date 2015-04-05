package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.net.message.play.entity.SpawnLightningStrikeMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    /**
     * How far a living entity must be from the lightning strike to damage it
     */
    private final float distanceToDamage;

    /**
     * How far a living entity must be from the lightning strike to set it on fire
     */
    private final float distanceToIgnition;

    /**
     * For how long the living entity will burn if struck directly
     */
    private final int burnTicks;

    private final Random random;

    public GlowLightningStrike(Location location, boolean effect, Random random) {
        super(location);
        this.effect = effect;
        this.ticksToLive = 30;
        distanceToDamage = 5;
        distanceToIgnition = 1;
        burnTicks = 75;
        this.random = random;
    }

    @Override
    public EntityType getType() {
        return EntityType.LIGHTNING;
    }

    @Override
    public boolean isEffect() {
        return effect;
    }

    @Override
    public void pulse() {
        super.pulse();
        if (getTicksLived() >= ticksToLive) {
            remove();
        }
        if (getTicksLived() == 1) {
            // Play Sound
            location.getWorld().playSound(location, Sound.AMBIENCE_THUNDER, 10000, 0.8F + random.nextFloat() * 0.2F);
            location.getWorld().playSound(location, Sound.EXPLODE, 2, 0.5F + random.nextFloat() * 0.2F);
            // Deal Damage to nearby enemies
            if (effect) {
                return; // It's just a visual, don't deal damage
            } else {
                for (LivingEntity livingEntity : location.getWorld().getLivingEntities()) {
                    if (location.distance(livingEntity.getLocation()) <= distanceToDamage) {
                        livingEntity.damage(5, this, EntityDamageEvent.DamageCause.LIGHTNING);
                    }
                    if (location.distance(livingEntity.getLocation()) <= distanceToIgnition) {
                        livingEntity.setFireTicks(burnTicks);
                    }
                }
            }
        }
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return Arrays.<Message>asList(new SpawnLightningStrikeMessage(id, x, y, z));
    }

    @Override
    public List<Message> createUpdateMessage() {
        return Arrays.asList();
    }
}
