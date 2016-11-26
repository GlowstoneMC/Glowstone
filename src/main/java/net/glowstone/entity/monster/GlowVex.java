package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vex;

import java.util.concurrent.ThreadLocalRandom;

public class GlowVex extends GlowMonster implements Vex {
    private int lifeTicks;

    public GlowVex(Location loc) {
        super(loc, EntityType.VEX, 14);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        lifeTicks = (random.nextInt(75) + 33) * 20;
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

    public int getLifeTicks() {
        return lifeTicks;
    }

    public void setLifeTicks(int lifeTicks) {
        this.lifeTicks = lifeTicks;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_VEX_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_VEX_HURT;
    }
}
