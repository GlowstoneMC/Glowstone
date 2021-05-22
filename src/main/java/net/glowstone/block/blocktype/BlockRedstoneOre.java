package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

public class BlockRedstoneOre extends BlockRandomDrops {

    public BlockRedstoneOre() {
        super(Material.REDSTONE, 0, 3, 4, ToolType.IRON_PICKAXE);
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
        BlockData blockData = Bukkit.getServer().createBlockData(Material.REDSTONE_ORE);
        ((Lightable) blockData).setLit(true);
        EntityChangeBlockEvent changeBlockEvent = new EntityChangeBlockEvent(player, block,
            blockData);
        EventFactory.getInstance().callEvent(changeBlockEvent);
        if (!changeBlockEvent.isCancelled()) {
            GlowBlockState state = block.getState();
            state.setType(Material.REDSTONE_ORE);
            state.update(true);
        }
        return false;
    }
}
