package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.NoteblockEntity;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockNote extends BlockType {

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new NoteblockEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        // TODO: 1.13 NoteBlock block data
        NoteBlock noteBlockData = (NoteBlock) block.getBlockData();
        Note note = noteBlockData.getNote();
        noteBlockData.setNote(new Note(note.getId() == 24 ? 0 : note.getId() + 1));
        block.setBlockData(noteBlockData);
        // TODO: 1.13 NoteBlock replacement for play()
        return false;
    }

    @Override
    public void leftClickBlock(GlowPlayer player, GlowBlock block, ItemStack holding) {
        // TODO: 1.13 NoteBlock replacement for play()
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock me) {
        super.updatePhysicsAfterEvent(me);
        if (me.isBlockIndirectlyPowered()) {
            // TODO: 1.13 NoteBlock replacement for play()
        }
    }
}
