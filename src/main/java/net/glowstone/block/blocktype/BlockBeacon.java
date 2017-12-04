package net.glowstone.block.blocktype;

import net.glowstone.block.entity.BeaconEntity;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.chunk.GlowChunk;
import org.bukkit.Material;

public class BlockBeacon extends BlockDirectDrops {

    public BlockBeacon() {
        super(Material.BEACON);
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new BeaconEntity(chunk.getBlock(cx, cy, cz));
    }
}
