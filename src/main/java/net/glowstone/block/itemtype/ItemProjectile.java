package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.projectile.GlowProjectile;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * An item that can be used to throw a projectile (egg, snowball, etc.)
 */
public abstract class ItemProjectile extends ItemType {

    private final EntityType entityType;

    public ItemProjectile(EntityType entityType) {
        this.entityType = entityType;
    }

    public abstract GlowProjectile throwProjectile(GlowPlayer player, ItemStack stack);

    protected GlowProjectile throwProjectile(Location location, Vector originalVector, float offset, float velocity) {
        double k = Math.toRadians(-1);
        double x = cos(k * location.getPitch()) * sin(k * location.getYaw());
        double y = sin(k * (location.getPitch() - offset));
        double z = cos(location.getPitch() * k) * cos(location.getYaw() * k);
        GlowProjectile projectile = throwProjectile(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()), x, y, z, velocity);
        projectile.getVelocity().add(originalVector);
        return projectile;
    }

    protected GlowProjectile throwProjectile(Location location, double x, double y, double z, float velocity) {
        GlowProjectile projectile = (GlowProjectile) location.getWorld().spawnEntity(location, entityType);
        double k = Math.sqrt(x * x + y * y + z * z);
        x += (x * (velocity - k)) / k;
        y += (y * (velocity - k)) / k;
        z += (z * (velocity - k)) / k;
        projectile.setVelocity(new Vector(x, y, z));
        projectile.setRawLocation(location);
        return projectile;
    }
}
