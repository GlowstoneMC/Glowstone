package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowNoteBlock;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;
import org.bukkit.Note;

import java.util.List;

public class TENote extends TileEntity {

    private Note note;

    public TENote(GlowBlock block) {
        super(block);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        note = new Note(tag.get("note", ByteTag.class));
    }

    @Override
    public List<Tag> saveNbt() {
        List<Tag> result = super.saveNbt();
        result.add(new ByteTag("note", note.getId()));
        return result;
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
