package net.glowstone.entity.projectile;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.passive.GlowChicken;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class GlowEgg extends GlowProjectile implements Egg {

    /**
     * Creates an egg entity.
     *
     * @param location the initial location
     */
    public GlowEgg(Location location) {
        super(location);
        setGravityAccel(new Vector(0, -0.3, 0));
        setBoundingBox(0.25, 0.25);
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
                GlowChicken chicken = (GlowChicken)
                        location.getWorld().spawnEntity(location.clone().add(0, 1, 0),
                                EntityType.CHICKEN);
                chicken.setAge(-24000);
            }
        }
    }

    @Override
    protected int getObjectId() {
        return SpawnObjectMessage.EGG;
    }
}
