package net.glowstone.block.entity;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.state.GlowNoteBlock;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Note;

public class NoteblockEntity extends BlockEntity {

    @Getter
    @Setter
    private Note note = new Note(0);

    public NoteblockEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:noteblock");
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
}
