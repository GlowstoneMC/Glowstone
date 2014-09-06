package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.entity.TEDropper;
import net.glowstone.block.entity.TileEntity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockDropper extends BlockDispenser {

    public BlockDropper() {
        setDrops(new ItemStack(Material.DROPPER));
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEDropper(chunk.getBlock(cx, cy, cz));
    }

}
