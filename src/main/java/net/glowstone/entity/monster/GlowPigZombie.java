package net.glowstone.entity.monster;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Random;
import java.util.UUID;

public class GlowPigZombie extends GlowZombie implements PigZombie {

    @Getter
    @Setter
    private int anger;
    @Getter
    @Setter
    private UUID hurtBy;

    public GlowPigZombie(Location loc) {
        super(loc, EntityType.ZOMBIFIED_PIGLIN);
    }

    @Override
    public boolean isAngry() {
        return anger > 0;
    }

    @Override
    public void setAngry(boolean angry) {
        if (!angry) {
            anger = 0;
        } else if (isAngry()) {
            anger = (int) (new Random().nextGaussian() * 400) + 400;
        }
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ZOMBIFIED_PIGLIN_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    public boolean canTakeDamage(DamageCause damageCause) {
        if (damageCause == DamageCause.FIRE || damageCause == DamageCause.LAVA) {
            return false;
        }
        return super.canTakeDamage(damageCause);
    }
}
