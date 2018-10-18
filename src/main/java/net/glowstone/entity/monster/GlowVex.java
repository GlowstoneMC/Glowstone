package net.glowstone.entity.monster;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Vex;

public class GlowVex extends GlowMonster implements Vex {

    @Getter
    @Setter
    private int lifeTicks;

    @Getter
    @Setter
    private Mob summoner; // TODO: Vex summoner/owner

    /**
     * Creates a vex with a random lifespan.
     *
     * @param loc the location
     */
    public GlowVex(Location loc) {
        super(loc, EntityType.VEX, 14);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        lifeTicks = TickUtil.secondsToTicks((random.nextInt(75) + 33));
        setBoundingBox(0.4, 0.8);
    }

    @Override
    public void pulse() {
        super.pulse();
        lifeTicks--;
        if (lifeTicks == 0) {
            damage(1.0);
            lifeTicks = 20;
        }
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_VEX_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_VEX_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_VEX_AMBIENT;
    }
}
