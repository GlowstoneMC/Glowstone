package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.InventoryUtil;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemTool extends ItemType {

    @Override
    public final void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (onToolRightClick(player, target, face, holding, clickedLoc, hand)) {
            player.getInventory().setItem(hand, InventoryUtil.damageItem(player, holding));
        }
    }

    /**
     * Called when a player used (right clicked with) the tool.
     *
     * @param player The player using the tool
     * @param target The block right clicked with the tool
     * @param face The clicked BlockFace
     * @param holding The tool
     * @param clickedLoc The click location on the block
     * @param player the player this advancement message is for
     * @return true if the tool's durability should be decreased, false otherwise
     */
    protected boolean onToolRightClick(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        // to be overridden in subclasses
        return false;
    }
}
