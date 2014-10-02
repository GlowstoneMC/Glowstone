package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.TEContainer;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Base BlockType for containers.
 */
public class BlockContainer extends BlockType {

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        TileEntity te = block.getTileEntity();
        if (te instanceof TEContainer) {
            // todo: animation?
            player.openInventory(((TEContainer) te).getInventory());
            return true;
        }
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block) {
        LinkedList<ItemStack> list = new LinkedList<>();

        list.add(new ItemStack(block.getType(), 1));

        for (ItemStack i : ((TEContainer) block.getTileEntity()).getInventory().getContents()) {
            if (i != null) {
                list.add(i);
            }
        }
        return list;
    }

}
