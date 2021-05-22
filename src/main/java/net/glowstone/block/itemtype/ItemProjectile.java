package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.InventoryUtil;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * An item that can be used to throw a projectile (egg, snowball, etc.)
 */
public abstract class ItemProjectile extends ItemType {

    protected final EntityType entityType;

    public ItemProjectile(EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public Context getContext() {
        return Context.ANY;
    }

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        use(player, holding);
        InventoryUtil.consumeHeldItem(player, holding);
    }

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
                                ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        use(player, holding);
        InventoryUtil.consumeHeldItem(player, holding);
    }

    /**
     * Throws this projectile.
     *
     * @param player  the player throwing the projectile
     * @param holding the projectile as an item
     * @return the projectile as an entity
     */
    public abstract Projectile use(GlowPlayer player, ItemStack holding);
}
