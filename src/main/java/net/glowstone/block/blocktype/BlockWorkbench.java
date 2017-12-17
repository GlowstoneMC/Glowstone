package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class BlockWorkbench extends BlockType {

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        return player.openWorkbench(block.getLocation(), false) != null;
    }
}
