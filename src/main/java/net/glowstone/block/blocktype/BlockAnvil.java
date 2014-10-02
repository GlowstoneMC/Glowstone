package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowAnvilInventory;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class BlockAnvil extends BlockType {

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        return player.openInventory(new GlowAnvilInventory(player)) != null;
    }
}
