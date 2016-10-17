package net.glowstone.entity.projectile;

import net.glowstone.entity.passive.GlowChicken;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GlowEgg extends GlowProjectile implements Egg {
    public GlowEgg(Location location) {
        super(location);
        setBoundingBox(0.25, 0.25);
    }

    @Override
    protected void pulsePhysics() {
        super.pulsePhysics();
        if (!isOnGround()) {
            setVelocity(getVelocity().subtract(new Vector(0, 0.3, 0)));
            location.add(getVelocity());
        }
    }

    @Override
    public void collide(Block block) {
        randomSpawnChicken(getLocation().add(0, 1, 0));
        remove();
    }

    @Override
    public void collide(LivingEntity entity) {
        randomSpawnChicken(getLocation());
        entity.damage(0);
        remove();
    }

    private void randomSpawnChicken(Location location) {
        Random random = ThreadLocalRandom.current();
        if (random.nextInt(8) == 0) {
            int count = 1;
            if (random.nextInt(32) == 0) {
                count = 4;
            }
            for (int i = 0; i < count; i++) {
                GlowChicken chicken = (GlowChicken) location.getWorld().spawnEntity(location.clone().add(0, 1, 0), EntityType.CHICKEN);
                chicken.setAge(-24000);
            }
        }
    }

    @Override
    protected int getObjectId() {
        return 62;
    }
}
