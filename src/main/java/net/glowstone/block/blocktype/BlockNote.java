package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.TENote;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockNote extends BlockType {
    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TENote(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        NoteBlock noteBlock = (NoteBlock) block.getState();
        Note note = noteBlock.getNote();
        System.out.println("Note: " + noteBlock.getNote());
        noteBlock.setNote(new Note(note.getId() == 24 ? 0 : note.getId() + 1));
        noteBlock.update();
        System.out.println("Note: " + noteBlock.getNote());
        return noteBlock.play();
    }

    @Override
    public void leftClickBlock(GlowPlayer player, GlowBlock block, ItemStack holding) {
        System.out.println("Note: " + ((NoteBlock) block.getState()).getNote());
        ((NoteBlock) block.getState()).play();
    }
}
