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
import org.bukkit.block.NoteBlock;
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
        NoteBlock noteBlock = (NoteBlock) block.getState();
        Note note = noteBlock.getNote();
        noteBlock.setNote(new Note(note.getId() == 24 ? 0 : note.getId() + 1));
        noteBlock.update();
        return noteBlock.play();
    }

    @Override
    public void leftClickBlock(GlowPlayer player, GlowBlock block, ItemStack holding) {
        ((NoteBlock) block.getState()).play();
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
    public void updatePhysics(GlowBlock me) {
        super.updatePhysics(me);
        if (me.isBlockIndirectlyPowered()) {
            ((NoteBlock) me.getState()).play();
        }
    }
}
