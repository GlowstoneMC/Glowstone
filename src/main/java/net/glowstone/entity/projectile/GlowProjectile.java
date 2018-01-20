package net.glowstone.entity.projectile;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public abstract class GlowProjectile extends GlowEntity implements Projectile {

    @Getter
    @Setter
    private boolean glowing;
    @Getter
    @Setter
    private boolean invulnerable;
    @Getter
    @Setter
    private ProjectileSource shooter;
    @Setter
    private boolean bounce;

    /**
     * Creates a projectile.
     *
     * @param location the initial location
     */
    public GlowProjectile(Location location) {
        super(location);
    }

    @Override
    public List<Message> createSpawnMessage() {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Arrays.asList(
                new SpawnObjectMessage(entityId, getUniqueId(), getObjectId(), x, y, z, pitch, yaw),
                new EntityMetadataMessage(entityId, metadata.getEntryList()),
                new EntityVelocityMessage(entityId, getVelocity())
        );
    }

    @Override
    protected void pulsePhysics() {
        Vector size = boundingBox.getSize();
        for (Entity entity : world.getNearbyEntities(
                location, size.getX(), size.getY(), size.getZ())) {
            if (entity instanceof LivingEntity && !(entity.equals(shooter))) {
                collide((LivingEntity) entity);
                break;
            }
        }
        super.pulsePhysics();
    }

    @Override
    public abstract void collide(Block block);

    public abstract void collide(LivingEntity entity);

    protected abstract int getObjectId();

    @Override
    public boolean doesBounce() {
        return bounce;
    }
}
