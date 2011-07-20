package net.glowstone.io.blockstate;

import net.glowstone.block.GlowNoteBlock;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;

import java.util.Map;

public class NoteBlockStore extends BlockStateStore<GlowNoteBlock> {
    public NoteBlockStore() {
        super(GlowNoteBlock.class, "Music");
    }

    @Override
    public void load(GlowNoteBlock state, CompoundTag compound) {
        state.setRawNote(((ByteTag) compound.getValue().get("note")).getValue());
    }

    @Override
    public Map<String, Tag> save(GlowNoteBlock entity) {
        Map<String, Tag> map = super.save(entity);
        map.put("note", new ByteTag("note", entity.getRawNote()));
        return map;
    }
}
