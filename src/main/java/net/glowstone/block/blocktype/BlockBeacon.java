package net.glowstone.block.blocktype;

import net.glowstone.block.entity.TEBeacon;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.chunk.GlowChunk;
import org.bukkit.Material;

public class BlockBeacon extends BlockDirectDrops {
    public BlockBeacon() {
        super(Material.BEACON);
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEBeacon(chunk.getBlock(cx, cy, cz));
    }
}
