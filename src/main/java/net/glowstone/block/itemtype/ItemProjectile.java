package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

/**
 * An item that can be used to throw a projectile (egg, snowball, etc.)
 */
public abstract class ItemProjectile extends ItemType {

    protected final EntityType entityType;

    public ItemProjectile(EntityType entityType) {
        this.entityType = entityType;
    }

    public abstract Projectile use(GlowPlayer player, ItemStack holding);
}
