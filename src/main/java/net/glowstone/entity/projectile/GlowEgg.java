package net.glowstone.entity.projectile;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.passive.GlowChicken;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

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
        randomSpawnChicken(getLocation().clone());
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
     * Handle spawning chicks when the egg breaks.
     * There is a 1/8 chance to spawn 1 chick,
     * and if that happends, there is a 1/32 chance to spawn 3 more chicks.
     *
     * @param location The location the egg breaks
     */
    private void randomSpawnChicken(Location location) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int amount = 0;

        if (random.nextInt(8) == 0) {
            amount = 1;
            if (random.nextInt(32) == 0) {
                amount = 4;
            }
        }

        for (int i = 0; i < amount; i++) {
            GlowChicken chicken =
                (GlowChicken) location.getWorld().spawnEntity(
                    location.clone().add(0, 0.3, 0), EntityType.CHICKEN);
            chicken.setBaby();
        }
    }

    @Override
    protected int getObjectId() {
        return SpawnObjectMessage.EGG;
    }
}
