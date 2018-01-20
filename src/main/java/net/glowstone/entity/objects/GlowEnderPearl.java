package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.List;
import net.glowstone.entity.projectile.GlowProjectile;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class GlowEnderPearl extends GlowProjectile implements EnderPearl {

    private float speed;

    /**
     * Creates a thrown ender pearl.
     *
     * @param location the position and facing of the thrower
     * @param speed the initial speed
     */
    public GlowEnderPearl(Location location, float speed) {
        super(location);
        setDrag(0.99, false);
        setDrag(0.99, true);
        setGravityAccel(new Vector(0,-0.06,0));
        this.speed = speed;
        setVelocity(location.getDirection().multiply(this.speed));
    }

    @Override
    protected void pulsePhysics() {
        velocity.setY(airDrag * (velocity.getY() + getGravityAccel().getY()));

        velocity.setX(velocity.getX() * 0.95);
        velocity.setZ(velocity.getZ() * 0.95);

        setRawLocation(location.clone().add(velocity));

        // If the EnderPearl collides with anything except air/fluids
        if (!location.getBlock().isLiquid() && !location.getBlock().isEmpty()
                && shooter instanceof Entity) {
            ((Entity) shooter).teleport(location);
            this.remove();
        }
    }

    @Override
    public void collide(Block block) {
        // No-op: collisions are handled in pulsePhysics
    }

    @Override
    public void collide(LivingEntity entity) {
        // No-op
    }

    @Override
    protected int getObjectId() {
        return SpawnObjectMessage.THROWN_ENDERPEARL;
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Arrays.asList(
                new SpawnObjectMessage(
                        entityId, getUniqueId(), SpawnObjectMessage.THROWN_ENDERPEARL, location),
                new EntityMetadataMessage(entityId, metadata.getEntryList()),
                // These keep the client from assigning a random velocity
                new EntityTeleportMessage(entityId, location),
                new EntityVelocityMessage(entityId, getVelocity())
            );
    }
}
