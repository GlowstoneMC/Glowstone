package net.glowstone.entity.projectile;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownExpBottle;

// TODO: stubs
public class GlowThrownExpBottle extends GlowProjectile implements ThrownExpBottle {
    public GlowThrownExpBottle(Location location) {
        super(location);
    }

    @Override
    public void collide(Block block) {
        spawnOrb();
    }

    @Override
    public void collide(LivingEntity entity) {
        spawnOrb();
    }

    private void spawnOrb() {
        int xp = ThreadLocalRandom.current().nextInt(9) + 3;
        ((ExperienceOrb) world.spawnEntity(location, EntityType.EXPERIENCE_ORB)).setExperience(xp);
        remove();
    }

    @Override
    protected int getObjectId() {
        return 75;
    }
}
