package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.TENote;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;
import org.bukkit.util.Vector;

public class BlockNote extends BlockType {
    @Override
    public TileEntity createTileEntity(GlowBlock block) {
        return new TENote(block);
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        return ((NoteBlock) block.getState()).play();
    }
}
