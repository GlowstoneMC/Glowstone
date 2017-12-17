package net.glowstone.entity.monster;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vex;

public class GlowVex extends GlowMonster implements Vex {

    private int lifeTicks;

    public GlowVex(Location loc) {
        super(loc, EntityType.VEX, 14);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        lifeTicks = (random.nextInt(75) + 33) * 20;
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

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_VEX_AMBIENT;
    }
}
