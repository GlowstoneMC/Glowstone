package net.glowstone.entity.projectile;

import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;

public class GlowSnowball extends GlowProjectile implements Snowball {

    public GlowSnowball(Location location) {
        super(location);
        setBoundingBox(0.25, 0.25);
    }

    @Override
    public void collide(Block block) {
        remove();
    }

    @Override
    public void collide(LivingEntity entity) {
        entity.damage(0);
        remove();
    }

    @Override
    protected int getObjectId() {
        return SpawnObjectMessage.SNOWBALL;
    }
}
