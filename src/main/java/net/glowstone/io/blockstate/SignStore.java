package net.glowstone.io.blockstate;

import net.glowstone.block.entity.GlowSign;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;

import java.util.List;

public class SignStore extends BlockStateStore<GlowSign> {

    public SignStore() {
        super(GlowSign.class, "Sign");
    }

    @Override
    public void load(GlowSign state, CompoundTag compound) {
        super.load(state, compound);
        state.setLine(0, compound.get("Text1", StringTag.class));
        state.setLine(1, compound.get("Text2", StringTag.class));
        state.setLine(2, compound.get("Text3", StringTag.class));
        state.setLine(3, compound.get("Text4", StringTag.class));
    }

    @Override
    public List<Tag> save(GlowSign state) {
        List<Tag> result = super.save(state);
        result.add(new StringTag("Text1", state.getLine(0)));
        result.add(new StringTag("Text2", state.getLine(1)));
        result.add(new StringTag("Text3", state.getLine(2)));
        result.add(new StringTag("Text4", state.getLine(3)));
        result.add(new ByteTag("Logic", (byte) 0)); // Not sure what this does
        return result;
    }
}
