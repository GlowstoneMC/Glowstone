package net.glowstone.entity.projectile;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;

public abstract class GlowProjectile extends GlowEntity implements Projectile {

    private ProjectileSource shooter;

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
                new SpawnObjectMessage(id, getUniqueId(), getObjectId(), x, y, z, pitch, yaw),
                new EntityMetadataMessage(id, metadata.getEntryList()),
                new EntityVelocityMessage(id, getVelocity())
        );
    }

    @Override
    public void pulse() {
        if (location.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) {
            collide(location.clone().subtract(0, 1, 0).getBlock());
            return;
        }
        // todo collide with entities
        super.pulse();
    }

    public abstract void collide(Block block);

    public abstract void collide(LivingEntity entity);

    @Override
    public void setGlowing(boolean b) {

    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public void setInvulnerable(boolean b) {

    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    @Override
    public Location getOrigin() {
        return null;
    }

    protected abstract int getObjectId();

    @Override
    public LivingEntity _INVALID_getShooter() {
        return null;
    }

    @Override
    public ProjectileSource getShooter() {
        return shooter;
    }

    @Override
    public void _INVALID_setShooter(LivingEntity livingEntity) {
    }

    @Override
    public void setShooter(ProjectileSource source) {
        this.shooter = source;
    }

    @Override
    public boolean doesBounce() {
        return false;
    }

    @Override
    public void setBounce(boolean b) {

    }
}
