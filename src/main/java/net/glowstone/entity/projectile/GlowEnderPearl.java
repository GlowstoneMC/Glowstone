package net.glowstone.entity.projectile;

import com.flowpowered.network.Message;
import io.netty.util.internal.ThreadLocalRandom;
import java.util.Arrays;
import java.util.List;

import net.glowstone.entity.monster.GlowEndermite;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class GlowEnderPearl extends GlowProjectile implements EnderPearl {

    private static final double ENDER_PEARL_DAMAGE = 5.0;

    /**
     * Creates a thrown ender pearl with default speed.
     *
     * @param location the position and facing of the thrower
     */
    public GlowEnderPearl(Location location) {
        this(location, 3.0f);
    }

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
        setHorizontalAirDrag(1);
        setGravityAccel(new Vector(0, -0.03, 0));
        setVelocity(location.getDirection().multiply(speed));
        setBoundingBox(0.25, 0.25);
    }

    /**
     * Process teleportation when collide with a block.
     *
     * @param block the block that the ender pearl collides with
     */
    @Override
    public void collide(Block block) {
        ProjectileSource source = getShooter();
        if (source instanceof Entity) {
            Location destination = getLocation();
            Entity entity = (Entity) source;
            Location entityLocation = entity.getLocation();

            // Add 0.3 to Y value. Otherwise the eneity will get stuck inside a block.
            destination.add(0, 0.3, 0);
            // Renew the pitch and yaw value right before teleportation.
            destination.setPitch(entityLocation.getPitch());
            destination.setYaw(entityLocation.getYaw());

            entity.teleport(destination, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);

            // Give fall damage to the eneity that threw this ender pearl.
            if (source instanceof LivingEntity) {
                ((LivingEntity) entity).damage(ENDER_PEARL_DAMAGE,
                        EntityDamageEvent.DamageCause.FALL);
            }
        }

        // Spawn endermite for 5% chance.
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextInt(100) < 5) {
            getWorld().spawn(
                location,
                GlowEndermite.class,
                CreatureSpawnEvent.SpawnReason.ENDER_PEARL);
        }

        remove();
    }

    /**
     * Process teleportation when collide with an entity.
     *
     * @param entity the eneity that the ender pearl collides with
     */
    @Override
    public void collide(LivingEntity entity) {
        ProjectileSource source = getShooter();
        // the entity receives fake damage.
        if (source instanceof Entity) {
            entity.damage(0, (Entity) source, EntityDamageEvent.DamageCause.PROJECTILE);
        } else {
            entity.damage(0, EntityDamageEvent.DamageCause.PROJECTILE);
        }
        collide(entity.getLocation().getBlock());
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
