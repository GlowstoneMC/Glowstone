package net.glowstone.entity.monster;

import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.annotation.Sounds;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@Sounds(hurt = Sound.ENTITY_ZOMBIE_PIG_HURT,
        death = Sound.ENTITY_ZOMBIE_PIG_DEATH,
        ambient = Sound.ENTITY_ZOMBIE_PIG_AMBIENT)
public class GlowPigZombie extends GlowZombie implements PigZombie {

    @Getter
    @Setter
    private int anger;
    @Getter
    @Setter
    private UUID hurtBy;

    public GlowPigZombie(Location loc) {
        super(loc, EntityType.PIG_ZOMBIE);
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
    public boolean canTakeDamage(DamageCause damageCause) {
        if (damageCause == DamageCause.FIRE || damageCause == DamageCause.LAVA) {
            return false;
        }
        return super.canTakeDamage(damageCause);
    }
}
