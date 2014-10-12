package net.glowstone.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class GlowEntityBlockDamageHandler {

    /**
     * The entity that takes damages.
     */
    private GlowEntity parentEntity = null;

    /**
     * The last taken damage from a cactus.
     */
    private long lastCactusDamageTick = 0;


    public GlowEntityBlockDamageHandler(GlowEntity targetEntity) {
        this.parentEntity = targetEntity;
    }

    protected void damage(EntityDamageEvent event) {
        if (!(parentEntity instanceof GlowLivingEntity)) {
            parentEntity.remove();
        } else {
            LivingEntity living = (LivingEntity) parentEntity;
            living.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                living.damage(event.getDamage(), null);
            }
        }
    }

    /**
     * Check damages that needs to be applied this tick.
     */
    public void pulse() {

        Location location = parentEntity.getLocation();
        World w = location.getWorld();

        double x = location.getX() % 1;
        double y = location.getY() % 1;
        double z = location.getZ() % 1;

        Location[] blocksTouching = new Location[]{null, null, new Location(w, location.getX(), location.getY() - 1, location.getZ())};

        if ((x > 0 && x >= 0.7) || (x < 0 && x > -0.3)) {
            blocksTouching[0] = new Location(w, location.getX() + 1, location.getY(), location.getZ());
        } else if ((x > 0 && x <= 0.3) || (x < 0 && x < -0.7)) {
            blocksTouching[0] = new Location(w, location.getX() - 1, location.getY(), location.getZ());
        }

        if ((z > 0 && z >= 0.7) || (z < 0 && z > -0.3)) {
            blocksTouching[1] = new Location(w, location.getX(), location.getY(), location.getZ() + 1);
        } else if ((z > 0 && z <= 0.3) || (z < 0 && z < -0.7)) {
            blocksTouching[1] = new Location(w, location.getX(), location.getY(), location.getZ() - 1);
        }

        if (y > 0.9) {
            blocksTouching[2] = new Location(w, location.getX(), location.getY(), location.getZ());
        }

        for (Location touchingLoc : blocksTouching) {
            if (touchingLoc == null) {
                continue;
            }

            Block touching = w.getBlockAt(touchingLoc);
            if (touching == null) {
                continue;
            }

            if (touching.getType() == Material.CACTUS) {
                if (lastCactusDamageTick + 10 < parentEntity.getWorld().getWorldAge()) {
                    EntityDamageByBlockEvent ev = new EntityDamageByBlockEvent(touching, parentEntity, EntityDamageEvent.DamageCause.CONTACT, 1.0);
                    this.parentEntity.getServer().getPluginManager().callEvent(ev);
                    damage(ev); // The event is called in DAMAGE
                    lastCactusDamageTick = parentEntity.getWorld().getWorldAge();
                }
            }
        }
    }

}
