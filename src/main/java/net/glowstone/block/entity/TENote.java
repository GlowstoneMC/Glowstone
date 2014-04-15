package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowNoteBlock;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Note;

public class TENote extends TileEntity {

    private Note note = new Note(0);

    public TENote(GlowBlock block) {
        super(block);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        note = new Note(tag.getByte("note"));
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putByte("note", note.getId());
    }

    @Override
    public GlowBlockState getState() {
        return new GlowNoteBlock(block);
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
