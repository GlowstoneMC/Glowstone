package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.TENote;
import net.glowstone.block.entity.TileEntity;

public class BlockNote extends BlockType {
    @Override
    public TileEntity createTileEntity(GlowBlock block) {
        return new TENote(block);
    }
}
