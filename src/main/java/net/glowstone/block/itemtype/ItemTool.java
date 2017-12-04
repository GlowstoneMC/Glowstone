package net.glowstone.block.itemtype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemTool extends ItemType {

    public int getMaxDurability() {
        return getMaterial().getMaxDurability();
    }

    @Override
    public final void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (onToolRightClick(player, target, face, holding, clickedLoc, hand)) {
            damageTool(player, holding);
        }
    }

    private void damageTool(GlowPlayer player, ItemStack holding) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        holding.setDurability((short) (holding.getDurability() + 1));
        if (holding.getDurability() == getMaxDurability() + 1) {
            EventFactory.callEvent(new PlayerItemBreakEvent(player, holding));
            holding.setAmount(0);
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
     * @return true if the tool's durability should be decreased, false otherwise
     */
    protected boolean onToolRightClick(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        // to be overridden in subclasses
        return false;
    }
}
