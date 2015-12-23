package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemSpawn extends ItemType {
    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face, ItemStack holding, Vector clickedLoc) {
        GlowBlock target = against.getRelative(face);

        EntityType type = EntityType.fromId(holding.getDurability());
        if (type == null || !type.isSpawnable()) return;

        try {
            target.getWorld().spawnEntity(target.getLocation(), type);
        } catch (UnsupportedOperationException ignored) {
            //Some entities are missing
        }
    }
}
