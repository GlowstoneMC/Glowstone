package net.glowstone.block.itemtype;


import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemPickaxe extends ItemTool {

    public ItemPickaxe(int maxDurability) {
        super(maxDurability);
    }

    @Override
    protected int onToolBreakBlock(GlowPlayer player, ItemStack tool, GlowBlock target, BlockFace face, Vector clickedLoc) {
        return 1; //TODO: Make this value change according to the block broken and the type (wood, iron, diamod) of pick used
    }
}
