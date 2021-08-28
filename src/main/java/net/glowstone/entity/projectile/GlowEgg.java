package net.glowstone.entity.projectile;

import net.glowstone.EventFactory;
import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.entity.GlowAgeable;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class GlowEgg extends GlowProjectile implements Egg {
    private static final double VERTICAL_GRAVITY_ACCEL = -0.03;

    /**
     * Creates a thrown egg with default speed.
     *
     * @param location the position and facing of the thrower
     */
    public GlowEgg(Location location) {
        super(location);
        setAirDrag(0.99);
        setGravityAccel(new Vector(0, VERTICAL_GRAVITY_ACCEL, 0));
        setApplyDragBeforeAccel(true);
        setVelocity(location.getDirection().multiply(3));
        setBoundingBox(0.25, 0.25);
    }

    /**
     * Process random spawn chicks when collide with a block.
     *
     * @param block the block that the egg collides with
     */
    @Override
    public void collide(Block block) {
        randomHatchSpawning(getLocation().clone());
        getWorld().spawnParticle(
            Particle.ITEM_CRACK, location, 5,
            0, 0, 0, 0.05, new ItemStack(Material.EGG));
        remove();
    }

    /**
     * Process random spawn chicks when collide with a living entity.
     *
     * @param entity the eneity that the egg collides with
     */
    @Override
    public void collide(LivingEntity entity) {
        ProjectileSource source = getShooter();
        // the entity receives fake damage.
        if (entity instanceof Entity) {
            entity.damage(0, (Entity) source, EntityDamageEvent.DamageCause.PROJECTILE);
        } else {
            entity.damage(0, EntityDamageEvent.DamageCause.PROJECTILE);
        }

        collide(entity.getLocation().getBlock());
    }

    /**
     * Handle spawning entities when the egg breaks.
     *
     * @param location The location the egg breaks
     */
    private void randomHatchSpawning(Location location) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // There is a 1/8 chance for egg to hatch and spawn at least 1 entity
        boolean hatching = random.nextInt(8) == 0;

        // ...and if the egg is hatching, there is now
        // 1/32 chance to spawn 3 more entities.
        byte amount;
        if (hatching) {
            amount = (byte) ((random.nextInt(32) == 0) ? 4 : 1);
        } else {
            amount = 0;
        }

        EntityType hatchingType = EntityType.CHICKEN;

        final ProjectileSource shooter = getShooter();
        if (shooter instanceof GlowPlayer) {
            PlayerEggThrowEvent event = EventFactory.getInstance().callEvent(
                new PlayerEggThrowEvent((GlowPlayer) shooter, this,
                    hatching, amount, hatchingType));

            amount = event.getNumHatches();
            hatching = event.isHatching();
            hatchingType = event.getHatchingType();
        }

        if (hatching) {
            for (int i = 0; i < amount; i++) {
                GlowEntity entity =
                    (GlowEntity) location.getWorld().spawnEntity(
                        location.clone().add(0, 0.3, 0), hatchingType);
                if (entity instanceof GlowAgeable) {
                    ((GlowAgeable) entity).setBaby();
                }
            }
        }
    }

    @Override
    protected int getObjectId() {
        return EntityNetworkUtil.getObjectId(EntityType.EGG);
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Material.EGG);
    }

    @Override
    public void setItem(@NotNull ItemStack itemStack) {
        // TODO: 1.16
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
