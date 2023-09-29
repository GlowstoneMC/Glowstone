package net.glowstone.entity.monster;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Vex;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

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
    public Sound getDeathSound() {
        return Sound.ENTITY_VEX_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_VEX_HURT;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_VEX_AMBIENT;
    }

    @Override
    public boolean isCharging() {
        // TODO: vex charging
        return false;
    }

    @Override
    public void setCharging(boolean charging) {

    }

    @Override
    public @Nullable Location getBound() {
        return null;
    }

    @Override
    public void setBound(@Nullable Location location) {

    }

    @Override
    public boolean hasLimitedLife() {
        return false;
    }

    @Override
    public boolean hasLimitedLifetime() {
        return false;
    }

    @Override
    public void setLimitedLifetime(boolean hasLimitedLifetime) {

    }

    @Override
    public int getLimitedLifetimeTicks() {
        return 0;
    }

    @Override
    public void setLimitedLifetimeTicks(int ticks) {

    }
}
