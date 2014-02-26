package net.glowstone.io.blockstate;

import net.glowstone.block.entity.GlowNoteBlock;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;

import java.util.List;

public class NoteBlockStore extends BlockStateStore<GlowNoteBlock> {
    public NoteBlockStore() {
        super(GlowNoteBlock.class, "Music");
    }

    @Override
    public void load(GlowNoteBlock state, CompoundTag compound) {
        state.setRawNote(compound.get("note", ByteTag.class));
    }

    @Override
    public List<Tag> save(GlowNoteBlock entity) {
        List<Tag> map = super.save(entity);
        map.add(new ByteTag("note", entity.getRawNote()));
        return map;
    }
}
