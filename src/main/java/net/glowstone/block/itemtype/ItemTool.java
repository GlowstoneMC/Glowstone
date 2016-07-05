package net.glowstone.block.itemtype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemTool extends ItemType {

    private final int maxDurability;

    public ItemTool(int maxDurability) {
        setMaxStackSize(1);
        this.maxDurability = maxDurability;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    @Override
    public final void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding, Vector clickedLoc) {
            damageTool(player, holding, onToolRightClick(player, holding, target, face, clickedLoc));
    }

    public final void breakBlock(GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding, Vector clickedLoc) {
            damageTool(player, holding, onToolBreakBlock(player, holding, target, face, clickedLoc));
    }

    protected void damageTool(GlowPlayer player, ItemStack holding) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        holding.setDurability((short) (holding.getDurability() + 1));
        if (holding.getDurability() > maxDurability) {
            EventFactory.callEvent(new PlayerItemBreakEvent(player, holding));
            holding.setAmount(0);
        }
    }

    protected void damageTool(GlowPlayer player, ItemStack holding, int damageAmount) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        holding.setDurability((short) (holding.getDurability() + damageAmount));
        if (holding.getDurability() > maxDurability) {
            EventFactory.callEvent(new PlayerItemBreakEvent(player, holding));
            holding.setAmount(0);
        }
    }

    /**
     * Called when a player used (right clicked with) the tool.
     *
     * @param player     The player using the tool
     * @param tool       The tool
     * @param target     The block right clicked with the tool
     * @param face       The clicked BlockFace
     * @param clickedLoc The click location on the block
     * @return the amount the tool's durability should be increased by
     */
    protected int onToolRightClick(GlowPlayer player, ItemStack tool, GlowBlock target, BlockFace face, Vector clickedLoc) {
        // to be overridden in subclasses
        return 0;
    }


    /**
     * Called when a player breaks a block with the tool.
     *
     * @param player     The player using the tool
     * @param tool       The tool
     * @param target     The block left clicked with the tool
     * @param face       The clicked BlockFace
     * @param clickedLoc The click location on the block
     * @return the amount the tool's durability should be increased by
     */
    protected int onToolBreakBlock(GlowPlayer player, ItemStack tool, GlowBlock target, BlockFace face, Vector clickedLoc) {
        // to be overridden in subclasses
        return 0;
    }
}
