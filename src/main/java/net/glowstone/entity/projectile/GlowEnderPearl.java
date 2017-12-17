package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;

public class GlowEnderPearl extends GlowProjectile implements EnderPearl {
    private static final double ENDER_PEARL_DAMAGE = 5.0;

    public GlowEnderPearl(Location location) {
        super(location);
    }

    @Override
    public void collide(Block block) {
        teleportThrower();
    }

    @Override
    public void collide(LivingEntity entity) {
        teleportThrower();
    }

    private void teleportThrower() {
        ProjectileSource source = getShooter();
        if (source instanceof Entity) {
            ((Entity) source).teleport(location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
            if (source instanceof LivingEntity) {
                ((LivingEntity) source).damage(ENDER_PEARL_DAMAGE,
                        EntityDamageEvent.DamageCause.FALL);
            }
        }
        remove();
    }

    @Override
    protected int getObjectId() {
        // TODO
        return 0;
    }
}
