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
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

public abstract class GlowProjectile extends GlowEntity implements Projectile {

    @Getter @Setter private boolean glowing;
    @Getter @Setter private boolean invulnerable;
    @Getter @Setter private ProjectileSource shooter;
    @Setter private boolean bounce;

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
    protected void pulsePhysics() {
        if (location.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
            setOnGround(true);
            collide(location.getBlock().getRelative(BlockFace.DOWN));
            return;
        }
        super.pulsePhysics();
    }

    public abstract void collide(Block block);

    public abstract void collide(LivingEntity entity);

    protected abstract int getObjectId();

    @Override
    public boolean doesBounce() {
        return bounce;
    }
}
